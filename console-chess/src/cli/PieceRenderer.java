package cli;
import enums.Color;
import pieces.Piece;

public final class PieceRenderer {

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
