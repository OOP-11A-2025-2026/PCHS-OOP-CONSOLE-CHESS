package input;

import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import java.util.List;
import pieces.Piece;

public class MoveParser {

    public static Move parse(String input, Board board, Color color) {
        if (input == null || input.isEmpty()) return null;

        input = input.trim();
        input = input.replace("+", "").replace("#", "");

        // Handle castling notation
        if (input.equalsIgnoreCase("O-O-O") || input.equals("0-0-0")) {
            return parseCastling(board, color, true); // Queenside (long)
        }
        if (input.equalsIgnoreCase("O-O") || input.equals("0-0")) {
            return parseCastling(board, color, false); // Kingside (short)
        }

        if (input.matches("[a-h][1-8]\\s+[a-h][1-8]")) {
            return parseSimple(input);
        }

        return parseAlgebraic(input, board, color);
    }

    private static Move parseCastling(Board board, Color color, boolean queenside) {
        int homeRank = (color == Color.WHITE) ? 0 : 7;
        Square kingFrom = new Square(4, homeRank);
        
        // Check if king is at starting position
        Piece king = board.getPieceAt(kingFrom);
        if (king == null || king.getType() != PieceType.KING || king.getColor() != color) {
            return null;
        }

        // Determine target square for the king
        Square kingTo;
        if (queenside) {
            kingTo = new Square(2, homeRank); // c1 or c8
        } else {
            kingTo = new Square(6, homeRank); // g1 or g8
        }

        return new Move(kingFrom, kingTo);
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
