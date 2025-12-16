import board.Board;
import board.Square;
import cli.BoardPrinter;
import cli.ChessCLI;
import enums.Color;
import pieces.Rook;
import timer.GameTimer;

public class ChessGame {

    public static void main(String[] args) {
        ChessCLI cli = new ChessCLI();
        cli.start();
    }
}
