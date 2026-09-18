# ScreenMotion

Native iOS + Android experiment for living lock/home screen themes.

## What is implemented

### Android
- Kotlin + Jetpack Compose theme picker and interactive onboarding
- real `WallpaperService`
- rotation-vector / gyro input
- touch / drag input
- Canvas render loop with visibility-aware battery handling
- aquarium fish + bubbles
- space starfield + spacecraft
- nature parallax
- car, truck, bus, tractor, tank, helicopter, airplane and bicycle scenes

### iOS
- SwiftUI theme picker and interactive onboarding
- CoreMotion tilt preview
- drag interaction
- aquarium, space, nature and vehicle previews
- Live Photo render/export pipeline
- Photos library save flow

> iOS does not expose an Android-style third-party live-wallpaper engine. Tilt/touch interaction is real while ScreenMotion is open; lock-screen output is exported as a Live Photo.

## Repository

- `android/` Android Studio project
- `ios/` Swift/XcodeGen project
- `assets/wallpapers/` shared prototype backgrounds
- `docs/` architecture, platform limitations and asset roadmap

## Android

Open `android/` in Android Studio, sync and run.

Choose a theme and tap **Canlı Duvar Kağıdını Uygula**.

The wallpaper engine reads the saved theme and starts motion/touch rendering only while visible.

## iOS

The iOS project is described with XcodeGen:

```bash
cd ios
xcodegen generate
open ScreenMotion.xcodeproj
```

Run on a physical device for CoreMotion and Live Photo validation.

## Prototype assets

The starter contains four lightweight generated images:

- `car_night.jpg`
- `aquarium.jpg`
- `space.jpg`
- `nature.jpg`

See `docs/ASSET_PLAN.md` before producing store-quality packs.
