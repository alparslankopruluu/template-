import SwiftUI

@main
struct ScreenMotionApp: App {
    @StateObject private var store = ThemeStore()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(store)
                .preferredColorScheme(.dark)
        }
    }
}
