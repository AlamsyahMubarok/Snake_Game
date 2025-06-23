# Snake Game – Java Project

This is a simple **Snake Game** developed in Java as a fun side project. The goal of this project was to explore game mechanics, object-oriented design, and basic graphics rendering in Java. Despite being built casually, this project demonstrates modular design using multiple classes for better game state management and extensibility.

---

## Gameplay Overview

- Classic Snake mechanics: control the snake to collect food and grow longer.
- Includes **Power-Ups**, **Obstacles (Stones)**, and **Bonus items**.
- Simple keyboard controls and custom font rendering.
- Background, textures, and fonts customized for visual enhancement.

---

## Class Structure

The project is structured using several classes to handle specific responsibilities:

| Class Name         | Description |
|--------------------|-------------|
| `GameMain`         | Main entry point to start the game loop and render graphics. |
| `Snake`            | Core class that represents the snake's logic and movement. |
| `SnakeSegment`     | Represents individual segments of the snake body. |
| `Food`             | Handles spawning and rendering food objects. |
| `Bonus`            | Implements bonus items with special effects. |
| `PowerUp`          | Adds temporary abilities or score boosts. |
| `Move`             | Defines movement behavior and direction handling. |
| `State`            | Enumeration of different game states (e.g., playing, paused). |
| `StateTransition`  | Manages transitions between different game states. |

---

## Assets Used

- `grass.jpg`, `stone.jpg` – Game textures
- `grassbackround.avif` – Background image
- `Bungee-Regular.ttf` – Custom font used for UI elements

> All assets are stored in the project structure under `/out/production/GameUloeRapip/Snake`.

---

## Getting Started

To run the game:

1. Clone or download the project.
2. Open in IntelliJ IDEA or any Java IDE.
3. Ensure asset files are properly referenced in the project path.
4. Run `GameMain.java`.

---

## Author

**Sultan Alamsyah Lintang Mubarok**  
Just a casual experiment in learning game development using Java.

---
