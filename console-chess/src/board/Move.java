package board;

import enums.PieceType;

public class Move {
    private final Square from;
    private final Square to;
    private final PieceType promotion;

    public Move(Square from, Square to) {
        this(from, to, null);
    }

    public Move(Square from, Square to, PieceType promotion) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public PieceType getPromotion() {
        return promotion;
    }

}
