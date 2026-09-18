import SwiftUI

struct VehicleSceneView: View {
    let type: ThemeType
    @ObservedObject var motion: MotionManager
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            ZStack {
                Image(type.assetName)
                    .resizable()
                    .scaledToFill()
                    .frame(width: proxy.size.width * 1.12, height: proxy.size.height * 1.12)
                    .offset(
                        x: -CGFloat(motion.roll) * 32,
                        y: -CGFloat(motion.pitch) * 18
                    )

                LinearGradient(
                    colors: [.clear, .black.opacity(0.55)],
                    startPoint: .center,
                    endPoint: .bottom
                )

                Text(type.emoji)
                    .font(.system(size: type == .bicycle ? 72 : 92))
                    .shadow(color: .cyan.opacity(0.55), radius: 24)
                    .offset(
                        x: CGFloat(motion.roll) * proxy.size.width * 0.22 + drag.width * 0.45,
                        y: CGFloat(motion.pitch) * 26 + drag.height * 0.15 + proxy.size.height * 0.16
                    )
                    .rotationEffect(.degrees(-motion.roll * 7))

                VStack {
                    Spacer()
                    Text("Telefonu eğ veya aracı sürükle")
                        .font(.caption.weight(.semibold))
                        .padding(.horizontal, 14)
                        .padding(.vertical, 9)
                        .background(.black.opacity(0.45), in: Capsule())
                        .padding(.bottom, 18)
                }
            }
            .frame(width: proxy.size.width, height: proxy.size.height)
            .clipped()
            .contentShape(Rectangle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { drag = $0.translation }
                    .onEnded { _ in
                        withAnimation(.spring(response: 0.35, dampingFraction: 0.72)) {
                            drag = .zero
                        }
                    }
            )
        }
    }
}
