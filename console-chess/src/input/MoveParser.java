package input;

import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import pieces.Piece;

import java.util.List;

public class MoveParser {

    public static Move parse(String input, Board board, Color color) {
        if (input == null || input.isEmpty()) return null;

        input = input.trim();
        input = input.replace("+", "").replace("#", "");

        if (input.matches("[a-h][1-8]\\s+[a-h][1-8]")) {
            return parseSimple(input);
        }

        return parseAlgebraic(input, board, color);
    }


    private static Move parseSimple(String input) {
        String[] parts = input.split("\\s+");
        Square from = Square.fromString(parts[0]);
        Square to   = Square.fromString(parts[1]);

        if (from == null || to == null) return null;
        return new Move(from, to);
    }

    private static Move parseAlgebraic(
            String input, Board board, Color color) {

        if (Character.isUpperCase(input.charAt(0))) {
            return parsePieceMove(input, board, color);
        }

        return parsePawnMove(input, board, color);
    }

    private static Move parsePieceMove(
            String input, Board board, Color color) {

        PieceType type = PieceType.fromChar(input.charAt(0));
        Square target = Square.fromString(input.substring(1));
        if (type == null || target == null) return null;

        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Square from = new Square(f, r);
                Piece p = board.getPieceAt(from);

                if (p == null) continue;
                if (p.getColor() != color) continue;
                if (p.getType() != type) continue;

                List<Move> moves = p.getLegalMoves(board);
                if (moves == null) continue;

                for (Move m : moves) {
                    if (m.getTo().equals(target)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    private static Move parsePawnMove(
            String input, Board board, Color color) {

        Square target = Square.fromString(input);
        if (target == null) return null;

        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Square from = new Square(f, r);
                Piece p = board.getPieceAt(from);

                if (p == null) continue;
                if (p.getColor() != color) continue;
                if (p.getType() != PieceType.PAWN) continue;

                List<Move> moves = p.getLegalMoves(board);
                if (moves == null) continue;

                for (Move m : moves) {
                    if (m.getTo().equals(target)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
}
