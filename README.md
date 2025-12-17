# Console Chess Project

CLI chess made by dummies

---

## How to Run

**Requirements:**

- Java JDK 11 or higher installed.

- `make` utility (Linux, macOS, or Windows with WSL / Git Bash / MinGW).

---

**Compile and run:**

<p style="color:LightSkyBlue">
1. Open a terminal and navigate to the <code>project root</code> (the directory where the Makefile is)<br>
2. To compile and run in <strong>one step</strong>, type:
</p>

```
make
```

This will:

- Compile all ``.java`` files from ``src/`` into the ``bin/`` directory.

- Launch the chess game.

---

3. To **only compile** without running:
```
make compile
```

4. To **only run** after compilation:
```
make run
```
5. To **clean** all compiled files (remove bin/)
```
make clean
```
---

## ☆ Core Classes

**Piece**
- Abstract class representing a chess piece.
- Stores color, type, and position.
- Each subclass (`Pawn`, `Rook`, `Knight`, `Bishop`, `Queen`, `King`) should implement `getLegalMoves()` to generate all possible valid moves from it's current position.

---

**Board**

Represents the physical chessboard as an 8×8 `Piece[][]` grid.
Each cell either holds a Piece or `null` for empty squares.

The `Board` is responsible for:

- Placing and retrieving pieces

- Applying moves (including special moves like en passant)

- Checking bounds, paths, and attacks

- Cloning itself for move validation without mutating the real game state

---

**Move**
- Represents a move with `from` and `to` squares.

---

**Square**
- Represents a square on the board with file and rank.

---

**Enums**
- **Color:** `WHITE` or `BLACK`.
- **PieceType:** `PAWN`, `ROOK`, `KNIGHT`, `BISHOP`, `QUEEN`, `KING`.

---

**Input & Parsing**

**Move parser**

Handles all user input and converts it into a valid `Move`.

Supported input formats:
- Coordinate notation: `e2 e4`.
- Basic algebraic notation: `e4`, `Nf3`

How it works:
- Coordinate notation: e2 e4
- Analyzes the current board state
- Finds the first move that matches the given input
- Ignores check (`+`) and checkmate (`#`) symbols

If the move is invalid or impossible, the parser returns `null`.

---

**Pawn Promotion Handler**

Handles pawn promotion when a pawn reaches the last rank.

When promotion occurs:
- Displays a console menu
- Prompts the player to choose a piece:
  -  Queen
  - Rook
  - Bishop
  - Knight
- Creates and returns a new piece of the selected type

The handler **guarantees a valid choice** and does not allow the game to continue
until correct input is provided.

---

**Timer**

**GameTimer**

Provides real-time chess clock functionality.

Features:
- Each player starts with 10 minutes
- Time decreases only for the player whose turn it is
- Timer switches after every valid move
- Runs in real time inside the console

If a player runs out of time, the game **ends automatically**.

**Game**

Acts as the main game controller and rule enforcer.
It manages the current player, game state, and high-level flow of the match.

The `Game` class is responsible for:

- Starting and controlling a chess session

- Validating and executing player moves

- Preventing illegal moves and self-check

- Detecting check, checkmate, stalemate, draw, and resignation

- Switching turns and tracking draw offers

---

## File Structure
```
src/
├── ChessGame.java            
│
├── board/                    # board representation & state
│   ├── Board.java
│   ├── Square.java
│   ├── Move.java
│   ├── PawnPromotionHandler.java
│   └── Square.java
│
├── pieces/                   # piece classes
│   ├── Piece.java            # abstract
│   ├── Pawn.java
│   ├── Rook.java
│   ├── Knight.java
│   ├── Bishop.java
│   ├── Queen.java
│   └── King.java
│
├── enums/                    # enums for clarity
│   ├── Color.java
│   └── PieceType.java
│
├── game/                     # game logic & controller
│   └── Game.java
│
├── cli                       # visualization
│   ├── ChessCLI.java   
│   ├── PieceRenderer.java   
│   └── BoardPrinter.java 
│
├── timer                     # timer logic
│   └── GameTimer.java 
│
├── pgn                       # pgn logic
│   ├── PGNExporter.java 
│   └── PGNParser.java   
│
└── input/                    # algebraic notation parser
    └── MoveParser.java 
  
```

---
## TLDR

- `board/` contains core board representation and move logic.
- `pieces/` contains piece classes and their movement rules.
- `enums/` stores shared types for clarity.
- `game/` handles player turns, rules enforcement, and game state.
- `cli/` console interface and rendering
- `timer/` real-time chess clock
- `input/` user move parsing
- `pgn/` saving and loading games

---
## State diagram
![Diagram](images/console-chess-state-diagram.drawio.png)
