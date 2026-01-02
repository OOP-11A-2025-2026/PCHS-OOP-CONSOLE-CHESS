package board;

import enums.PieceType;

/**
 * Represents a chess move from one square to another.
 * Optionally includes promotion information for pawn promotion moves.
 */
public class Move {
    private final Square from;
    private final Square to;
    private final PieceType promotion;

    /**
     * Creates a new Move without promotion.
     * @param from The starting square
     * @param to The destination square
     */
    public Move(Square from, Square to) {
        this(from, to, null);
    }

    /**
     * Creates a new Move with optional promotion.
     * @param from The starting square
     * @param to The destination square
     * @param promotion The piece type to promote to (null if not a promotion)
     */
    public Move(Square from, Square to, PieceType promotion) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    /**
     * Gets the starting square of the move.
     * @return The source square
     */
    public Square getFrom() {
        return from;
    }

    /**
     * Gets the destination square of the move.
     * @return The target square
     */
    public Square getTo() {
        return to;
    }

    /**
     * Gets the promotion piece type, if this is a pawn promotion move.
     * @return The piece type to promote to, or null if not a promotion
     */
    public PieceType getPromotion() {
        return promotion;
    }

}
