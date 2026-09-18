import SwiftUI

struct NatureSceneView: View {
    @ObservedObject var motion: MotionManager
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            TimelineView(.animation) { timeline in
                let t = timeline.date.timeIntervalSinceReferenceDate

                ZStack {
                    LinearGradient(
                        colors: [
                            Color(red: 0.40, green: 0.67, blue: 0.90),
                            Color(red: 0.16, green: 0.34, blue: 0.32),
                            Color(red: 0.04, green: 0.09, blue: 0.08)
                        ],
                        startPoint: .top,
                        endPoint: .bottom
                    )

                    Canvas { context, size in
                        let shift = -CGFloat(motion.roll) * 28 + drag.width * 0.08
                        var mountain = Path()
                        mountain.move(to: CGPoint(x: -40 + shift, y: size.height * 0.62))
                        mountain.addLine(to: CGPoint(x: size.width * 0.28 + shift, y: size.height * 0.25))
                        mountain.addLine(to: CGPoint(x: size.width * 0.48 + shift, y: size.height * 0.55))
                        mountain.addLine(to: CGPoint(x: size.width * 0.70 + shift, y: size.height * 0.20))
                        mountain.addLine(to: CGPoint(x: size.width + 50 + shift, y: size.height * 0.60))
                        mountain.closeSubpath()
                        context.fill(mountain, with: .color(Color.black.opacity(0.48)))

                        let lake = CGRect(x: 0, y: size.height * 0.61, width: size.width, height: size.height * 0.39)
                        context.fill(Path(lake), with: .color(.blue.opacity(0.20)))
                    }

                    ForEach(0..<7, id: \.self) { i in
                        Text("⌁")
                            .font(.system(size: 22 + CGFloat(i % 3) * 3, weight: .bold))
                            .foregroundStyle(.black.opacity(0.55))
                            .position(
                                x: CGFloat((t * (12 + Double(i)) + Double(i * 83)).truncatingRemainder(dividingBy: proxy.size.width + 80)) - 40,
                                y: proxy.size.height * (0.16 + CGFloat(i % 4) * 0.055)
                            )
                    }
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
}
