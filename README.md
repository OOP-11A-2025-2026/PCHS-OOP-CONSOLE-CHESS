# Console Chess Project

CLI chess made by dummies

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
│   └── Move.java
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
├── game/       TO-DO              # game logic & controller
│   (game logic, validation ect.)
│
├── cli
│   ├── ChessCLI.java   
│   └── BoardPrinter.java 
└── utils/                    
  (algebraic notation parser + pgn file stuff)
  
```

---
## TLDR

- `board/` contains core board representation and move logic.
- `pieces/` contains piece classes and their movement rules.
- `enums/` stores shared types for clarity.
- `game/` handles player turns, rules enforcement, and game state.
- `utils/` contains helper classes for algebraic notation and PGN file handling.

---
## State diagram
![Diagram](images/console-chess-state-diagram.drawio.png)
