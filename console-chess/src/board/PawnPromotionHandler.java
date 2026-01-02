package board;

import enums.Color;
import java.util.Scanner;
import pieces.*;

/**
 * Handles pawn promotion logic in the chess game.
 * When a pawn reaches the opposite end of the board, it must be promoted to another piece.
 */
public class PawnPromotionHandler {

    /**
     * Prompts the user to select a piece for pawn promotion and creates the promoted piece.
     * Valid promotion choices are: Queen, Rook, Bishop, or Knight.
     * 
     * @param color The color of the pawn being promoted
     * @param square The square where the promotion occurs
     * @return The newly created promoted piece
     * @throws IllegalStateException if an invalid promotion choice is made
     */
    public static Piece promote(Color color, Square square) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose what to promote the pawn to:");
        System.out.println("1 - Queen");
        System.out.println("2 - Tower");
        System.out.println("3 - Bishop");
        System.out.println("4 - Horse");

        int choice;
        do {
            System.out.print("> ");
            choice = scanner.nextInt();
        } while (choice < 1 || choice > 4);

        int file = square.getFile();
        int rank = square.getRank();

        return switch (choice) {
            case 1 -> new Queen(color, file, rank);
            case 2 -> new Rook(color, file, rank);
            case 3 -> new Bishop(color, file, rank);
            case 4 -> new Knight(color, file, rank);
            default -> throw new IllegalStateException("Invalid promotion choice");
        };
    }
}
