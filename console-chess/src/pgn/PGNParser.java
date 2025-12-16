package pgn;

import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pieces.*;

/**
 * Robust PGN parser that can handle any valid SAN move.
 * Parses tags and moves, and applies them to a Board.
 */
public class PGNParser {

    private static final Pattern TAG_PATTERN = Pattern.compile("\\[(\\w+)\\s+\"([^\"]*)\"\\]");

    public Map<String, String> parseTags(String text) {
        Map<String, String> tags = new LinkedHashMap<>();
        Matcher m = TAG_PATTERN.matcher(text);
        while (m.find()) {
            tags.put(m.group(1), m.group(2));
        }
        return tags;
    }

    public List<String> parseMoves(String text) {
        // Remove tags
        String noTags = text.replaceAll("(?s)\\[.*?\\]", " ");
        // Remove comments in {}
        noTags = noTags.replaceAll("(?s)\\{.*?\\}", " ");
        // Remove ; comments
        noTags = noTags.replaceAll(";.*?(\\r?\\n|$)", " ");
        // Remove variations in ()
        noTags = noTags.replaceAll("(?s)\\(.*?\\)", " ");
        // Remove move numbers like "1." or "12..." 
        noTags = noTags.replaceAll("\\d+\\.+", " ");
        // Normalize whitespace
        String[] toks = noTags.trim().split("\\s+");
        List<String> moves = new ArrayList<>();
        for (String t : toks) {
            if (t.isEmpty()) continue;
            // Skip results
            if (t.matches("1-0|0-1|1/2-1/2|\\*")) break;
            moves.add(t);
        }
        return moves;
    }

    public Map<String, Object> parsePGN(String text) {
        Map<String, Object> out = new HashMap<>();
        out.put("tags", parseTags(text));
        out.put("moves", parseMoves(text));
        return out;
    }

    /**
     * Load PGN moves onto a board starting from the initial position.
     */
    public boolean loadToBoard(Board board, String pgnText) {
        setupInitialPosition(board);
        List<String> tokens = parseMoves(pgnText);
        Color toMove = Color.WHITE;
        for (int idx = 0; idx < tokens.size(); idx++) {
            String san = tokens.get(idx);
            Move mv = resolveSAN(board, san, toMove);
            if (mv == null) {
                System.out.println("Failed to resolve SAN at index " + idx + ": " + san);
                printBoard(board);
                return false;
            }
            board.applyMove(mv);
            toMove = (toMove == Color.WHITE) ? Color.BLACK : Color.WHITE;
        }
        return true;
    }

    private void printBoard(Board board) {
        System.out.println("  a b c d e f g h");
        for (int r = 7; r >= 0; r--) {
            System.out.print((r + 1) + " ");
            for (int f = 0; f < 8; f++) {
                Piece p = board.getPieceAt(new Square(f, r));
                if (p == null) {
                    System.out.print(". ");
                } else {
                    char c = switch (p.getType()) {
                        case PAWN -> 'P';
                        case KNIGHT -> 'N';
                        case BISHOP -> 'B';
                        case ROOK -> 'R';
                        case QUEEN -> 'Q';
                        case KING -> 'K';
                    };
                    System.out.print((p.getColor() == Color.WHITE ? c : Character.toLowerCase(c)) + " ");
                }
            }
            System.out.println(r + 1);
        }
        System.out.println("  a b c d e f g h");
    }

