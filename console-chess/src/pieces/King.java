package pieces;
import board.*;
import enums.*;
import java.util.*;

public class King extends Piece
{
    private boolean hasMoved = false;
    public King(Color color, int file, int rank)
    {
        super(color, PieceType.KING, file, rank);
    }

    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
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
        if(!hasMoved && !board.isSquareAttacked(from, opposite()))
        {
            int homeRank = (color==Color.WHITE)?0:7;
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

    private Color opposite()
    {
        return(color == Color.WHITE)?Color.BLACK:Color.WHITE;
    }
    public void setHasMoved(boolean value)
    {
        this.hasMoved = value;
    }

    @Override
    public Piece copy() {
        King copy = new King(this.color, this.file, this.rank);
        copy.setHasMoved(this.hasMoved);
        return copy;
    }
}
