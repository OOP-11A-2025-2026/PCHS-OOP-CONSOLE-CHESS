package enums;

public enum PieceType {
    KING('K'),
    QUEEN('Q'),
    ROOK('R'),
    BISHOP('B'),
    KNIGHT('N'),
    PAWN('P');

    private final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
    public static PieceType fromChar(char c) {
        for (PieceType type : values()) {
            if (type.symbol == c) {
                return type;
            }
        }
        return null;
    }
}
