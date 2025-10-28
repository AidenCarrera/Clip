# Nibs' Paperclip Collector 🦝📎

![Gameplay Gif](src/main/resources/readme/gameplay.gif)

A Java-based desktop game where you play as Nibs the raccoon, collecting paperclips scattered around a cluttered office desk.
Built entirely from scratch using Java and Swing, this project focuses on game logic, graphics rendering, and input handling.

## Purpose

The goal of Nibs’ Paperclip Collector is to create a fun game built with a custom Java engine.
It started as a personal project inspired by the mobile game Cookies Inc., focused on recreating its core gameplay mechanics.

## Features

Play as Nibs the raccoon and collect scattered paperclips to increase your clip count

Buy upgrades to increase the value and spawn rate of paperclips

Smooth frame timing with delta-based game loop

Adjustable clip size, resolution, and scaling options

Config-based settings

Lightweight custom rendering (no external engines)

Modular codebase for future expansion (menus, sounds, etc.)

## Tech Stack

- **Java 25** – Language
- **Swing / AWT** – Rendering and input handling
- **Maven** – Project management and dependency system
- **Jackson** – JSON configuration and save system

## Save Location

The game automatically saves progress and settings in a folder named:

Documents/My Games/ClipGame/

If you uninstall or delete the game, make sure to delete this folder to remove your saved data.

## Installation

To clone and run this application, you'll need Git and Maven installed. Then:
```
# Clone the repository
git clone https://github.com/AidenCarrera/Clip.git

# Go into the project directory
cd clip-game

# Compile and run the game
mvn clean compile exec:java
```

## Gameplay

- Drag the mouse across the paperclips to collect them
- Buy upgrades from the store
- Repeat until you get the golden clip upgrade
- Play endlessly for higher scores and faster progression

## Future Improvements

- More upgrades and progression systems
- Animated sprites and visual polish
- Pause menu and settings UI
- Add sound effects
- Improve mouse handling and responsiveness
