import CoreMotion
import Foundation

@MainActor
final class MotionManager: ObservableObject {
    private let manager = CMMotionManager()

    @Published private(set) var roll = 0.0
    @Published private(set) var pitch = 0.0

    private var filteredRoll = 0.0
    private var filteredPitch = 0.0

    func start() {
        guard manager.isDeviceMotionAvailable else { return }
        manager.deviceMotionUpdateInterval = 1.0 / 60.0
        manager.startDeviceMotionUpdates(
            using: .xArbitraryZVertical,
            to: .main
        ) { [weak self] motion, _ in
            guard let self, let attitude = motion?.attitude else { return }
            Task { @MainActor in
                self.filteredRoll += 0.12 * (attitude.roll - self.filteredRoll)
                self.filteredPitch += 0.12 * (attitude.pitch - self.filteredPitch)
                self.roll = self.normalize(self.filteredRoll, maxDegrees: 25)
                self.pitch = self.normalize(self.filteredPitch, maxDegrees: 20)
            }
        }
    }

    func stop() {
        manager.stopDeviceMotionUpdates()
    }

    private func normalize(_ radians: Double, maxDegrees: Double) -> Double {
        let limit = maxDegrees * .pi / 180
        return min(max(radians / limit, -1), 1)
    }
}
