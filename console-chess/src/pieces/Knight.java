package pieces;
import board.*;
import enums.*;
import java.util.*;

public class Knight extends Piece{
    public Knight(Color color, int file, int rank)
    {
        super(color, PieceType.KNIGHT, file, rank);
    }

    @Override
    public List<Move> getLegalMoves(Board board)
    {
        List<Move> moves = new ArrayList<>();
        Square from = new Square(file, rank);
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

    @Override
    public Piece copy() {
        return new Knight(this.color, this.file, this.rank);
    }
}
