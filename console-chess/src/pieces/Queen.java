package pieces;
import board.*;
import enums.*;
import java.util.*;

/**
 * Represents the Queen piece in chess.
 * The Queen is the most powerful piece, combining the movement abilities
 * of both the Rook and Bishop. It can move any number of squares
 * horizontally, vertically, or diagonally.
 */
public class Queen extends Piece {

    /**
     * Creates a new Queen piece.
     * @param color The color of the queen (WHITE or BLACK)
     * @param file The initial file (column) position
     * @param rank The initial rank (row) position
     */
    public Queen(Color color, int file, int rank)
    {
        super(color, PieceType.QUEEN, file, rank);
    }

    /**
     * Generates all legal moves for the Queen.
     * The Queen can move in 8 directions: horizontally, vertically, and diagonally.
     * Movement is blocked by other pieces; can capture enemy pieces.
     * 
     * @param board The current board state
     * @return List of all legal moves for this queen
     */
    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
        // 8 directions: horizontal, vertical, and diagonal
        int[] df = {1,-1,0,0,1,1,-1,-1};
        int[] dr = {0,0,1,-1,1,-1,1,-1};
        for(int i=0;i<8;i++)
        {
            for(int step=1;step<8;step++)
            {
                Square to = new Square(file+df[i] * step, rank+dr[i] * step);
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
     * Creates a copy of this Queen.
     * @return A new Queen instance with identical properties
     */
    @Override
    public Piece copy() {
        return new Queen(this.color, this.file, this.rank);
    }
}
