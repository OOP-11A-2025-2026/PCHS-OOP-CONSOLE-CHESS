package pieces;
import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece{
    public Pawn(Color color, int file, int rank)
    {
        super(color, PieceType.PAWN, file, rank);
    }

    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        int dir = (color==Color.WHITE)?1:-1;
        int startRank = (color==Color.WHITE)?1:6;
        Square from = new Square(file, rank);
        Square oneForward = new Square(file, rank + dir);
        if(board.isInBounds(oneForward) && board.getPieceAt(oneForward) == null)
        {
            moves.add(new Move(from, oneForward));
            Square twoForward = new Square(file, rank+2*dir);
            if(rank == startRank && board.getPieceAt(twoForward) == null)
            {
                moves.add(new Move(from, twoForward));
            }
        }
        for(int df:new int[]{-1, 1})
        {
            Square diag = new Square(file + df, rank + dir);
            if(!board.isInBounds(diag))
            {
                continue;
            }
            Piece target = board.getPieceAt(diag);
            if(target!=null && target.getColor()!=color)
            {
                moves.add(new Move(from, diag));
            }
            if(target==null && board.getLastMove()!=null)
            {
                moves.add(new Move(from, diag));
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Pawn(this.color, this.file, this.rank);
    }
}
