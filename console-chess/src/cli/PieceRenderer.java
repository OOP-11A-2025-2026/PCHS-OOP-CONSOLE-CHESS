package cli;
import enums.Color;
import pieces.Piece;

/**
 * Utility class for rendering chess pieces as text symbols.
 * Uses uppercase letters for White pieces and lowercase for Black pieces.
 */
public final class PieceRenderer {

    /**
     * Converts a piece to its single-character symbol representation.
     * White pieces use uppercase (K, Q, R, B, N, P).
     * Black pieces use lowercase (k, q, r, b, n, p).
     * 
     * @param piece The piece to render
     * @return Single character representing the piece
     */
    public static char toSymbol(Piece piece) {
        // Using letters: UPPERCASE = White, lowercase = Black
        return switch (piece.getType()) {
            case KING   -> piece.getColor() == Color.WHITE ? 'K' : 'k';
            case QUEEN  -> piece.getColor() == Color.WHITE ? 'Q' : 'q';
            case ROOK   -> piece.getColor() == Color.WHITE ? 'R' : 'r';
            case BISHOP -> piece.getColor() == Color.WHITE ? 'B' : 'b';
            case KNIGHT -> piece.getColor() == Color.WHITE ? 'N' : 'n';
            case PAWN   -> piece.getColor() == Color.WHITE ? 'P' : 'p';
        };
    }
}
