package pieces;

import board.Move;
import enums.Color;
import enums.PieceType;
import board.Board;

public abstract class Piece {
    protected final Color color;
    protected PieceType type;
    protected int file;  // 0–7 for a–h
    protected int rank;  // 0–7 for 1–8

    public Piece(Color color, PieceType type, int file, int rank) {
        this.color = color;
        this.type = type;
        this.file = file;
        this.rank = rank;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public int getFile() {
        return file;
    }

    public int getRank() {
        return rank;
    }

    public void setPosition(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    // Every piece must implement how it generates legal moves
    public abstract java.util.List<Move> getLegalMoves(Board board);
    public abstract Piece copy();
}

