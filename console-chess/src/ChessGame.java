import java.util.Scanner;

import board.Board;
import board.Move;
import cli.PieceRenderer;
import pieces.Piece;
import enums.PieceType;
import enums.Color;
import pieces.Rook;
import input.MoveParser;

public class ChessGame {
    public static void main(String[] args) {
        //printing example
        Rook rook = new Rook(Color.BLACK, 0, 0);
        System.out.println(PieceRenderer.toSymbol(rook));
    
        Board board = new Board();

        Scanner scanner = new Scanner(System.in);
        Color currentPlayer = Color.WHITE;

        while (true) {
            System.out.print(currentPlayer + " ход: ");
            String input = scanner.nextLine();

            Move move = MoveParser.parse(input, board, currentPlayer);
            if (move == null) {
                System.out.println("Невалиден ход!");
                continue;
            }

            board.applyMove(move);
            currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
        }
    }
}