    /**
     * Set up the standard chess starting position.
     */
    public void setupInitialPosition(Board board) {
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                board.setPieceAt(new Square(f, r), null);
            }
        }
        // White pieces
        board.setPieceAt(new Square(0, 0), new Rook(Color.WHITE, 0, 0));
        board.setPieceAt(new Square(1, 0), new Knight(Color.WHITE, 1, 0));
        board.setPieceAt(new Square(2, 0), new Bishop(Color.WHITE, 2, 0));
        board.setPieceAt(new Square(3, 0), new Queen(Color.WHITE, 3, 0));
        board.setPieceAt(new Square(4, 0), new King(Color.WHITE, 4, 0));
        board.setPieceAt(new Square(5, 0), new Bishop(Color.WHITE, 5, 0));
        board.setPieceAt(new Square(6, 0), new Knight(Color.WHITE, 6, 0));
        board.setPieceAt(new Square(7, 0), new Rook(Color.WHITE, 7, 0));
        for (int f = 0; f < 8; f++) {
            board.setPieceAt(new Square(f, 1), new Pawn(Color.WHITE, f, 1));
        }
        // Black pieces
        board.setPieceAt(new Square(0, 7), new Rook(Color.BLACK, 0, 7));
        board.setPieceAt(new Square(1, 7), new Knight(Color.BLACK, 1, 7));
        board.setPieceAt(new Square(2, 7), new Bishop(Color.BLACK, 2, 7));
        board.setPieceAt(new Square(3, 7), new Queen(Color.BLACK, 3, 7));
        board.setPieceAt(new Square(4, 7), new King(Color.BLACK, 4, 7));
        board.setPieceAt(new Square(5, 7), new Bishop(Color.BLACK, 5, 7));
        board.setPieceAt(new Square(6, 7), new Knight(Color.BLACK, 6, 7));
        board.setPieceAt(new Square(7, 7), new Rook(Color.BLACK, 7, 7));
        for (int f = 0; f < 8; f++) {
            board.setPieceAt(new Square(f, 6), new Pawn(Color.BLACK, f, 6));
        }
    }

    /**
     * Resolve a SAN string to a concrete Move.
     * Handles: pieces, pawns, captures, promotions, castling, disambiguation.
     */
    public Move resolveSAN(Board board, String san, Color color) {
        if (san == null || san.isEmpty()) return null;

        // Handle castling first
        if (san.startsWith("O-O-O") || san.startsWith("0-0-0")) {
            return resolveCastling(board, color, false); // queenside
        }
        if (san.startsWith("O-O") || san.startsWith("0-0")) {
            return resolveCastling(board, color, true); // kingside
        }

        // Remove check/mate markers and annotations
        String token = san.replaceAll("[+#!?]+$", "");

        // Parse promotion (e.g., e8=Q)
        PieceType promotionType = null;
        int eqIdx = token.indexOf('=');
        if (eqIdx >= 0 && eqIdx < token.length() - 1) {
            char pc = token.charAt(eqIdx + 1);
            promotionType = charToPieceType(pc);
            token = token.substring(0, eqIdx);
        }

        // Determine piece type
        PieceType pieceType = PieceType.PAWN;
        int startIdx = 0;
        if (!token.isEmpty()) {
            char first = token.charAt(0);
            if (first >= 'A' && first <= 'Z' && first != 'O') {
                PieceType parsed = charToPieceType(first);
                if (parsed != null) {
                    pieceType = parsed;
                    startIdx = 1;
                }
            }
        }

        // Remove capture marker
        token = token.substring(startIdx).replace("x", "");

        if (token.length() < 2) return null;

        // Target square is last two characters
        String targetStr = token.substring(token.length() - 2);
        int targetFile = targetStr.charAt(0) - 'a';
        int targetRank = targetStr.charAt(1) - '1';
        if (targetFile < 0 || targetFile > 7 || targetRank < 0 || targetRank > 7) return null;
        Square target = new Square(targetFile, targetRank);

        // Disambiguation (everything before target)
        String disamb = token.substring(0, token.length() - 2);
        Integer disambFile = null;
        Integer disambRank = null;
        for (char c : disamb.toCharArray()) {
            if (c >= 'a' && c <= 'h') disambFile = c - 'a';
            else if (c >= '1' && c <= '8') disambRank = c - '1';
        }

        // Find all candidate pieces
        List<CandidateMove> candidates = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = board.getPieceAt(new Square(f, r));
                if (p == null || p.getColor() != color || p.getType() != pieceType) continue;

                // Check disambiguation
                if (disambFile != null && f != disambFile) continue;
                if (disambRank != null && r != disambRank) continue;

                Square from = new Square(f, r);

                // Check if this piece can reach the target
                if (canReach(board, p, from, target, color)) {
                    Move move = new Move(from, target, promotionType);
                    // Verify the move doesn't leave king in check
                    if (!leavesKingInCheck(board, move, color)) {
                        candidates.add(new CandidateMove(from, target, promotionType));
                    }
                }
            }
        }

        if (candidates.size() == 1) {
            CandidateMove c = candidates.get(0);
            return new Move(c.from, c.to, c.promotion);
        }
        if (candidates.size() > 1) {
            // Multiple candidates - shouldn't happen with proper disambiguation
            // Return the first one
            CandidateMove c = candidates.get(0);
            return new Move(c.from, c.to, c.promotion);
        }

        return null;
    }

    private static class CandidateMove {
        Square from, to;
        PieceType promotion;
        CandidateMove(Square from, Square to, PieceType promotion) {
            this.from = from;
            this.to = to;
            this.promotion = promotion;
        }
    }

    private Move resolveCastling(Board board, Color color, boolean kingside) {
        int rank = (color == Color.WHITE) ? 0 : 7;
        // Find king
        for (int f = 0; f < 8; f++) {
            Piece p = board.getPieceAt(new Square(f, rank));
            if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                int toFile = kingside ? f + 2 : f - 2;
                return new Move(new Square(f, rank), new Square(toFile, rank));
            }
        }
        return null;
    }

    private PieceType charToPieceType(char c) {
        return switch (c) {
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'R' -> PieceType.ROOK;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            case 'P' -> PieceType.PAWN;
            default -> null;
        };
    }

    /**
     * Check if a piece can geometrically reach from 'from' to 'to'.
     * This checks piece movement rules and path clearance.
     */
    private boolean canReach(Board board, Piece piece, Square from, Square to, Color color) {
        int fx = from.getFile(), fy = from.getRank();
        int tx = to.getFile(), ty = to.getRank();
        int dx = tx - fx, dy = ty - fy;
        int adx = Math.abs(dx), ady = Math.abs(dy);

        Piece targetPiece = board.getPieceAt(to);
        // Can't capture own piece
        if (targetPiece != null && targetPiece.getColor() == color) return false;

        switch (piece.getType()) {
            case PAWN -> {
                int dir = (color == Color.WHITE) ? 1 : -1;
                int startRank = (color == Color.WHITE) ? 1 : 6;

                // Capture (diagonal)
                if (adx == 1 && dy == dir) {
                    // Normal capture
                    if (targetPiece != null && targetPiece.getColor() != color) return true;
                    // En passant
                    if (targetPiece == null && isEnPassantTarget(board, from, to, color)) return true;
                    return false;
                }
                // Forward move (no capture)
                if (dx == 0 && targetPiece == null) {
                    if (dy == dir) return true;
                    if (dy == 2 * dir && fy == startRank) {
                        Square mid = new Square(fx, fy + dir);
                        return board.getPieceAt(mid) == null;
                    }
                }
                return false;
            }
            case KNIGHT -> {
                return (adx == 1 && ady == 2) || (adx == 2 && ady == 1);
            }
            case BISHOP -> {
                if (adx != ady || adx == 0) return false;
                return isPathClear(board, from, to);
            }
            case ROOK -> {
                if (dx != 0 && dy != 0) return false;
                if (dx == 0 && dy == 0) return false;
                return isPathClear(board, from, to);
            }
            case QUEEN -> {
                if (dx != 0 && dy != 0 && adx != ady) return false;
                if (dx == 0 && dy == 0) return false;
                return isPathClear(board, from, to);
            }
            case KING -> {
                // Normal king move
                if (adx <= 1 && ady <= 1 && (adx + ady > 0)) return true;
                // Castling handled separately
                return false;
            }
        }
        return false;
    }

    private boolean isEnPassantTarget(Board board, Square from, Square to, Color color) {
        Move lastMove = board.getLastMove();
        if (lastMove == null) return false;

        Square lmFrom = lastMove.getFrom();
        Square lmTo = lastMove.getTo();
        if (lmFrom == null || lmTo == null) return false;

        Piece lastPiece = board.getPieceAt(lmTo);
        if (lastPiece == null || lastPiece.getType() != PieceType.PAWN) return false;

        // Last move was a two-square pawn advance
        if (Math.abs(lmTo.getRank() - lmFrom.getRank()) != 2) return false;

        // The capturing pawn must be adjacent to the target pawn
        if (lmTo.getRank() != from.getRank()) return false;
        if (Math.abs(lmTo.getFile() - from.getFile()) != 1) return false;

        // The target square is where the pawn passed through
        int passedRank = (lmFrom.getRank() + lmTo.getRank()) / 2;
        return to.getFile() == lmTo.getFile() && to.getRank() == passedRank;
    }

    private boolean isPathClear(Board board, Square from, Square to) {
        int fx = from.getFile(), fy = from.getRank();
        int tx = to.getFile(), ty = to.getRank();
        int dx = Integer.compare(tx - fx, 0);
        int dy = Integer.compare(ty - fy, 0);

        int x = fx + dx, y = fy + dy;
        while (x != tx || y != ty) {
            if (board.getPieceAt(new Square(x, y)) != null) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    /**
     * Check if making a move would leave the moving side's king in check.
     */
    private boolean leavesKingInCheck(Board board, Move move, Color color) {
        // Clone board
        Board copy = board.clone();

        // Apply move manually
        Square from = move.getFrom();
        Square to = move.getTo();
        Piece moving = copy.getPieceAt(from);
        if (moving == null) return true;

        // Handle en passant capture
        if (moving.getType() == PieceType.PAWN) {
            int dx = to.getFile() - from.getFile();
            if (dx != 0 && copy.getPieceAt(to) == null) {
                // En passant - remove the captured pawn
                Move lastMove = copy.getLastMove();
                if (lastMove != null) {
                    copy.setPieceAt(lastMove.getTo(), null);
                }
            }
        }

        copy.setPieceAt(to, moving);
        copy.setPieceAt(from, null);

        // Handle promotion
        if (move.getPromotion() != null && moving.getType() == PieceType.PAWN) {
            Piece promoted = createPiece(move.getPromotion(), color, to.getFile(), to.getRank());
            copy.setPieceAt(to, promoted);
        }

        // Find king
        Square kingSquare = null;
        for (int r = 0; r < 8 && kingSquare == null; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = copy.getPieceAt(new Square(f, r));
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    kingSquare = new Square(f, r);
                    break;
                }
            }
        }

        if (kingSquare == null) return false;

        // Check if king is attacked
        Color enemy = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttackedBy(copy, kingSquare, enemy);
    }

    /**
     * Check if a square is attacked by any piece of the given color.
     * Uses direct attack pattern checks to avoid recursion.
     */
    private boolean isSquareAttackedBy(Board board, Square target, Color byColor) {
        int tx = target.getFile(), ty = target.getRank();

        // Pawn attacks
        int pawnDir = (byColor == Color.WHITE) ? 1 : -1;
        for (int df : new int[]{-1, 1}) {
            int pf = tx + df;
            int pr = ty - pawnDir;
            if (pf >= 0 && pf < 8 && pr >= 0 && pr < 8) {
                Piece p = board.getPieceAt(new Square(pf, pr));
                if (p != null && p.getColor() == byColor && p.getType() == PieceType.PAWN) return true;
            }
        }

        // Knight attacks
        int[][] knightMoves = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};
        for (int[] m : knightMoves) {
            int nx = tx + m[0], ny = ty + m[1];
            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                Piece p = board.getPieceAt(new Square(nx, ny));
                if (p != null && p.getColor() == byColor && p.getType() == PieceType.KNIGHT) return true;
            }
        }

        // King attacks (adjacent squares)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int kx = tx + dx, ky = ty + dy;
                if (kx >= 0 && kx < 8 && ky >= 0 && ky < 8) {
                    Piece p = board.getPieceAt(new Square(kx, ky));
                    if (p != null && p.getColor() == byColor && p.getType() == PieceType.KING) return true;
                }
            }
        }

        // Sliding attacks (rook, bishop, queen)
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // orthogonal (rook, queen)
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // diagonal (bishop, queen)
        };

        for (int i = 0; i < directions.length; i++) {
            int dx = directions[i][0], dy = directions[i][1];
            int x = tx + dx, y = ty + dy;
            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                Piece p = board.getPieceAt(new Square(x, y));
                if (p != null) {
                    if (p.getColor() == byColor) {
                        PieceType t = p.getType();
                        boolean isOrthogonal = (i < 4);
                        boolean isDiagonal = (i >= 4);
                        if ((isOrthogonal && (t == PieceType.ROOK || t == PieceType.QUEEN)) ||
                            (isDiagonal && (t == PieceType.BISHOP || t == PieceType.QUEEN))) {
                            return true;
                        }
                    }
                    break; // blocked
                }
                x += dx;
                y += dy;
            }
        }

        return false;
    }

    private Piece createPiece(PieceType type, Color color, int file, int rank) {
        return switch (type) {
            case QUEEN -> new Queen(color, file, rank);
            case ROOK -> new Rook(color, file, rank);
            case BISHOP -> new Bishop(color, file, rank);
            case KNIGHT -> new Knight(color, file, rank);
            case KING -> new King(color, file, rank);
            case PAWN -> new Pawn(color, file, rank);
        };
    }

    /**
     * Generate SAN notation for a move (for export).
     */
    public String moveToSAN(Board board, Move move, Color color) {
        if (move == null) return "";
        Square from = move.getFrom();
        Square to = move.getTo();
        Piece mover = board.getPieceAt(from);
        if (mover == null) return "";

        // Castling
        if (mover.getType() == PieceType.KING && Math.abs(to.getFile() - from.getFile()) == 2) {
            return (to.getFile() > from.getFile()) ? "O-O" : "O-O-O";
        }

        StringBuilder sb = new StringBuilder();

        // Piece letter (not for pawns)
        if (mover.getType() != PieceType.PAWN) {
            sb.append(pieceToChar(mover.getType()));

            // Disambiguation
            boolean needFile = false, needRank = false;
            for (int r = 0; r < 8; r++) {
                for (int f = 0; f < 8; f++) {
                    if (f == from.getFile() && r == from.getRank()) continue;
                    Piece other = board.getPieceAt(new Square(f, r));
                    if (other == null || other.getType() != mover.getType() || other.getColor() != color) continue;
                    if (canReach(board, other, new Square(f, r), to, color)) {
                        if (!leavesKingInCheck(board, new Move(new Square(f, r), to), color)) {
                            if (f != from.getFile()) needFile = true;
                            else needRank = true;
                        }
                    }
                }
            }
            if (needFile) sb.append((char) ('a' + from.getFile()));
            if (needRank) sb.append((char) ('1' + from.getRank()));
        } else {
            // Pawn capture needs file
            if (from.getFile() != to.getFile()) {
                sb.append((char) ('a' + from.getFile()));
            }
        }

        // Capture
        Piece captured = board.getPieceAt(to);
        if (captured != null || (mover.getType() == PieceType.PAWN && from.getFile() != to.getFile())) {
            sb.append('x');
        }

        // Target square
        sb.append((char) ('a' + to.getFile()));
        sb.append((char) ('1' + to.getRank()));

        // Promotion
        if (move.getPromotion() != null) {
            sb.append('=').append(pieceToChar(move.getPromotion()));
        }

        return sb.toString();
    }

    private char pieceToChar(PieceType type) {
        return switch (type) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> 'P';
        };
    }

    public static String readFile(Path p) throws IOException {
        return Files.readString(p);
    }
}
