package input;

import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import java.util.List;
import pieces.Piece;

/**
 * Parses user input into Move objects.
 * Supports multiple input formats: simple coordinate notation (e.g., "e2 e4"),
 * standard algebraic notation (SAN), and castling notation.
 */
public class MoveParser {

    /**
     * Parses a move string into a Move object.
     * Supports:
     * - Simple notation: "e2 e4" (from-square to-square)
     * - Algebraic notation: "Nf3", "e4", "Bxc6"
     * - Castling: "O-O" (kingside), "O-O-O" (queenside)
     * 
     * @param input The move string to parse
     * @param board The current board state (needed for move validation)
     * @param color The color of the player making the move
     * @return The parsed Move object, or null if parsing fails
     */
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

        // Simple coordinate notation (e.g., "e2 e4")
        if (input.matches("[a-h][1-8]\\s+[a-h][1-8]")) {
            return parseSimple(input);
        }

        // Standard algebraic notation
        return parseAlgebraic(input, board, color);
    }

    /**
     * Parses castling moves.
     * @param board The current board state
     * @param color The color of the player castling
     * @param queenside true for queenside (O-O-O), false for kingside (O-O)
     * @return The castling Move, or null if invalid
     */
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

    /**
     * Parses simple coordinate notation (e.g., "e2 e4").
     * @param input The input string in "from to" format
     * @return The parsed Move, or null if invalid
     */
    private static Move parseSimple(String input) {
        String[] parts = input.split("\\s+");
        Square from = Square.fromString(parts[0]);
        Square to   = Square.fromString(parts[1]);

        if (from == null || to == null) return null;
        return new Move(from, to);
    }

    /**
     * Parses standard algebraic notation.
     * Determines if it's a piece move or pawn move based on first character.
     * 
     * @param input The algebraic notation string
     * @param board The current board state
     * @param color The color of the player moving
     * @return The parsed Move, or null if invalid
     */
    private static Move parseAlgebraic(
            String input, Board board, Color color) {

        if (Character.isUpperCase(input.charAt(0))) {
            return parsePieceMove(input, board, color);
        }

        return parsePawnMove(input, board, color);
    }

    /**
     * Parses a piece move in algebraic notation (e.g., "Nf3", "Bxc6").
     * Finds a matching piece that can legally move to the target square.
     * 
     * @param input The move notation (starts with piece letter)
     * @param board The current board state
     * @param color The color of the player moving
     * @return The parsed Move, or null if invalid
     */
    private static Move parsePieceMove(
            String input, Board board, Color color) {

        PieceType type = PieceType.fromChar(input.charAt(0));
        Square target = Square.fromString(input.substring(1));
        if (type == null || target == null) return null;

        // Search for a piece of the right type that can reach the target
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

    /**
     * Parses a pawn move in algebraic notation (e.g., "e4", "exd5").
     * Finds a pawn that can legally move to the target square.
     * 
     * @param input The move notation (target square only for pawns)
     * @param board The current board state
     * @param color The color of the player moving
     * @return The parsed Move, or null if invalid
     */
    private static Move parsePawnMove(
            String input, Board board, Color color) {

        Square target = Square.fromString(input);
        if (target == null) return null;

        // Search for a pawn that can reach the target
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
