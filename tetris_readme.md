# Tetris Game - JavaFX Implementation

A complete implementation of the classic Tetris game built with JavaFX, following strict Object-Oriented Programming principles.

## 📋 Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [How to Run](#how-to-run)
- [Game Controls](#game-controls)
- [Game Rules](#game-rules)
- [Architecture](#architecture)
- [Scoring System](#scoring-system)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- **7 Standard Tetromino Pieces**: I, O, T, S, Z, J, and L shapes
- **Smooth Gameplay**: AnimationTimer-based game loop for fluid animations
- **Progressive Difficulty**: Game speed increases with each level
- **Next Piece Preview**: See what's coming next
- **Score Tracking**: Earn more points for clearing multiple lines at once
- **Level System**: Advance through levels as you clear lines
- **Game Over Detection**: Automatic detection when blocks reach the top
- **Restart Capability**: Quick restart with space bar

## 🔧 Requirements

- **Java Development Kit (JDK)**: Version 11 or higher
- **JavaFX SDK**: Version 11 or higher (included in JDK 8, separate download for JDK 11+)
- **Operating System**: Windows, macOS, or Linux

### JavaFX Installation

For JDK 11 and above, JavaFX needs to be installed separately:

**Option 1: Using a JDK with JavaFX bundled**
- Download and install a JDK distribution that includes JavaFX (e.g., Liberica Full JDK, Azul Zulu FX)

**Option 2: Download JavaFX SDK separately**
1. Download JavaFX SDK from [openjfx.io](https://openjfx.io/)
2. Extract to a location on your computer
3. Note the path to the `lib` folder

## 📥 Installation

1. **Clone or download** the `TetrisGame.java` file to your local machine

2. **Verify Java installation**:
   ```bash
   java -version
   ```
   Should show Java 11 or higher

## 🚀 How to Run

### Method 1: If JavaFX is bundled with your JDK

```bash
# Compile
javac TetrisGame.java

# Run
java TetrisGame
```

### Method 2: If JavaFX is installed separately

```bash
# Compile (replace PATH_TO_FX with your JavaFX lib path)
javac --module-path PATH_TO_FX --add-modules javafx.controls TetrisGame.java

# Run
java --module-path PATH_TO_FX --add-modules javafx.controls TetrisGame
```

**Example** (Windows):
```bash
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls TetrisGame.java
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls TetrisGame
```

**Example** (macOS/Linux):
```bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls TetrisGame.java
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls TetrisGame
```

## 🎮 Game Controls

| Key | Action |
|-----|--------|
| **←** (Left Arrow) | Move piece left |
| **→** (Right Arrow) | Move piece right |
| **↓** (Down Arrow) | Soft drop (faster fall + bonus points) |
| **↑** (Up Arrow) or **X** | Rotate piece clockwise |
| **SPACE** | Restart game (when game over) |

## 📖 Game Rules

1. **Objective**: Clear as many lines as possible before the blocks reach the top

2. **Gameplay**:
   - Tetromino pieces fall from the top of the 10×20 grid
   - Rotate and position pieces to create complete horizontal lines
   - Complete lines are cleared and all blocks above shift down
   - Game speed increases with each level

3. **Leveling Up**:
   - Clear 10 lines to advance to the next level
   - Each level increases the falling speed
   - Higher levels multiply your score

4. **Game Over**:
   - Game ends when a new piece cannot be placed at the starting position
   - Press SPACE to restart and try again

## 🏗️ Architecture

The game follows Object-Oriented Programming principles with three core classes:

### **Block Class**
- Represents a single Tetromino piece
- Manages shape definition for all 7 standard pieces
- Handles rotation logic (90-degree clockwise rotation)
- Tracks position (x, y coordinates)
- Stores piece color

### **GameGrid Class**
- Manages the 10×20 grid state
- Tracks filled/empty cells and their colors
- Implements collision detection:
  - Boundary checking (walls and floor)
  - Collision with settled blocks
- Handles line clearing and row shifting
- Locks pieces into the grid permanently

### **GameEngine Class**
- Controls game loop timing using AnimationTimer
- Manages scoring system:
  - 1 line: 100 points × level
  - 2 lines: 300 points × level
  - 3 lines: 500 points × level
  - 4 lines: 800 points × level
- Implements level progression
- Handles piece spawning and game over detection
- Coordinates between Block and GameGrid

### **TetrisGame Class** (Main Application)
- JavaFX Application entry point
- Renders game state to canvas
- Processes keyboard input
- Manages UI components (score, level, preview)

## 🎯 Scoring System

| Lines Cleared | Base Points | Actual Points |
|---------------|-------------|---------------|
| 1 line | 100 | 100 × Level |
| 2 lines | 300 | 300 × Level |
| 3 lines | 500 | 500 × Level |
| 4 lines | 800 | 800 × Level |

**Bonus**: +1 point for each soft drop (Down Arrow)

**Level Progression**: Every 10 lines cleared advances you to the next level

## 🔍 Troubleshooting

### Issue: "javafx.application.Application not found"

**Solution**: JavaFX is not in your classpath. Use Method 2 in [How to Run](#how-to-run) section.

### Issue: Game window doesn't appear

**Solution**: Ensure you have a display environment. If running on a server, you need X11 forwarding or a virtual display.

### Issue: Game runs too slowly/quickly

**Solution**: This is controlled by the `BASE_DROP_INTERVAL` constant (line 337). Modify this value in the code:
```java
private static final long BASE_DROP_INTERVAL = 1000; // milliseconds
```

### Issue: Pieces rotate incorrectly

**Solution**: The rotation is clockwise by default. If you need counter-clockwise, press the rotate key three times, or modify the `rotate()` method.

## 📝 Code Documentation

All classes and methods include comprehensive Javadoc comments. To generate HTML documentation:

```bash
javadoc -d docs TetrisGame.java
```

Then open `docs/index.html` in your browser.

## 🎨 Customization

You can customize various aspects of the game by modifying constants in the code:

- **Grid Size**: `GRID_WIDTH` and `GRID_HEIGHT` (lines 25-26)
- **Cell Size**: `CELL_SIZE` (line 24)
- **Drop Speed**: `BASE_DROP_INTERVAL` (line 337)
- **Lines per Level**: `LINES_PER_LEVEL` (line 340)
- **Piece Colors**: `COLORS` array in Block class (lines 66-74)

## 🏆 Tips for High Scores

1. **Plan Ahead**: Use the next piece preview to strategize
2. **Clear Multiple Lines**: 4 lines at once (Tetris) gives the most points
3. **Soft Drop Bonus**: Use Down Arrow for extra points
4. **Keep It Flat**: Avoid creating tall stacks with gaps
5. **Save the I-Piece**: Keep a column open for the long I-piece

## 📄 License

This is an educational project implementing the classic Tetris game. Feel free to use and modify for learning purposes.

## 👤 Author

Created as a demonstration of Object-Oriented Programming principles using JavaFX.

---

**Enjoy playing Tetris! 🎮**