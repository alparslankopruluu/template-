import SwiftUI

struct InteractiveSceneView: View {
    let type: ThemeType
    @StateObject private var motion = MotionManager()

    var body: some View {
        Group {
            switch type {
            case .space:
                SpaceSceneView(motion: motion)
            case .aquarium:
                AquariumSceneView(motion: motion)
            case .nature:
                NatureSceneView(motion: motion)
            default:
                VehicleSceneView(type: type, motion: motion)
            }
        }
        .onAppear { motion.start() }
        .onDisappear { motion.stop() }
    }
}
