# Asset plan

The included images are lightweight generated prototype assets. They prove composition and parallax, not final production quality.

## Current prototype backgrounds

| File | Scene | Interactive layer |
|---|---|---|
| `assets/wallpapers/car_night.jpg` | neon wet highway | vehicle X movement, camera parallax |
| `assets/wallpapers/aquarium.jpg` | coral reef | fish flock, bubbles, touch attraction |
| `assets/wallpapers/space.jpg` | planet / nebula | star field, spaceship, gyro drift |
| `assets/wallpapers/nature.jpg` | alpine lake | camera parallax, birds / motes |

## Production asset layers

Each premium theme should evolve from one flat background into 3–6 render layers:

- far background
- mid background
- road / sea / terrain
- foreground mask
- interactive subject sprite or GLB
- particles / weather

Recommended master size for vertical artwork: **1440 × 3200 or larger**, with a safe center crop for common Android and iPhone ratios.

## Vehicle assets

Create transparent PNG/WebP sprites first, then migrate premium themes to GLB:

- supercar rear 3/4
- motorcycle
- bicycle
- tractor
- tank
- city bus
- semi truck
- helicopter
- passenger aircraft
- sci-fi spacecraft

For every vehicle produce a neutral, trademark-free design.

## Animation rules

- gyro input is filtered and clamped
- subject movement uses spring + damping, never raw sensor coordinates
- touch input influences target position rather than teleporting the subject
- background parallax moves less than foreground
- particles have independent depth factors
- when Android wallpaper visibility becomes false, all sensor and render work stops
