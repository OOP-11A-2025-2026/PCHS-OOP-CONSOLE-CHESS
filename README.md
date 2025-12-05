# Console Chess Project

CLI chess made by dummies

---

## ☆ Core Classes

**Piece**
- Abstract class representing a chess piece.
- Stores color, type, and position.
- Each subclass (`Pawn`, `Rook`, `Knight`, `Bishop`, `Queen`, `King`) should implement `getLegalMoves()` to generate all possible valid moves from it's current position.

**Board**
- Represents the chessboard as a `Piece[][]` array.
- `null` indicates empty squares.

**Move**
- Represents a move with `from` and `to` squares.

**Square**
- Represents a square on the board with file and rank.


**Enums**
- **Color:** `WHITE` or `BLACK`.
- **PieceType:** `PAWN`, `ROOK`, `KNIGHT`, `BISHOP`, `QUEEN`, `KING`.

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
