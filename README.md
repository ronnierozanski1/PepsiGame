# Pepse – A Procedurally‑Generated 2‑D Sandbox Game

## Table of Contents
1. [About the Project](#about-the-project)  
2. [Getting Started](#getting-started)  
    • Prerequisites  
    • Running the Game  
3. [How to Play](#how-to-play)  
4. [Project Structure & Design Overview](#project-structure--design-overview)  
5. [Extending the Game](#extending-the-game)  
6. [License](#license)

---

## About the Project
**Pepse** is a lightweight Java game built on the **[DanoGL](https://github.com/danonav/DanoGL)** educational engine. The world is generated on‑the‑fly using deterministic noise, delivering an *infinite* side‑scrolling terrain that is never quite the same twice.

### Core Features

| Feature | Description |
|---------|-------------|
| Procedural terrain | Smooth hills produced with a Perlin‑style noise function and coloured by `ColorSupplier`. |
| Infinite scrolling | New terrain and foliage are created just beyond the camera while off‑screen chunks are freed to keep memory usage constant. |
| Dynamic day/night | A travelling sun, glow halo and a fading night overlay simulate a 30‑second diurnal cycle. |
| Living world | Trees sprout leaves & fruits; clouds drift across the sky and rain when the avatar jumps. |
| Energy system | Running and jumping consume energy; idling and eating fruit replenish it. |
| Fully animated avatar | Separate idle / run / jump sprite sheets rendered as `AnimationRenderable`s. |

---

## Getting Started
### Prerequisites
* **JDK 17** or later  
* A copy of **`danogl.jar`** (place it under `lib/` or add it to your class‑path)  
* The **`assets/`** folder shipped with the game (sprites, sounds).

### Running from an IDE
1. **Clone** the repository:  
   ```bash
   git clone https://github.com/<your‑username>/pepse.git
   ```
2. **Import** the project as a *Gradle* / *Maven* / *plain* Java project (no external plugins required).  
3. Mark `src/` as *Sources Root* and ensure `assets/` is copied to the **working directory**.  
4. Run the `pepse.PepseGameManager` *main* class – the game window should pop right up.

### Running from the command line
```bash
# compile (Unix/macOS – adjust path separators for Windows)
javac -cp "lib/danogl.jar" -d out $(find src -name "*.java")

# run
java -cp "out:lib/danogl.jar:assets" pepse.PepseGameManager
```
> **Tip:** Add the optional `-Xmx` flag to tweak the JVM heap size if you plan to modify chunk‑generation parameters.

---

## How to Play
| Key | Action |
|-----|--------|
| **← / →** | Run left / right (costs 0.5 ⚡ per frame) |
| **Space** | Jump (costs 10 ⚡) |

* ⚡ *Energy* is shown at the top‑left. If it hits 0 you can only walk slowly until you rest or eat fruit.  
* Blue **fruit** grow randomly in tree canopies. Collide with them to regain 10 ⚡.  
* Every jump may trigger the nearest **cloud** to shower a burst of raindrops.  
* Watch the **sun** rise and set every 30 seconds; night mutes colours and dims the world.

---

## Project Structure & Design Overview
```
pepse/
 ├── PepseGameManager.java   # Entry‑point & high‑level orchestration
 ├── util/
 │   ├── ColorSupplier.java
 │   └── NoiseGenerator.java
 ├── world/
 │   ├── Avatar.java
 │   ├── Block.java
 │   ├── Terrain.java
 │   ├── Sky.java
 │   ├── Cloud.java
 │   ├── daynight/
 │   │     ├── Night.java
 │   │     ├── Sun.java
 │   │     └── SunHalo.java
 │   └── trees/
 │         ├── Flora.java
 │         ├── Tree.java
 │         ├── Leaf.java
 │         ├── Fruit.java
 │         └── TreeTrunk.java
 └── assets/
```

### Architectural Highlights
* **Component‑oriented design** – GameObjects are decorated at runtime with `Transition`, `ScheduledTask` and custom lambda components for behaviour.
* **Deterministic randomness** – `seed` is generated once at boot; combined with x‑coordinates it ensures trees & terrain appear identical on each run with the same seed.
* **Chunk management** – `PepseGameManager` tracks the avatar’s movement and lazily builds new terrain/flora just beyond the camera while pruning objects that scroll out of view.
* **Visual polish** – Subtle opacity tweens (night overlay, raindrop fade‑out) and size/angle oscillations (leaves).

---

## Extending the Game
| Idea | Where to Start |
|------|---------------|
| Add new biomes | Fork `Terrain` & adjust `ColorSupplier` hues + noise frequency. |
| Extra power‑ups | Implement a `GameObject` similar to `Fruit`, register collisions in `Avatar`. |
| Parallax backgrounds | Spawn additional `Sky` layers at different scroll speeds. |
| Enemy mobs | Derive from `GameObject`, add AI loop, and register collisions. |
| Save/Load worlds | Serialize `seed`, avatar position, and energy to JSON. |

---

## License
Distributed under the **MIT License**. See `LICENSE` for more information.
