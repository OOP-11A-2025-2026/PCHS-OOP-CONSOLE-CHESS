package pieces;
import board.*;
import enums.*;
import java.util.*;

/**
 * Represents the Bishop piece in chess.
 * The Bishop can move any number of squares diagonally.
 * Each bishop is confined to squares of one color throughout the game.
 */
public class Bishop extends Piece {

    /**
     * Creates a new Bishop piece.
     * @param color The color of the bishop (WHITE or BLACK)
     * @param file The initial file (column) position
     * @param rank The initial rank (row) position
     */
    public Bishop(Color color, int file, int rank)
    {
        super(color, PieceType.BISHOP, file, rank);
    }

    /**
     * Generates all legal moves for the Bishop.
     * The Bishop can move diagonally in 4 directions any number of squares.
     * Movement is blocked by other pieces; can capture enemy pieces.
     * 
     * @param board The current board state
     * @return List of all legal moves for this bishop
     */
    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
        // 4 diagonal directions
        int[] df = {1,1,-1,-1};
        int[] dr = {1,-1,1,-1};
        for(int i=0;i<4;i++)
        {
            for(int step=1;step<8;step++)
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
     * Creates a copy of this Bishop.
     * @return A new Bishop instance with identical properties
     */
    @Override
    public Piece copy() {
        return new Bishop(this.color, this.file, this.rank);
    }

}
