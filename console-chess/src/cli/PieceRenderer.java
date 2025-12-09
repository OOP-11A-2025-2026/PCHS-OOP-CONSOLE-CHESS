package cli;
import pieces.Piece;
import enums.Color;

public final class PieceRenderer {

    public static char toSymbol(Piece piece) {
        return switch (piece.getType()) {
            case KING   -> piece.getColor() == Color.WHITE ? '♔' : '♚';
            case QUEEN  -> piece.getColor() == Color.WHITE ? '♕' : '♛';
            case ROOK   -> piece.getColor() == Color.WHITE ? '♖' : '♜';
            case BISHOP -> piece.getColor() == Color.WHITE ? '♗' : '♝';
            case KNIGHT -> piece.getColor() == Color.WHITE ? '♘' : '♞';
            case PAWN   -> piece.getColor() == Color.WHITE ? '♙' : '♟';
        };
    }
}
