package board;

import enums.Color;
import java.util.Scanner;
import pieces.*;

public class PawnPromotionHandler {

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
