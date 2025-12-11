package pieces;
import board.*;
import enums.*;
import java.util.*;

public class Rook extends Piece{
    public Rook(Color color, int file, int rank) {
        super(color, PieceType.ROOK, file, rank);
    }

    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
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
                if(board.getPieceAt(to)!=null)
                {
                    break;
                }
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Rook(this.color, this.file, this.rank);
    }
}
