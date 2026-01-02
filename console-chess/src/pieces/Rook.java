package pieces;
import board.*;
import enums.*;
import java.util.*;

/**
 * Represents the Rook piece in chess.
 * The Rook can move any number of squares horizontally or vertically.
 * Also participates in castling with the King.
 */
public class Rook extends Piece{
    private boolean hasMoved = false;
    
    /**
     * Creates a new Rook piece.
     * @param color The color of the rook (WHITE or BLACK)
     * @param file The initial file (column) position
     * @param rank The initial rank (row) position
     */
    public Rook(Color color, int file, int rank) {
        super(color, PieceType.ROOK, file, rank);
    }
    
    /**
     * Checks if this rook has moved (affects castling rights).
     * @return true if the rook has moved from its starting position
     */
    public boolean hasMoved() {
        return hasMoved;
    }
    
    /**
     * Sets whether this rook has moved.
     * @param value true if the rook has moved
     */
    public void setHasMoved(boolean value) {
        this.hasMoved = value;
    }

    /**
     * Generates all legal moves for the Rook.
     * The Rook can move horizontally or vertically any number of squares.
     * Movement is blocked by other pieces; can capture enemy pieces.
     * 
     * @param board The current board state
     * @return List of all legal moves for this rook
     */
    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
        // 4 directions: right, left, up, down
        int[] df = {1, -1,0,0};
        int[] dr = {0,0, 1,-1};
        for(int i=0;i<4;i++)
        {
            for (int step = 1; step < 8; step++)
            {
                Square to = new Square(file+df[i]*step,rank+dr[i]*step);
                if(!board.isInBounds(to))
                {
                    break;
                }
                if(!board.isPathClear(from, to))
                {
                    break;
                }
                if(!board.isOwnPiece(to, color))
                {
                    moves.add(new Move(from, to));
                }
                // Stop if we hit any piece
                if(board.getPieceAt(to)!=null)
                {
                    break;
                }
            }
        }
        return moves;
    }

    /**
     * Creates a copy of this Rook with the same state.
     * @return A new Rook instance with identical properties
     */
    @Override
    public Piece copy() {
        Rook copy = new Rook(this.color, this.file, this.rank);
        copy.setHasMoved(this.hasMoved);
        return copy;
    }
}
