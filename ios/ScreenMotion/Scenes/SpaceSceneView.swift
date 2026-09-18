import SwiftUI

struct SpaceSceneView: View {
    @ObservedObject var motion: MotionManager
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            TimelineView(.animation) { timeline in
                let t = timeline.date.timeIntervalSinceReferenceDate

                ZStack {
                    Image("space")
                        .resizable()
                        .scaledToFill()
                        .frame(width: proxy.size.width * 1.14, height: proxy.size.height * 1.14)
                        .offset(
                            x: -CGFloat(motion.roll) * 40 + drag.width * 0.12,
                            y: -CGFloat(motion.pitch) * 22 + drag.height * 0.06
                        )

                    Canvas { context, size in
                        for i in 0..<110 {
                            let seed = pseudo(Double(i) * 12.9898)
                            let seed2 = pseudo(Double(i) * 31.726)
                            let z = 0.35 + pseudo(Double(i) * 7.77) * 1.55
                            let drift = (t * (3.0 + z * 4.0)).truncatingRemainder(dividingBy: size.height + 30)
                            let x = seed * size.width + CGFloat(motion.roll) * 20 * z + drag.width * 0.08 * z
                            let y = (seed2 * size.height + drift).truncatingRemainder(dividingBy: size.height)
                            let r = 0.7 + z * 1.2
                            context.fill(
                                Path(ellipseIn: CGRect(x: x, y: y, width: r, height: r)),
                                with: .color(.white.opacity(0.45 + z * 0.18))
                            )
                        }
                    }

                    Text("🚀")
                        .font(.system(size: 72))
                        .shadow(color: .cyan.opacity(0.75), radius: 24)
                        .offset(
                            x: CGFloat(motion.roll) * proxy.size.width * 0.18 + drag.width * 0.35,
                            y: CGFloat(motion.pitch) * 20 + drag.height * 0.12 + proxy.size.height * 0.18
                        )
                        .rotationEffect(.degrees(-motion.roll * 9))
                }
            }
            .frame(width: proxy.size.width, height: proxy.size.height)
            .clipped()
            .gesture(
                DragGesture()
                    .onChanged { drag = $0.translation }
                    .onEnded { _ in withAnimation(.spring) { drag = .zero } }
            )
        }
    }

    private func pseudo(_ x: Double) -> Double {
        let value = sin(x) * 43758.5453
        return value - floor(value)
    }
}
