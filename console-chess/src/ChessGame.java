import board.Board;
import board.Square;
import cli.PieceRenderer;
import cli.BoardPrinter;
import pieces.Piece;
import enums.PieceType;
import enums.Color;
import pieces.Rook;

public class ChessGame {
    public static void main(String[] args) {
        Board board = new Board();

        board.setPieceAt(new Square(0, 0), new Rook(Color.WHITE, 0, 0));
        board.setPieceAt(new Square(0, 7), new Rook(Color.BLACK, 0, 7));

        BoardPrinter.print(board);
    }
}

