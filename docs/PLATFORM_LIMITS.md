# Platform behavior

## Android

Supported in the live wallpaper itself:

- continuous animation
- gyroscope / rotation-vector interaction
- touch / drag interaction where the wallpaper host forwards events
- 30/60 FPS render loop
- dynamic theme selection
- particles and layered parallax

## iOS

Supported inside the app preview:

- CoreMotion tilt
- drag interaction
- animated particles
- interactive vehicles, fish and space scenes

Supported as system wallpaper output:

- generated Live Photo for the lock screen
- still image fallback

Not supported by public iOS APIs:

- an app-owned continuously executing wallpaper renderer
- real-time CoreMotion while the app is no longer running and the wallpaper is displayed
- Android-style touch callbacks from the iOS home screen to a third-party wallpaper app

The product UI should state this difference clearly instead of promising identical platform behavior.
