# ScreenMotion

Native iOS + Android experiment for living lock/home screen themes.

## Implemented

### Android
- Kotlin + Jetpack Compose interactive onboarding
- real `WallpaperService`
- rotation-vector / gyroscope input
- touch / drag input
- visibility-aware 30/60 FPS render loop
- aquarium fish + bubbles
- space starfield + spacecraft
- nature parallax
- car, truck, bus, tractor, tank, helicopter, airplane and bicycle scenes
- procedural fallback backgrounds, so the app runs without external binary assets

### iOS
- SwiftUI interactive onboarding and theme picker
- CoreMotion tilt preview
- drag interaction
- procedural aquarium, space, nature and vehicle scenes
- Live Photo render/export pipeline
- Photos library save flow

> iOS does not expose an Android-style third-party live-wallpaper engine. Tilt/touch interaction is real while ScreenMotion is open; lock-screen output is exported as a Live Photo.

## Repository

- `android/` Android Studio project
- `ios/` Swift/XcodeGen project
- `assets/wallpapers/` vector concept assets
- `docs/` architecture, platform limitations and asset roadmap

## Android

Open `android/` in Android Studio, sync and run.

Choose a theme and tap **Canlı Duvar Kağıdını Uygula**.

## iOS

Generate the Xcode project with XcodeGen:

```bash
cd ios
xcodegen generate
open ScreenMotion.xcodeproj
```

Use a physical device for CoreMotion and Live Photo validation.

## Asset strategy

The committed SVG files are lightweight visual references. Production theme packs should use 3–6 depth layers and transparent subject assets or GLB models. See `docs/ASSET_PLAN.md`.
