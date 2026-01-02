package cli;

import board.Board;
import board.Square;
import enums.Color;
import pieces.Piece;

/**
 * Utility class for printing the chess board to the console.
 * Uses ANSI escape codes for colored output with alternating square colors.
 */
public final class BoardPrinter {
    // ANSI escape codes for console colors
    private static final String RESET = "\033[0m";

    private static final String WHITE_BG = "\033[47m";   // white background
    private static final String BLACK_BG = "\033[40m";   // dark gray background

    private static final String WHITE_PIECE = "\033[97m"; // bright white text
    private static final String BLACK_PIECE = "\033[90m"; // dark gray text

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private BoardPrinter() {}

    /**
     * Prints the chess board to the console with colored squares and pieces.
     * Displays file letters (a-h) and rank numbers (1-8) around the board.
     * Uses different background colors for light and dark squares.
     * 
     * @param board The board state to print
     */
    public static void print(Board board) {

        System.out.println("\n   a  b  c  d  e  f  g  h");

        // Print ranks from 8 to 1 (top to bottom)
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print(" " + (rank + 1) + " ");

            for (int file = 0; file < 8; file++) {
                // Determine square color (alternating pattern)
                boolean lightSquare = (rank + file) % 2 == 0;
                String bg = lightSquare ? WHITE_BG : BLACK_BG;

                Square square = new Square(file, rank);
                Piece piece = board.getPieceAt(square);

                if (piece == null) {
                    // Empty square
                    System.out.print(bg + "   " + RESET);
                } else {
                    // Square with piece
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
