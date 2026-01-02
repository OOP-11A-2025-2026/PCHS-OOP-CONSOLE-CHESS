package enums;

/**
 * Enumeration of all chess piece types with their standard algebraic notation symbols.
 * K=King, Q=Queen, R=Rook, B=Bishop, N=Knight, P=Pawn
 */
public enum PieceType {
    KING('K'),
    QUEEN('Q'),
    ROOK('R'),
    BISHOP('B'),
    KNIGHT('N'),
    PAWN('P');

    private final char symbol;

    /**
     * Creates a PieceType with its notation symbol.
     * @param symbol The single character used in algebraic notation
     */
    PieceType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Gets the algebraic notation symbol for this piece type.
     * @return The character symbol (K, Q, R, B, N, or P)
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Converts a character to its corresponding PieceType.
     * @param c The character to convert (K, Q, R, B, N, or P)
     * @return The matching PieceType, or null if no match found
     */
    public static PieceType fromChar(char c) {
        for (PieceType type : values()) {
            if (type.symbol == c) {
                return type;
            }
        }
        return null;
    }
}
