package pieces;
import board.*;
import enums.*;
import java.util.*;

/**
 * Represents the Knight piece in chess.
 * The Knight moves in an "L" shape: two squares in one direction
 * and one square perpendicular to that. Knights can jump over other pieces.
 */
public class Knight extends Piece{

    /**
     * Creates a new Knight piece.
     * @param color The color of the knight (WHITE or BLACK)
     * @param file The initial file (column) position
     * @param rank The initial rank (row) position
     */
    public Knight(Color color, int file, int rank)
    {
        super(color, PieceType.KNIGHT, file, rank);
    }

    /**
     * Generates all legal moves for the Knight.
     * The Knight moves in an L-shape pattern and can jump over pieces.
     * 8 possible moves: 2 squares in one direction + 1 square perpendicular.
     * 
     * @param board The current board state
     * @return List of all legal moves for this knight
     */
    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
        // 8 L-shaped move offsets
        int[] df = {1,2,2,1, -1,-2,-2,-1};
        int[] dr = {2,1,-1,-2,-2,-1,1, 2};
        for (int i = 0; i < 8; i++) {
            Square to = new Square(file+df[i],rank+dr[i]);
            if(!board.isInBounds(to))
            {
                continue;
            }
            if(!board.isOwnPiece(to, color))
            {
                moves.add(new Move(from, to));
            }
        }
        return moves;
    }

    /**
     * Creates a copy of this Knight.
     * @return A new Knight instance with identical properties
     */
    @Override
    public Piece copy() {
        return new Knight(this.color, this.file, this.rank);
    }
}
