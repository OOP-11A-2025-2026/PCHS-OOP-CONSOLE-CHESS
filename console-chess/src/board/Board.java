package board;

import enums.Color;
import enums.PieceType;
import pieces.Piece;

/**
 * Represents the chess board and manages piece positions and moves.
 * The board is an 8x8 grid with files (columns) a-h and ranks (rows) 1-8.
 */
public class Board {
    private Piece[][] squares = new Piece[8][8];

    /**
     * Gets the piece at the specified square.
     * @param square The square to check
     * @return The piece at that square, or null if empty
     */
    public Piece getPieceAt(Square square) {
        return squares[square.getRank()][square.getFile()];
    }

    /**
     * Places a piece at the specified square.
     * @param square The square to place the piece on
     * @param piece The piece to place (can be null to clear the square)
     */
    public void setPieceAt(Square square, Piece piece) {
        squares[square.getRank()][square.getFile()] = piece;
    }

    /**
     * Checks if a square is within the board boundaries.
     * @param square The square to check
     * @return true if the square is valid (0-7 for both file and rank)
     */
    public boolean isInBounds(Square square) {
        if (square == null) return false;
        int f = square.getFile();
        int r = square.getRank();
        return f >= 0 && f < 8 && r >= 0 && r < 8;
    }

    /**
     * Checks if a square contains a piece of the specified color.
     * @param square The square to check
     * @param color The color to match
     * @return true if the square contains a piece of the given color
     */
    public boolean isOwnPiece(Square square, Color color) {
        if (!isInBounds(square)) return false;
        Piece p = getPieceAt(square);
        return p != null && p.getColor() == color;
    }

    /**
     * Checks if the path between two squares is clear of pieces.
     * Works for straight lines (horizontal, vertical, diagonal).
     * @param from The starting square
     * @param to The destination square
     * @return true if all squares between from and to are empty
     */
    public boolean isPathClear(Square from, Square to) {
        if (from == null || to == null) return false;
        if (!isInBounds(from) || !isInBounds(to)) return false;

        int df = to.getFile() - from.getFile();
        int dr = to.getRank() - from.getRank();

        int stepF = Integer.compare(df, 0);
        int stepR = Integer.compare(dr, 0);

        if (stepF != 0 && stepR != 0 && Math.abs(df) != Math.abs(dr)) {
            return false;
        }

        int curF = from.getFile() + stepF;
        int curR = from.getRank() + stepR;

        while (curF != to.getFile() || curR != to.getRank()) {
            Piece p = squares[curR][curF];
            if (p != null) return false;
            curF += stepF;
            curR += stepR;
        }
        return true;
    }

