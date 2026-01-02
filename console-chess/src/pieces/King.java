package pieces;
import board.*;
import enums.*;
import java.util.*;

/**
 * Represents the King piece in chess.
 * The King can move one square in any direction and can also castle
 * under specific conditions (king and rook haven't moved, no pieces between,
 * king doesn't pass through or end in check).
 */
public class King extends Piece
{
    private boolean hasMoved = false;

    /**
     * Creates a new King piece.
     * @param color The color of the king (WHITE or BLACK)
     * @param file The initial file (column) position
     * @param rank The initial rank (row) position
     */
    public King(Color color, int file, int rank)
    {
        super(color, PieceType.KING, file, rank);
    }

    /**
     * Generates all legal moves for the King.
     * Includes normal one-square moves in all directions and castling moves.
     * Castling is available if: king hasn't moved, target rook hasn't moved,
     * path is clear, and king doesn't move through or into check.
     * 
     * @param board The current board state
     * @return List of all legal moves for this king
     */
    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
        // Check all 8 surrounding squares
        for(int df=-1;df<=1;df++)
        {
            for(int dr=-1;dr<=1;dr++)
            {
                if(df==0 && dr==0)
                {
                    continue;
                }
                Square to = new Square(file+df,rank+dr);
                if(!board.isInBounds(to))
                {
                    continue;
                }
                if(!board.isOwnPiece(to, color))
                {
                    moves.add(new Move(from, to));
                }
            }
        }
        // Castling logic
        if(!hasMoved && !board.isSquareAttacked(from, opposite()))
        {
            int homeRank = (color==Color.WHITE)?0:7;
            // Kingside castling (O-O)
            Square f = new Square(5, homeRank);
            Square g = new Square(6, homeRank);
            Square h = new Square(7, homeRank);
            Piece rookH = board.getPieceAt(h);
            if(board.getPieceAt(f) == null &&
                    board.getPieceAt(g) == null &&
                    rookH instanceof Rook &&
                    !((Rook)rookH).hasMoved() &&
                    !board.isSquareAttacked(f, opposite()) &&
                    !board.isSquareAttacked(g, opposite()))
            {
                moves.add(new Move(from, g));
            }
            // Queenside castling (O-O-O)
            Square d = new Square(3, homeRank);
            Square c = new Square(2, homeRank);
            Square b = new Square(1, homeRank);
            Square a = new Square(0, homeRank);
            Piece rookA = board.getPieceAt(a);
            if(board.getPieceAt(d) == null &&
                    board.getPieceAt(c) == null &&
                    board.getPieceAt(b) == null &&
                    rookA instanceof Rook &&
                    !((Rook)rookA).hasMoved() &&
                    !board.isSquareAttacked(d, opposite()) &&
                    !board.isSquareAttacked(c, opposite()))
            {
                moves.add(new Move(from, c));
            }
        }
        return moves;
    }

    /**
     * Gets the opponent's color.
     * @return BLACK if this king is WHITE, WHITE if this king is BLACK
     */
    private Color opposite()
    {
        return(color == Color.WHITE)?Color.BLACK:Color.WHITE;
    }

    /**
     * Sets whether this king has moved (affects castling rights).
     * @param value true if the king has moved
     */
    public void setHasMoved(boolean value)
    {
        this.hasMoved = value;
    }

    /**
     * Creates a copy of this King with the same state.
     * @return A new King instance with identical properties
     */
    @Override
    public Piece copy() {
        King copy = new King(this.color, this.file, this.rank);
        copy.setHasMoved(this.hasMoved);
        return copy;
    }
}
