import cli.PieceRenderer;
import pieces.Piece;
import enums.PieceType;
import enums.Color;
import pieces.Rook;

public class ChessGame {
    public static void main(String[] args) {
        //printing example
        Rook rook = new Rook(Color.BLACK, 0, 0);
        System.out.println(PieceRenderer.toSymbol(rook));
    }
}
