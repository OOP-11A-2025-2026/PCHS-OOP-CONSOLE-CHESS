package cli;

import board.Board;
import board.Square;
import pieces.Piece;

public final class BoardPrinter {

    private BoardPrinter() {}

    public static void print(Board board) {
        printTopBorder();

        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + " │");

            for (int file = 0; file < 8; file++) {
                Square sq = new Square(file, rank);
                Piece piece = board.getPieceAt(sq);

                char symbol = (piece == null)
                        ? ' '
                        : PieceRenderer.toSymbol(piece);

                System.out.print(" " + symbol + " │");
            }

            System.out.println();

            if (rank > 0) {
                printMiddleBorder();
            }
        }

        printBottomBorder();
        printFiles();
    }

    private static void printTopBorder() {
        System.out.println("  ┌───┬───┬───┬───┬───┬───┬───┬───┐");
    }

    private static void printMiddleBorder() {
        System.out.println("  ├───┼───┼───┼───┼───┼───┼───┼───┤");
    }

    private static void printBottomBorder() {
        System.out.println("  └───┴───┴───┴───┴───┴───┴───┴───┘");
    }

    private static void printFiles() {
        System.out.println("    a   b   c   d   e   f   g   h");
    }
}
