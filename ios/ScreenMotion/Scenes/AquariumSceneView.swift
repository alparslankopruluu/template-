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
                    Image("aquarium")
                        .resizable()
                        .scaledToFill()
                        .frame(width: proxy.size.width * 1.12, height: proxy.size.height * 1.12)
                        .offset(
                            x: -CGFloat(motion.roll) * 26 + drag.width * 0.08,
                            y: -CGFloat(motion.pitch) * 15
                        )

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
