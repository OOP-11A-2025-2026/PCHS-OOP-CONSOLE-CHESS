package board;

import pieces.Piece;

public class Board {
    private Piece[][] squares = new Piece[8][8];

    public Piece getPieceAt(Square square) {
        return squares[square.getRank()][square.getFile()];
    }

    public void setPieceAt(Square square, Piece piece) {
        squares[square.getRank()][square.getFile()] = piece;
    }
}
