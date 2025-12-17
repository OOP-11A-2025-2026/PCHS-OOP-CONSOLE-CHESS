import java.util.Scanner;

import board.Board;
import board.Move;
import cli.ChessCLI;
import cli.PieceRenderer;
import pieces.Piece;
import enums.PieceType;
import enums.Color;
import pieces.Rook;
import input.MoveParser;

public class ChessGame {
    public static void main(String[] args) {
        ChessCLI cli = new ChessCLI();
        cli.start();
    }
}
