import SwiftUI

struct AquariumSceneView: View {
    @ObservedObject var motion: MotionManager
    @State private var touchPoint: CGPoint?
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            TimelineView(.animation) { timeline in
                let t = timeline.date.timeIntervalSinceReferenceDate

                ZStack {
                    LinearGradient(
                        colors: [
                            Color(red: 0.04, green: 0.45, blue: 0.78),
                            Color(red: 0.00, green: 0.19, blue: 0.38),
                            Color(red: 0.00, green: 0.05, blue: 0.12)
                        ],
                        startPoint: .top,
                        endPoint: .bottom
                    )

                    Canvas { context, size in
                        for i in 0..<7 {
                            let x = size.width * (0.08 + CGFloat(i) * 0.145)
                            let h = size.height * (0.10 + CGFloat(i % 3) * 0.045)
                            var coral = Path()
                            coral.move(to: CGPoint(x: x, y: size.height))
                            coral.addCurve(
                                to: CGPoint(x: x + 20, y: size.height - h),
                                control1: CGPoint(x: x - 10, y: size.height - h * 0.45),
                                control2: CGPoint(x: x + 28, y: size.height - h * 0.70)
                            )
                            context.stroke(
                                coral,
                                with: .color([Color.orange, .pink, .purple, .mint][i % 4].opacity(0.72)),
                                lineWidth: 7
                            )
                        }
                    }

                    ForEach(0..<10, id: \.self) { index in
                        let speed = 0.12 + Double(index % 4) * 0.025
                        let direction = index.isMultiple(of: 2) ? 1.0 : -1.0
                        let phase = Double(index) * 0.63
                        let xBase = (sin(t * speed + phase) * 0.42 + 0.5) * proxy.size.width
                        let yBase = (0.18 + Double(index) * 0.068) * proxy.size.height
                        let touchEffectX: CGFloat = touchPoint.map { ($0.x - proxy.size.width / 2) * 0.08 } ?? 0
                        let touchEffectY: CGFloat = touchPoint.map { ($0.y - proxy.size.height / 2) * 0.05 } ?? 0

                        Text(["🐠", "🐟", "🐡"][index % 3])
                            .font(.system(size: 28 + CGFloat(index % 3) * 7))
                            .scaleEffect(x: direction, y: 1)
                            .position(
                                x: xBase + CGFloat(motion.roll) * 30 + touchEffectX,
                                y: yBase + CGFloat(motion.pitch) * 16 + touchEffectY
                            )
                    }

                    ForEach(0..<18, id: \.self) { index in
                        let y = proxy.size.height - CGFloat((t * (26 + Double(index % 5) * 5) + Double(index) * 37).truncatingRemainder(dividingBy: proxy.size.height))
                        Circle()
                            .stroke(.white.opacity(0.35), lineWidth: 1)
                            .frame(width: 5 + CGFloat(index % 4) * 3, height: 5 + CGFloat(index % 4) * 3)
                            .position(x: CGFloat((index * 47) % max(Int(proxy.size.width), 1)), y: y)
                    }
                }
            }
            .frame(width: proxy.size.width, height: proxy.size.height)
            .clipped()
            .contentShape(Rectangle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged {
                        touchPoint = $0.location
                        drag = $0.translation
                    }
                    .onEnded { _ in
                        touchPoint = nil
                        withAnimation(.spring) { drag = .zero }
                    }
            )
        }
    }
}
