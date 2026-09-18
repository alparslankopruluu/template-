import UIKit

enum WallpaperFrameRenderer {
    static func render(
        config: ThemeConfig,
        size: CGSize,
        time: Double
    ) throws -> CGImage {
        let renderer = UIGraphicsImageRenderer(size: size)

        let image = renderer.image { context in
            let rect = CGRect(origin: .zero, size: size)
            UIColor.black.setFill()
            context.fill(rect)

            if let background = UIImage(named: config.type.assetName) {
                drawAspectFill(background, in: rect)
            }

            let parallax = sin(time * 2.1) * 0.12 * config.parallaxStrength
            let emoji = config.type.emoji as NSString
            let fontSize = min(size.width, size.height) * (config.type == .aquarium ? 0.11 : 0.16)
            let attributes: [NSAttributedString.Key: Any] = [
                .font: UIFont.systemFont(ofSize: fontSize),
                .foregroundColor: UIColor.white
            ]
            let emojiSize = emoji.size(withAttributes: attributes)
            let x = size.width * (0.5 + parallax) - emojiSize.width / 2
            let y = size.height * (config.type.isVehicle ? 0.62 : 0.48) - emojiSize.height / 2
            emoji.draw(at: CGPoint(x: x, y: y), withAttributes: attributes)
        }

        guard let cgImage = image.cgImage else {
            throw ExportError.frameRenderFailed
        }
        return cgImage
    }

    private static func drawAspectFill(_ image: UIImage, in rect: CGRect) {
        let imageRatio = image.size.width / image.size.height
        let targetRatio = rect.width / rect.height
        var drawRect = rect

        if imageRatio > targetRatio {
            let width = rect.height * imageRatio
            drawRect.origin.x -= (width - rect.width) / 2
            drawRect.size.width = width
        } else {
            let height = rect.width / imageRatio
            drawRect.origin.y -= (height - rect.height) / 2
            drawRect.size.height = height
        }

        image.draw(in: drawRect)
    }
}

enum ExportError: LocalizedError {
    case frameRenderFailed
    case pixelBufferFailed
    case writerFailed
    case imageWriteFailed
    case photoPermissionDenied

    var errorDescription: String? {
        switch self {
        case .frameRenderFailed: "Kare render edilemedi."
        case .pixelBufferFailed: "Video buffer oluşturulamadı."
        case .writerFailed: "Video yazılamadı."
        case .imageWriteFailed: "Live Photo karesi yazılamadı."
        case .photoPermissionDenied: "Fotoğraflar izni verilmedi."
        }
    }
}
