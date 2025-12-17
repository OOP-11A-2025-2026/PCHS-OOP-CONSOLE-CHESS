package cli;

import board.Board;
import board.Square;
import pieces.Piece;
import enums.Color;

public final class BoardPrinter {
    private static final String RESET = "\033[0m";

    private static final String WHITE_BG = "\033[47m";   // white
    private static final String BLACK_BG = "\033[40m";  // dark gray

    private static final String WHITE_PIECE = "\033[97m"; // bright white
    private static final String BLACK_PIECE = "\033[90m"; // dark gray


    private BoardPrinter() {}

    public static void print(Board board) {

        System.out.println("\n   a  b  c  d  e  f  g  h");

        for (int rank = 7; rank >= 0; rank--) {
            System.out.print(" " + (rank + 1) + " ");

            for (int file = 0; file < 8; file++) {
                boolean lightSquare = (rank + file) % 2 == 0;
                String bg = lightSquare ? WHITE_BG : BLACK_BG;

                Square square = new Square(file, rank);
                Piece piece = board.getPieceAt(square);

                if (piece == null) {
                    System.out.print(bg + "   " + RESET);
                } else {
                    String fg = piece.getColor() == Color.WHITE
                            ? WHITE_PIECE
                            : BLACK_PIECE;

                    char symbol = PieceRenderer.toSymbol(piece);
                    System.out.print(bg + fg + " " + symbol + " " + RESET);
                }
            }

            System.out.println(" " + (rank + 1));
        }

        System.out.println("   a  b  c  d  e  f  g  h\n");
    }
}
