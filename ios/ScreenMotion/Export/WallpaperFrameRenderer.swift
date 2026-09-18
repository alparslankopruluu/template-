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
            let cg = context.cgContext

            let colors: [CGColor]
            switch config.type {
            case .aquarium:
                colors = [
                    UIColor(red: 0.03, green: 0.48, blue: 0.80, alpha: 1).cgColor,
                    UIColor(red: 0.00, green: 0.06, blue: 0.16, alpha: 1).cgColor
                ]
            case .space, .spaceship, .airplane, .helicopter:
                colors = [
                    UIColor(red: 0.16, green: 0.06, blue: 0.38, alpha: 1).cgColor,
                    UIColor.black.cgColor
                ]
            case .nature, .tractor, .bicycle:
                colors = [
                    UIColor(red: 0.42, green: 0.68, blue: 0.90, alpha: 1).cgColor,
                    UIColor(red: 0.04, green: 0.12, blue: 0.07, alpha: 1).cgColor
                ]
            default:
                colors = [
                    UIColor(red: 0.10, green: 0.03, blue: 0.22, alpha: 1).cgColor,
                    UIColor.black.cgColor
                ]
            }

            if let gradient = CGGradient(
                colorsSpace: CGColorSpaceCreateDeviceRGB(),
                colors: colors as CFArray,
                locations: [0, 1]
            ) {
                cg.drawLinearGradient(
                    gradient,
                    start: CGPoint(x: size.width / 2, y: 0),
                    end: CGPoint(x: size.width / 2, y: size.height),
                    options: []
                )
            }

            if config.type == .space || config.type == .spaceship || config.type == .airplane {
                UIColor.white.withAlphaComponent(0.55).setFill()
                for i in 0..<90 {
                    let x = CGFloat((i * 61) % max(Int(size.width), 1))
                    let y = CGFloat((i * 97) % max(Int(size.height), 1))
                    cg.fillEllipse(in: CGRect(x: x, y: y, width: i % 7 == 0 ? 4 : 2, height: i % 7 == 0 ? 4 : 2))
                }
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
