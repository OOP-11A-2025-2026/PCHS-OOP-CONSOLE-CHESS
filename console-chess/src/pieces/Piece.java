package pieces;

import board.Board;
import board.Move;
import enums.Color;
import enums.PieceType;

/**
 * Abstract base class for all chess pieces.
 * Defines common properties and methods that all pieces must implement.
 */
public abstract class Piece {
    protected final Color color;
    protected PieceType type;
    protected int file;  // 0–7 for a–h
    protected int rank;  // 0–7 for 1–8

    /**
     * Creates a new chess piece.
     * @param color The color of the piece (WHITE or BLACK)
     * @param type The type of piece (KING, QUEEN, etc.)
     * @param file The file (column) position, 0-7
     * @param rank The rank (row) position, 0-7
     */
    public Piece(Color color, PieceType type, int file, int rank) {
        this.color = color;
        this.type = type;
        this.file = file;
        this.rank = rank;
    }

    /**
     * Gets the color of this piece.
     * @return The piece's color (WHITE or BLACK)
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the type of this piece.
     * @return The piece type (KING, QUEEN, ROOK, BISHOP, KNIGHT, or PAWN)
     */
    public PieceType getType() {
        return type;
    }

    /**
     * Gets the file (column) position of this piece.
     * @return File index (0-7 for a-h)
     */
    public int getFile() {
        return file;
    }

    /**
     * Gets the rank (row) position of this piece.
     * @return Rank index (0-7 for 1-8)
     */
    public int getRank() {
        return rank;
    }

    /**
     * Updates the position of this piece on the board.
     * @param file The new file (column) position
     * @param rank The new rank (row) position
     */
    public void setPosition(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    /**
     * Generates all legal moves for this piece from its current position.
     * Must be implemented by each specific piece type.
     * @param board The current board state
     * @return List of all legal moves for this piece
     */
    public abstract java.util.List<Move> getLegalMoves(Board board);

    /**
     * Creates a deep copy of this piece.
     * Must be implemented by each specific piece type.
     * @return A new Piece instance with the same properties
     */
    public abstract Piece copy();
}