    /**
     * Creates a deep copy of this board, including all pieces and the last move.
     * @return A new Board instance with copies of all pieces
     */
    public Board clone() {
        Board b = new Board();
        b.squares = new Piece[8][8];

        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = this.squares[r][f];
                if (p != null)
                    b.squares[r][f] = p.copy();
            }
        }
        if (this.lastMove != null)
            b.lastMove = new Move(
                    new Square(lastMove.getFrom().getFile(), lastMove.getFrom().getRank()),
                    new Square(lastMove.getTo().getFile(), lastMove.getTo().getRank())
            );

        return b;
    }


    private Move lastMove = null;

    /**
     * Gets the last move made on this board.
     * @return The last Move, or null if no moves have been made
     */
    public Move getLastMove() {
        return lastMove;
    }

    /**
     * Applies a move to the board, handling all special cases.
     * Handles: regular moves, captures, en passant, castling, and pawn promotion.
     * Updates piece positions and tracks moved pieces for castling rights.
     * 
     * @param move The move to apply
     * @return The captured piece, or null if no capture occurred
     */
    public Piece applyMove(Move move) {
        if (move == null) return null;
        Square from = move.getFrom();
        Square to = move.getTo();
        if (!isInBounds(from) || !isInBounds(to)) return null;

        Piece moving = getPieceAt(from);
        if (moving == null) return null;

        Piece captured = null;

        if (isEnPassantMove(move, moving)) {
            Square lastLanding = lastMove.getTo();
            captured = getPieceAt(lastLanding);
            setPieceAt(lastLanding, null);
        } else {
            captured = getPieceAt(to);
        }

        setPieceAt(to, moving);
        setPieceAt(from, null);
        moving.setPosition(to.getFile(), to.getRank());

        // Track hasMoved for castling purposes
        if (moving.getType() == PieceType.KING && moving instanceof pieces.King) {
            ((pieces.King) moving).setHasMoved(true);
        }
        if (moving.getType() == PieceType.ROOK && moving instanceof pieces.Rook) {
            ((pieces.Rook) moving).setHasMoved(true);
        }

        if (moving.getType() == PieceType.KING) {
            int dx = to.getFile() - from.getFile();
            if (dx == 2) {
                int rank = from.getRank();
                Piece rook = getPieceAt(new Square(7, rank));
                if (rook != null && rook.getType() == PieceType.ROOK) {
                    setPieceAt(new Square(5, rank), rook);
                    setPieceAt(new Square(7, rank), null);
                    rook.setPosition(5, rank);
                    if (rook instanceof pieces.Rook) {
                        ((pieces.Rook) rook).setHasMoved(true);
                    }
                }
            } else if (dx == -2) {
                int rank = from.getRank();
                Piece rook = getPieceAt(new Square(0, rank));
                if (rook != null && rook.getType() == PieceType.ROOK) {
                    setPieceAt(new Square(3, rank), rook);
                    setPieceAt(new Square(0, rank), null);
                    rook.setPosition(3, rank);
                    if (rook instanceof pieces.Rook) {
                        ((pieces.Rook) rook).setHasMoved(true);
                    }
                }
            }
        }

        PieceType promoType = move.getPromotion();
        if (promoType != null && moving.getType() == PieceType.PAWN) {
            Piece promoted = null;
            int file = to.getFile();
            int rank = to.getRank();
            switch (promoType) {
                case QUEEN -> promoted = new pieces.Queen(moving.getColor(), file, rank);
                case ROOK -> promoted = new pieces.Rook(moving.getColor(), file, rank);
                case BISHOP -> promoted = new pieces.Bishop(moving.getColor(), file, rank);
                case KNIGHT -> promoted = new pieces.Knight(moving.getColor(), file, rank);
                default -> promoted = new pieces.Queen(moving.getColor(), file, rank);
            }
            setPieceAt(to, promoted);
        } else if (isPawnPromotion(moving, to)) {
            Piece promoted = PawnPromotionHandler.promote(moving.getColor(), to);
            setPieceAt(to, promoted);
        }
        lastMove = move;
        return captured;
    }

    /**
     * Checks if a pawn should be promoted (reached the last rank).
     * @param piece The piece to check
     * @param to The destination square
     * @return true if the piece is a pawn reaching the promotion rank
     */
    private boolean isPawnPromotion(Piece piece, Square to) {
        if (piece == null) return false;
        if (piece.getType() != PieceType.PAWN) return false;

        if (piece.getColor() == Color.WHITE && to.getRank() == 7) return true;
        if (piece.getColor() == Color.BLACK && to.getRank() == 0) return true;

        return false;
    }


    /**
     * Checks if a square is under attack by pieces of a given color.
     * Considers all piece types: pawns, knights, bishops, rooks, queens, and kings.
     * 
     * @param target The square to check
     * @param byColor The attacking color
     * @return true if any piece of byColor can attack the target square
     */
    public boolean isSquareAttacked(Square target, Color byColor) {
        if (target == null) return false;
        int tx = target.getFile();
        int ty = target.getRank();

        // Pawn attacks
        int pawnDir = (byColor == Color.WHITE) ? 1 : -1;
        int[] pFiles = new int[]{tx - 1, tx + 1};
        for (int pf : pFiles) {
            int pr = ty - pawnDir;
            if (pf >= 0 && pf < 8 && pr >= 0 && pr < 8) {
                Piece p = squares[pr][pf];
                if (p != null && p.getColor() == byColor && p.getType() == PieceType.PAWN) return true;
            }
        }

        int[][] knightOffsets = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};
        for (int[] o : knightOffsets) {
            int fx = tx + o[0];
            int ry = ty + o[1];
            if (fx >= 0 && fx < 8 && ry >= 0 && ry < 8) {
                Piece p = squares[ry][fx];
                if (p != null && p.getColor() == byColor && p.getType() == PieceType.KNIGHT) return true;
            }
        }

        for (int df = -1; df <= 1; df++) for (int dr = -1; dr <= 1; dr++) {
            if (df == 0 && dr == 0) continue;
            int fx = tx + df;
            int ry = ty + dr;
            if (fx >= 0 && fx < 8 && ry >= 0 && ry < 8) {
                Piece p = squares[ry][fx];
                if (p != null && p.getColor() == byColor && p.getType() == PieceType.KING) return true;
            }
        }

        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int i = 0; i < directions.length; i++) {
            int df = directions[i][0];
            int dr = directions[i][1];
            int fx = tx + df;
            int ry = ty + dr;
            while (fx >= 0 && fx < 8 && ry >= 0 && ry < 8) {
                Piece p = squares[ry][fx];
                if (p != null) {
                    if (p.getColor() == byColor) {
                        PieceType t = p.getType();
                        boolean orth = (i < 4);
                        boolean diag = (i >= 4);
                        if ((orth && (t == PieceType.ROOK || t == PieceType.QUEEN)) ||
                                (diag && (t == PieceType.BISHOP || t == PieceType.QUEEN))) {
                            return true;
                        }
                    }
                    break;
                }
                fx += df; ry += dr;
            }
        }

        return false;
    }

    /**
     * Checks if two squares represent the same position.
     * @param a First square
     * @param b Second square
     * @return true if both squares have the same file and rank
     */
    private boolean sameSquare(Square a, Square b) {
        if (a == null || b == null) return false;
        return a.getFile() == b.getFile() && a.getRank() == b.getRank();
    }

    /**
     * Determines if a move is an en passant capture.
     * En passant is a special pawn capture that occurs when an opponent's pawn
     * moves two squares forward and lands beside your pawn.
     * 
     * @param move The move to check
     * @param moving The piece making the move
     * @return true if this is a valid en passant capture
     */
    private boolean isEnPassantMove(Move move, Piece moving) {
        if (moving == null || move == null) return false;
        if (moving.getType() != PieceType.PAWN) return false;
        if (lastMove == null) return false;

        Square from = move.getFrom();
        Square to = move.getTo();
        if (from == null || to == null) return false;

        int df = to.getFile() - from.getFile();
        int dr = to.getRank() - from.getRank();

        if (Math.abs(df) != 1) return false;
        if (Math.abs(dr) != 1) return false;

        if (getPieceAt(to) != null) return false;

        Square lmFrom = lastMove.getFrom();
        Square lmTo = lastMove.getTo();
        if (lmFrom == null || lmTo == null) return false;
        Piece lastMovedPiece = getPieceAt(lmTo);
        if (lastMovedPiece == null || lastMovedPiece.getType() != PieceType.PAWN) return false;
        if (Math.abs(lmTo.getRank() - lmFrom.getRank()) != 2) return false;

        int passedRank = (lmFrom.getRank() + lmTo.getRank()) / 2;
        Square passedOver = new Square(lmTo.getFile(), passedRank);

        if (sameSquare(to, passedOver)) {
            if (lmTo.getRank() == from.getRank() && Math.abs(lmTo.getFile() - from.getFile()) == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simulate the given move on a clone of this board and detect whether
     * the moving side's king would be left in check.
     *
     * @param move the move to simulate
     * @param movingColor the color performing the move
     * @return true if after applying the move the moving side's king is under attack
     */
    public boolean simulateMoveAndDetectSelfCheck(Move move, Color movingColor) {
        if (move == null || movingColor == null) return false;
        Board copy = this.clone();

        Square from = move.getFrom();
        Square to = move.getTo();
        if (from == null || to == null) return false;
        Move copyMove = new Move(new Square(from.getFile(), from.getRank()), new Square(to.getFile(), to.getRank()));

        copy.applyMove(copyMove);

        Square kingSquare = null;
        for (int r = 0; r < 8 && kingSquare == null; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = copy.getPieceAt(new Square(f, r));
                if (p != null && p.getColor() == movingColor && p.getType() == PieceType.KING) {
                    kingSquare = new Square(f, r);
                    break;
                }
            }
        }

        if (kingSquare == null) {
            return false;
        }

        Color opponent = (movingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return copy.isSquareAttacked(kingSquare, opponent);
    }


}
