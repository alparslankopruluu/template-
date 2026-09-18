import SwiftUI

struct NatureSceneView: View {
    @ObservedObject var motion: MotionManager
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            TimelineView(.animation) { timeline in
                let t = timeline.date.timeIntervalSinceReferenceDate

                ZStack {
                    Image("nature")
                        .resizable()
                        .scaledToFill()
                        .frame(width: proxy.size.width * 1.13, height: proxy.size.height * 1.13)
                        .offset(
                            x: -CGFloat(motion.roll) * 36 + drag.width * 0.1,
                            y: -CGFloat(motion.pitch) * 20
                        )

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
