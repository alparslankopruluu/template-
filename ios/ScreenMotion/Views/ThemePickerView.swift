import SwiftUI

struct ThemePickerView: View {
    @EnvironmentObject private var store: ThemeStore
    @State private var exportStatus: String?
    @State private var exporting = false

    private let columns = [
        GridItem(.flexible(), spacing: 10),
        GridItem(.flexible(), spacing: 10),
        GridItem(.flexible(), spacing: 10)
    ]

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    InteractiveSceneView(type: store.config.type)
                        .frame(height: 410)
                        .clipShape(RoundedRectangle(cornerRadius: 34, style: .continuous))
                        .overlay {
                            RoundedRectangle(cornerRadius: 34)
                                .stroke(.white.opacity(0.12), lineWidth: 1)
                        }

                    Text("Tema seç")
                        .font(.title2.bold())

                    LazyVGrid(columns: columns, spacing: 10) {
                        ForEach(ThemeType.allCases) { theme in
                            Button {
                                store.config.type = theme
                            } label: {
                                VStack(spacing: 7) {
                                    Text(theme.emoji)
                                        .font(.system(size: 34))
                                    Text(theme.title)
                                        .font(.caption.bold())
                                        .lineLimit(1)
                                }
                                .frame(maxWidth: .infinity)
                                .frame(height: 86)
                                .background(
                                    store.config.type == theme
                                    ? Color.indigo.opacity(0.45)
                                    : Color.white.opacity(0.07)
                                )
                                .clipShape(RoundedRectangle(cornerRadius: 18))
                            }
                            .buttonStyle(.plain)
                        }
                    }

                    Toggle("Gyroscope", isOn: $store.config.gyroEnabled)
                    Toggle("Dokunma / sürükleme", isOn: $store.config.touchEnabled)

                    VStack(alignment: .leading) {
                        Text("Hareket yoğunluğu")
                            .font(.subheadline.weight(.semibold))
                        Slider(value: $store.config.parallaxStrength, in: 0.4...1.6)
                    }

                    Button {
                        exportLivePhoto()
                    } label: {
                        HStack {
                            if exporting { ProgressView().tint(.white) }
                            Text(exporting ? "Hazırlanıyor…" : "iOS Kilit Ekranı Animasyonu Oluştur")
                        }
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.indigo)
                    .disabled(exporting)

                    if let exportStatus {
                        Text(exportStatus)
                            .font(.footnote)
                            .foregroundStyle(.secondary)
                    }

                    Text("iOS, üçüncü taraf uygulamalara Android benzeri sürekli çalışan wallpaper motoru vermiyor. Uygulama içindeki tilt/touch demo gerçektir; kilit ekranı için çıktı Live Photo olarak Photos'a kaydedilir.")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }
                .padding(18)
            }
            .background(Color.black)
            .navigationTitle("ScreenMotion")
        }
    }

    private func exportLivePhoto() {
        exporting = true
        exportStatus = nil
        let config = store.config
        Task {
            do {
                let pair = try await LivePhotoExporter().export(
                    config: config,
                    size: CGSize(width: 1080, height: 1920),
                    duration: 3.0,
                    fps: 30
                )
                try await PhotoLibrarySaver.saveLivePhoto(photoURL: pair.photoURL, videoURL: pair.videoURL)
                await MainActor.run {
                    exportStatus = "Live Photo Photos'a kaydedildi. Ayarlar > Duvar Kağıdı bölümünden seçebilirsin."
                    exporting = false
                }
            } catch {
                await MainActor.run {
                    exportStatus = "Çıktı oluşturulamadı: \(error.localizedDescription)"
                    exporting = false
                }
            }
        }
    }
}
