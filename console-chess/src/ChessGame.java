import board.Board;
import board.Square;
import cli.BoardPrinter;
import enums.Color;
import pieces.Rook;
import timer.GameTimer;

public class ChessGame {

    public static void main(String[] args) {
        Board board = new Board();
        board.setPieceAt(new Square(0, 0), new Rook(Color.WHITE, 0, 0));
        board.setPieceAt(new Square(0, 7), new Rook(Color.BLACK, 0, 7));
        GameTimer timer = new GameTimer(10);
        timer.start();
        BoardPrinter.print(board);

        System.out.println("⏱ Game started. White to move.");
        timer.printTime();
    }
}
