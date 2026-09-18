import AVFoundation
import CoreGraphics
import ImageIO
import UniformTypeIdentifiers

struct LivePhotoPair {
    let photoURL: URL
    let videoURL: URL
}

final class LivePhotoExporter {
    func export(
        config: ThemeConfig,
        size: CGSize,
        duration: Double,
        fps: Int32
    ) async throws -> LivePhotoPair {
        let identifier = UUID().uuidString
        let directory = FileManager.default.temporaryDirectory
            .appendingPathComponent("screenmotion-\(UUID().uuidString)", isDirectory: true)
        try FileManager.default.createDirectory(at: directory, withIntermediateDirectories: true)

        let photoURL = directory.appendingPathComponent("wallpaper.jpg")
        let videoURL = directory.appendingPathComponent("wallpaper.mov")
        let stillTime = duration * 0.5

        let still = try WallpaperFrameRenderer.render(config: config, size: size, time: stillTime)
        try writeLivePhotoJPEG(still, identifier: identifier, to: photoURL)
        try await writeVideo(
            config: config,
            identifier: identifier,
            size: size,
            duration: duration,
            fps: fps,
            stillTime: stillTime,
            to: videoURL
        )

        return LivePhotoPair(photoURL: photoURL, videoURL: videoURL)
    }

    private func writeLivePhotoJPEG(
        _ image: CGImage,
        identifier: String,
        to url: URL
    ) throws {
        guard let destination = CGImageDestinationCreateWithURL(
            url as CFURL,
            UTType.jpeg.identifier as CFString,
            1,
            nil
        ) else {
            throw ExportError.imageWriteFailed
        }

        let metadata: [CFString: Any] = [
            kCGImagePropertyMakerAppleDictionary: ["17": identifier]
        ]
        CGImageDestinationAddImage(destination, image, metadata as CFDictionary)
        guard CGImageDestinationFinalize(destination) else {
            throw ExportError.imageWriteFailed
        }
    }

    private func writeVideo(
        config: ThemeConfig,
        identifier: String,
        size: CGSize,
        duration: Double,
        fps: Int32,
        stillTime: Double,
        to url: URL
    ) async throws {
        let writer = try AVAssetWriter(outputURL: url, fileType: .mov)

        let videoInput = AVAssetWriterInput(
            mediaType: .video,
            outputSettings: [
                AVVideoCodecKey: AVVideoCodecType.hevc,
                AVVideoWidthKey: Int(size.width),
                AVVideoHeightKey: Int(size.height)
            ]
        )
        videoInput.expectsMediaDataInRealTime = false

        let adaptor = AVAssetWriterInputPixelBufferAdaptor(
            assetWriterInput: videoInput,
            sourcePixelBufferAttributes: [
                kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA,
                kCVPixelBufferWidthKey as String: Int(size.width),
                kCVPixelBufferHeightKey as String: Int(size.height)
            ]
        )

        guard writer.canAdd(videoInput) else { throw ExportError.writerFailed }
        writer.add(videoInput)

        let idItem = AVMutableMetadataItem()
        idItem.identifier = .quickTimeMetadataContentIdentifier
        idItem.value = identifier as NSString
        idItem.dataType = kCMMetadataBaseDataType_UTF8 as String
        writer.metadata = [idItem]

        let metadataInput = try makeStillImageMetadataInput(writer: writer)
        let metadataAdaptor = AVAssetWriterInputMetadataAdaptor(assetWriterInput: metadataInput)

        guard writer.startWriting() else { throw writer.error ?? ExportError.writerFailed }
        writer.startSession(atSourceTime: .zero)

        let totalFrames = Int(duration * Double(fps))
        for frame in 0..<totalFrames {
            while !videoInput.isReadyForMoreMediaData {
                try await Task.sleep(for: .milliseconds(3))
            }

            guard let pool = adaptor.pixelBufferPool else {
                throw ExportError.pixelBufferFailed
            }
            var pixelBuffer: CVPixelBuffer?
            CVPixelBufferPoolCreatePixelBuffer(nil, pool, &pixelBuffer)
            guard let pixelBuffer else { throw ExportError.pixelBufferFailed }

            let seconds = Double(frame) / Double(fps)
            let cgImage = try WallpaperFrameRenderer.render(config: config, size: size, time: seconds)
            try draw(cgImage, into: pixelBuffer, size: size)

            let time = CMTime(value: CMTimeValue(frame), timescale: fps)
            guard adaptor.append(pixelBuffer, withPresentationTime: time) else {
                throw writer.error ?? ExportError.writerFailed
            }
        }

        let marker = AVMutableMetadataItem()
        marker.keySpace = .quickTimeMetadata
        marker.key = "com.apple.quicktime.still-image-time" as NSString
        marker.value = NSNumber(value: Int8(0))
        marker.dataType = kCMMetadataBaseDataType_SInt8 as String

        let markerTime = CMTime(seconds: stillTime, preferredTimescale: 600)
        let markerRange = CMTimeRange(
            start: markerTime,
            duration: CMTime(value: 1, timescale: fps)
        )
        metadataAdaptor.append(AVTimedMetadataGroup(items: [marker], timeRange: markerRange))

        videoInput.markAsFinished()
        metadataInput.markAsFinished()
        await writer.finishWriting()

        guard writer.status == .completed else {
            throw writer.error ?? ExportError.writerFailed
        }
    }

    private func makeStillImageMetadataInput(writer: AVAssetWriter) throws -> AVAssetWriterInput {
        let specification: [CFString: Any] = [
            kCMMetadataFormatDescriptionMetadataSpecificationKey_Identifier:
                "mdta/com.apple.quicktime.still-image-time",
            kCMMetadataFormatDescriptionMetadataSpecificationKey_DataType:
                kCMMetadataBaseDataType_SInt8
        ]

        var formatDescription: CMMetadataFormatDescription?
        let status = CMMetadataFormatDescriptionCreateWithMetadataSpecifications(
            allocator: kCFAllocatorDefault,
            metadataType: kCMMetadataFormatType_Boxed,
            metadataSpecifications: [specification] as CFArray,
            formatDescriptionOut: &formatDescription
        )

        guard status == noErr, let formatDescription else {
            throw ExportError.writerFailed
        }

        let input = AVAssetWriterInput(
            mediaType: .metadata,
            outputSettings: nil,
            sourceFormatHint: formatDescription
        )
        guard writer.canAdd(input) else { throw ExportError.writerFailed }
        writer.add(input)
        return input
    }

    private func draw(
        _ image: CGImage,
        into pixelBuffer: CVPixelBuffer,
        size: CGSize
    ) throws {
        CVPixelBufferLockBaseAddress(pixelBuffer, [])
        defer { CVPixelBufferUnlockBaseAddress(pixelBuffer, []) }

        guard let base = CVPixelBufferGetBaseAddress(pixelBuffer) else {
            throw ExportError.pixelBufferFailed
        }

        let bytesPerRow = CVPixelBufferGetBytesPerRow(pixelBuffer)
        guard let context = CGContext(
            data: base,
            width: Int(size.width),
            height: Int(size.height),
            bitsPerComponent: 8,
            bytesPerRow: bytesPerRow,
            space: CGColorSpaceCreateDeviceRGB(),
            bitmapInfo: CGImageAlphaInfo.premultipliedFirst.rawValue
        ) else {
            throw ExportError.pixelBufferFailed
        }

        context.translateBy(x: 0, y: size.height)
        context.scaleBy(x: 1, y: -1)
        context.draw(image, in: CGRect(origin: .zero, size: size))
    }
}
