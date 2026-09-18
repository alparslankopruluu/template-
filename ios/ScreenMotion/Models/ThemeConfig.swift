import Foundation

struct ThemeConfig: Equatable, Codable {
    var type: ThemeType = .spaceship
    var gyroEnabled = true
    var touchEnabled = true
    var speed = 1.0
    var particleAmount = 48
    var parallaxStrength = 1.0
    var fps = 30
}

@MainActor
final class ThemeStore: ObservableObject {
    @Published var config = ThemeConfig()
}
