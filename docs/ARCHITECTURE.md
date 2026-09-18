# ScreenMotion Architecture

## Android

Android is the full interactive wallpaper platform.

Flow:

1. Compose app picks a theme and interaction settings.
2. `ConfigRepository` persists `ThemeConfig`.
3. `InteractiveWallpaperService` owns the wallpaper surface.
4. `MotionController` reads rotation-vector data.
5. The selected `SceneRenderer` combines background assets, particles, touch input and gyro parallax.
6. Rendering stops as soon as the wallpaper is not visible.

Current rendering is intentionally Canvas-first for fast iteration. The next graphics milestone is an EGL/OpenGL ES renderer for layered 2.5D/3D scenes.

## iOS

iOS cannot run an Android-style third-party wallpaper engine.

The native SwiftUI app therefore has two paths:

- **Interactive preview:** CoreMotion + drag gestures run at app runtime.
- **Lock-screen output:** the app renders a short animation, packages it as a Live Photo pair, and saves it to Photos.

The user then selects that Live Photo from iOS wallpaper settings.

## Shared content model

Both apps use the same conceptual theme catalog:

- spaceship
- space
- aquarium
- car
- truck
- bus
- tractor
- tank
- helicopter
- airplane
- bicycle
- nature

Shared preview assets live under `assets/wallpapers`.
