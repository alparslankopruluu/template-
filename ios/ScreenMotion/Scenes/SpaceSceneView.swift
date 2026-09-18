import SwiftUI

struct SpaceSceneView: View {
    @ObservedObject var motion: MotionManager
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            TimelineView(.animation) { timeline in
                let t = timeline.date.timeIntervalSinceReferenceDate

                ZStack {
                    RadialGradient(
                        colors: [
                            Color(red: 0.20, green: 0.08, blue: 0.42),
                            Color(red: 0.03, green: 0.04, blue: 0.13),
                            .black
                        ],
                        center: .center,
                        startRadius: 10,
                        endRadius: 420
                    )

                    Canvas { context, size in
                        for i in 0..<130 {
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

                        let planetRect = CGRect(
                            x: size.width * 0.58 - CGFloat(motion.roll) * 10,
                            y: size.height * 0.16 - CGFloat(motion.pitch) * 8,
                            width: size.width * 0.55,
                            height: size.width * 0.55
                        )
                        context.fill(
                            Path(ellipseIn: planetRect),
                            with: .radialGradient(
                                Gradient(colors: [.cyan.opacity(0.65), .indigo.opacity(0.8), .black]),
                                center: CGPoint(x: planetRect.midX * 0.9, y: planetRect.midY * 0.8),
                                startRadius: 0,
                                endRadius: planetRect.width * 0.55
                            )
                        )
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
