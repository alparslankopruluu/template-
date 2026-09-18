import SwiftUI

struct VehicleSceneView: View {
    let type: ThemeType
    @ObservedObject var motion: MotionManager
    @State private var drag: CGSize = .zero

    var body: some View {
        GeometryReader { proxy in
            ZStack {
                LinearGradient(
                    colors: palette,
                    startPoint: .top,
                    endPoint: .bottom
                )

                Canvas { context, size in
                    drawWorld(context: &context, size: size)
                }
                .offset(
                    x: -CGFloat(motion.roll) * 22 + drag.width * 0.06,
                    y: -CGFloat(motion.pitch) * 10
                )

                Text(type.emoji)
                    .font(.system(size: type == .bicycle ? 72 : 92))
                    .shadow(color: glowColor.opacity(0.75), radius: 24)
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

    private var palette: [Color] {
        switch type {
        case .spaceship, .airplane, .helicopter:
            [Color(red: 0.02, green: 0.03, blue: 0.10), Color(red: 0.08, green: 0.12, blue: 0.32), .black]
        case .tractor, .bicycle:
            [Color(red: 0.45, green: 0.68, blue: 0.90), Color(red: 0.15, green: 0.28, blue: 0.20), Color(red: 0.05, green: 0.08, blue: 0.05)]
        case .tank:
            [Color(red: 0.34, green: 0.32, blue: 0.24), Color(red: 0.16, green: 0.18, blue: 0.12), .black]
        default:
            [Color(red: 0.08, green: 0.04, blue: 0.20), Color(red: 0.12, green: 0.02, blue: 0.16), .black]
        }
    }

    private var glowColor: Color {
        type == .spaceship ? .cyan : .purple
    }

    private func drawWorld(context: inout GraphicsContext, size: CGSize) {
        if [.car, .truck, .bus, .tractor, .tank, .bicycle].contains(type) {
            var road = Path()
            road.move(to: CGPoint(x: size.width * 0.20, y: size.height))
            road.addLine(to: CGPoint(x: size.width * 0.44, y: size.height * 0.46))
            road.addLine(to: CGPoint(x: size.width * 0.56, y: size.height * 0.46))
            road.addLine(to: CGPoint(x: size.width * 0.80, y: size.height))
            road.closeSubpath()
            context.fill(road, with: .color(.black.opacity(0.52)))

            for i in 0..<9 {
                let y = size.height * (0.52 + Double(i) * 0.065)
                let width = 2.0 + Double(i) * 0.8
                context.fill(
                    Path(CGRect(x: size.width / 2 - width / 2, y: y, width: width, height: 24)),
                    with: .color(.white.opacity(0.45))
                )
            }
        } else {
            for i in 0..<38 {
                let x = Double((i * 53) % max(Int(size.width), 1))
                let y = Double((i * 91) % max(Int(size.height), 1))
                context.fill(
                    Path(ellipseIn: CGRect(x: x, y: y, width: i % 5 == 0 ? 3 : 1.5, height: i % 5 == 0 ? 3 : 1.5)),
                    with: .color(.white.opacity(0.55))
                )
            }
        }
    }
}
