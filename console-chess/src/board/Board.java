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
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                b.squares[r][f] = this.squares[r][f];
            }
        }
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
        if (isPawnPromotion(moving, to)) {
            Piece promoted =
                PawnPromotionHandler.promote(moving.getColor(), to);
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
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                Piece p = squares[r][f];
                if (p == null) continue;
                if (p.getColor() != byColor) continue;
                java.util.List<Move> moves = p.getLegalMoves(this);
                if (moves == null) continue;
                for (Move m : moves) {
                    Square t = m.getTo();
                    if (t != null && t.getFile() == target.getFile() && t.getRank() == target.getRank()) {
                        return true;
                    }
                }
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
}
