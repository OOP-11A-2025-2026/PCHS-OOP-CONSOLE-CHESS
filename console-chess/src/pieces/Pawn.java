package pieces;
import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Pawn piece in chess.
 * Pawns have unique movement rules: they move forward but capture diagonally.
 * Special moves include double-step from starting position, en passant capture,
 * and promotion when reaching the opposite end of the board.
 */
public class Pawn extends Piece{

    /**
     * Creates a new Pawn piece.
     * @param color The color of the pawn (WHITE or BLACK)
     * @param file The initial file (column) position
     * @param rank The initial rank (row) position
     */
    public Pawn(Color color, int file, int rank)
    {
        super(color, PieceType.PAWN, file, rank);
    }

    /**
     * Generates all legal moves for the Pawn.
     * Includes: single forward move, double forward move from start,
     * diagonal captures, and en passant captures.
     * 
     * @param board The current board state
     * @return List of all legal moves for this pawn
     */
    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        // Direction depends on color: WHITE moves up (+1), BLACK moves down (-1)
        int dir = (color==Color.WHITE)?1:-1;
        int startRank = (color==Color.WHITE)?1:6;
        Square from = new Square(file, rank);

        // Single forward move
        Square oneForward = new Square(file, rank + dir);
        if(board.isInBounds(oneForward) && board.getPieceAt(oneForward) == null)
        {
            moves.add(new Move(from, oneForward));
            // Double forward move from starting position
            Square twoForward = new Square(file, rank+2*dir);
            if(rank == startRank && board.getPieceAt(twoForward) == null)
            {
                moves.add(new Move(from, twoForward));
            }
        }

        // Diagonal captures (including en passant)
        for(int df:new int[]{-1, 1})
        {
            Square diag = new Square(file + df, rank + dir);
            if(!board.isInBounds(diag))
            {
                continue;
            }
            Piece target = board.getPieceAt(diag);
            // Normal diagonal capture
            if(target!=null && target.getColor()!=color)
            {
                moves.add(new Move(from, diag));
            }
            // En passant capture
            if (target == null && board.getLastMove() != null) {
                Move lm = board.getLastMove();
                Square lmFrom = lm.getFrom();
                Square lmTo = lm.getTo();
                if (lmFrom != null && lmTo != null) {
                    Piece lastMoved = board.getPieceAt(lmTo);
                    // Check if last move was a two-square pawn advance
                    if (lastMoved != null && lastMoved.getType() == PieceType.PAWN) {
                        if (Math.abs(lmTo.getRank() - lmFrom.getRank()) == 2) {
                            int passedRank = (lmFrom.getRank() + lmTo.getRank()) / 2;
                            Square passedOver = new Square(lmTo.getFile(), passedRank);
                            // If diagonal move lands on the passed-over square
                            if (passedOver.getFile() == diag.getFile() && passedOver.getRank() == diag.getRank()) {
                                // And the enemy pawn is adjacent
                                if (lmTo.getRank() == from.getRank() && Math.abs(lmTo.getFile() - from.getFile()) == 1) {
                                    moves.add(new Move(from, diag));
                                }
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Creates a copy of this Pawn.
     * @return A new Pawn instance with identical properties
     */
    @Override
    public Piece copy() {
        return new Pawn(this.color, this.file, this.rank);
    }
}
