import SwiftUI

struct RootView: View {
    @AppStorage("screenmotion.onboarding.complete") private var onboardingComplete = false

    var body: some View {
        if onboardingComplete {
            ThemePickerView()
        } else {
            OnboardingView {
                onboardingComplete = true
            }
        }
    }
}
