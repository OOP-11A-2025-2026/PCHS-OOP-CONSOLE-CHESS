package board;

import enums.Color;
import enums.PieceType;
import pieces.Piece;

public class Board {
    private Piece[][] squares = new Piece[8][8];

    public Piece getPieceAt(Square square) {
        return squares[square.getRank()][square.getFile()];
    }

    public void setPieceAt(Square square, Piece piece) {
        squares[square.getRank()][square.getFile()] = piece;
    }

    public boolean isInBounds(Square square) {
        if (square == null) return false;
        int f = square.getFile();
        int r = square.getRank();
        return f >= 0 && f < 8 && r >= 0 && r < 8;
    }

    public boolean isOwnPiece(Square square, Color color) {
        if (!isInBounds(square)) return false;
        Piece p = getPieceAt(square);
        return p != null && p.getColor() == color;
    }

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

    public Move getLastMove() {
        return lastMove;
    }

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

        if (moving.getType() == PieceType.KING) {
            int dx = to.getFile() - from.getFile();
            if (dx == 2) {
                int rank = from.getRank();
                Piece rook = getPieceAt(new Square(7, rank));
                if (rook != null && rook.getType() == PieceType.ROOK) {
                    setPieceAt(new Square(5, rank), rook);
                    setPieceAt(new Square(7, rank), null);
                    rook.setPosition(5, rank);
                }
            } else if (dx == -2) {
                int rank = from.getRank();
                Piece rook = getPieceAt(new Square(0, rank));
                if (rook != null && rook.getType() == PieceType.ROOK) {
                    setPieceAt(new Square(3, rank), rook);
                    setPieceAt(new Square(0, rank), null);
                    rook.setPosition(3, rank);
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
    private boolean isPawnPromotion(Piece piece, Square to) {
        if (piece == null) return false;
        if (piece.getType() != PieceType.PAWN) return false;

        if (piece.getColor() == Color.WHITE && to.getRank() == 7) return true;
        if (piece.getColor() == Color.BLACK && to.getRank() == 0) return true;

        return false;
    }


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

    private boolean sameSquare(Square a, Square b) {
        if (a == null || b == null) return false;
        return a.getFile() == b.getFile() && a.getRank() == b.getRank();
    }

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
