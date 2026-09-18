import SwiftUI

struct OnboardingView: View {
    let onFinish: () -> Void

    @State private var page = 0
    @State private var demoType: ThemeType = .car

    private let titles = [
        "Telefonunu eğ. Sahne hareket etsin.",
        "Dokun ve sürükle. Dünya sana cevap versin.",
        "Her ekrana yaşayan bir tema."
    ]

    private let subtitles = [
        "Gyroscope ile araç, kamera ve arka plan gerçek zamanlı tepki verir.",
        "Balıkları yönlendir, yıldızları sürükle, araçları hareket ettir.",
        "Araçlar, uzay, akvaryum ve doğa temalarını keşfet."
    ]

    var body: some View {
        ZStack {
            LinearGradient(
                colors: [Color.black, Color(red: 0.06, green: 0.09, blue: 0.22), Color.black],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 18) {
                HStack {
                    Text("ScreenMotion")
                        .font(.title2.bold())
                    Spacer()
                    Text("\(page + 1)/3")
                        .foregroundStyle(.secondary)
                }

                InteractiveSceneView(type: demoType)
                    .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
                    .overlay {
                        RoundedRectangle(cornerRadius: 32)
                            .stroke(.white.opacity(0.12), lineWidth: 1)
                    }
                    .frame(maxHeight: 430)

                VStack(alignment: .leading, spacing: 10) {
                    Text(titles[page])
                        .font(.system(size: 31, weight: .black, design: .rounded))
                    Text(subtitles[page])
                        .foregroundStyle(.secondary)
                        .font(.body)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                HStack(spacing: 6) {
                    ForEach(0..<3, id: \.self) { index in
                        Capsule()
                            .fill(index == page ? Color.indigo : Color.white.opacity(0.18))
                            .frame(width: index == page ? 28 : 8, height: 8)
                    }
                }

                Button {
                    if page < 2 {
                        page += 1
                        demoType = [.car, .aquarium, .spaceship][page]
                    } else {
                        onFinish()
                    }
                } label: {
                    Text(page < 2 ? "Devam" : "Temaları Keşfet")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                }
                .buttonStyle(.borderedProminent)
                .tint(.indigo)
            }
            .padding(20)
        }
    }
}
