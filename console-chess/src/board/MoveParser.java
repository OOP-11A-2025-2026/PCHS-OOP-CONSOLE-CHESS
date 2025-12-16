package board;

import board.Move;
import board.Square;

public class MoveParser {

    public static Move parseSimple(String input) {
        // e2 e4
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) return null;

        Square from = Square.fromString(parts[0]);
        Square to   = Square.fromString(parts[1]);

        if (from == null || to == null) return null;

        return new Move(from, to);
    }
}
