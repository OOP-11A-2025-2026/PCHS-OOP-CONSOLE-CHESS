package game;

import board.Board;
import board.Move;
import board.Square;
import enums.Color;
import enums.PieceType;
import java.util.ArrayList;
import java.util.List;
import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.Queen;
import pieces.Rook;

public class Game {
    private Board board;
    private Color currentPlayer;
    private GameState state;
    private boolean drawOffered;
    private Color drawOfferedBy;
    private List<String> moveHistory = new ArrayList<>();

    public enum GameState {
        ONGOING,
        CHECK,
        CHECKMATE,
        STALEMATE,
        DRAW,
        RESIGNED
    }

    public Game() {
        this.board = new Board();
        this.currentPlayer = Color.WHITE;
        this.state = GameState.ONGOING;
        this.drawOffered = false;
        this.drawOfferedBy = null;
        initializeBoard();
    }

    private void initializeBoard() {
        for (int file = 0; file < 8; file++){
            board.setPieceAt(new Square(file, 1), new Pawn(Color.WHITE, file, 1));
            board.setPieceAt(new Square(file, 6), new Pawn(Color.BLACK, file, 6));
        }

        board.setPieceAt(new Square(0, 0), new Rook(Color.WHITE, 0, 0));
        board.setPieceAt(new Square(7, 0), new Rook(Color.WHITE, 7, 0));
        board.setPieceAt(new Square(1, 0), new Knight(Color.WHITE, 1, 0));
        board.setPieceAt(new Square(6, 0), new Knight(Color.WHITE, 6, 0));
        board.setPieceAt(new Square(2, 0), new Bishop(Color.WHITE, 2, 0));
        board.setPieceAt(new Square(5, 0), new Bishop(Color.WHITE, 5, 0));
        board.setPieceAt(new Square(3, 0), new Queen(Color.WHITE, 3, 0));
        board.setPieceAt(new Square(4, 0), new King(Color.WHITE, 4, 0));

        board.setPieceAt(new Square(0, 7), new Rook(Color.BLACK, 0, 7));
        board.setPieceAt(new Square(7, 7), new Rook(Color.BLACK, 7, 7));
        board.setPieceAt(new Square(1, 7), new Knight(Color.BLACK, 1, 7));
        board.setPieceAt(new Square(6, 7), new Knight(Color.BLACK, 6, 7));
        board.setPieceAt(new Square(2, 7), new Bishop(Color.BLACK, 2, 7));
        board.setPieceAt(new Square(5, 7), new Bishop(Color.BLACK, 5, 7));
        board.setPieceAt(new Square(3, 7), new Queen(Color.BLACK, 3, 7));
        board.setPieceAt(new Square(4, 7), new King(Color.BLACK, 4, 7));
    }

    public Board getBoard() {
        return board;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Color color) {
        this.currentPlayer = color;
    }

    public GameState getState() {
        return state;
    }

    public boolean isDrawOffered() {
        return drawOffered;
    }

    public Color getDrawOfferedBy() {
        return drawOfferedBy;
    }

    public void offerDraw() {
        drawOffered = true;
        drawOfferedBy = currentPlayer;
    }

    public void acceptDraw() {
        if (drawOffered && drawOfferedBy != currentPlayer) {
            state = GameState.DRAW;
        }
    }

    public void declineDraw() {
        drawOffered = false;
        drawOfferedBy = null;
    }

    public void resign() {
        state = GameState.RESIGNED;
    }

    public boolean makeMove(Move move) {
        if (move == null) return false;

        if (state == GameState.CHECKMATE ||
                state == GameState.STALEMATE ||
                state == GameState.DRAW ||
                state == GameState.RESIGNED) {
            return false;
        }

        Square from = move.getFrom();
        Square to = move.getTo();

        Piece piece = board.getPieceAt(from);

        if (piece == null || piece.getColor() != currentPlayer) {
            return false;
        }

        boolean legal = false;
        for (Move m : piece.getLegalMoves(board)) {
            if (m.getTo().getFile() == to.getFile() &&
                    m.getTo().getRank() == to.getRank()) {
                legal = true;
                break;
            }
        }
        if (!legal) return false;

        Board testBoard = board.clone();
        testBoard.applyMove(move);
        Square kingSquare = findKing(testBoard, currentPlayer);
        if (kingSquare != null &&
                testBoard.isSquareAttacked(kingSquare, getOpponentColor(currentPlayer))) {
            return false;
        }

        // Record move in SAN notation before applying
        String san = moveToSAN(move, piece);
        
        board.applyMove(move);
        moveHistory.add(san);

        if (drawOffered && drawOfferedBy != currentPlayer) {
            declineDraw();
        }

        currentPlayer = getOpponentColor(currentPlayer);

        updateGameState();

        return true;
    }


    private void updateGameState() {
        Square kingSquare = findKing(board, currentPlayer);
        if (kingSquare == null) {
            return;
        }

        boolean inCheck = board.isSquareAttacked(kingSquare, getOpponentColor(currentPlayer));
        boolean hasLegalMoves = hasAnyLegalMoves(currentPlayer);

        if (inCheck) {
            if (!hasLegalMoves) {
                state = GameState.CHECKMATE;
            } else {
                state = GameState.CHECK;
            }
        } else {
            if (!hasLegalMoves) {
                state = GameState.STALEMATE;
            } else {
                state = GameState.ONGOING;
            }
        }
    }

    private boolean hasAnyLegalMoves(Color color) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = new Square(file, rank);
                Piece piece = board.getPieceAt(square);
                if (piece != null && piece.getColor() == color) {
                    java.util.List<Move> moves = piece.getLegalMoves(board);
                    for (Move move : moves) {
                        Board testBoard = board.clone();
                        testBoard.applyMove(move);
                        Square kingSquare = findKing(testBoard, color);
                        if (kingSquare != null && !testBoard.isSquareAttacked(kingSquare, getOpponentColor(color))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Square findKing(Board board, Color color) {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = new Square(file, rank);
                Piece piece = board.getPieceAt(square);
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == color) {
                    return square;
                }
            }
        }
        return null;
    }

    private Color getOpponentColor(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    public String getWinner() {
        if (state == GameState.CHECKMATE || state == GameState.RESIGNED) {
            return currentPlayer == Color.WHITE ? "Black" : "White";
        }
        return null;
    }

    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public void setMoveHistory(List<String> history) {
        this.moveHistory = new ArrayList<>(history);
    }

    private String moveToSAN(Move move, Piece piece) {
        Square from = move.getFrom();
        Square to = move.getTo();
        StringBuilder sb = new StringBuilder();

        if (piece.getType() == PieceType.KING && Math.abs(to.getFile() - from.getFile()) == 2) {
            return to.getFile() > from.getFile() ? "O-O" : "O-O-O";
        }

        if (piece.getType() != PieceType.PAWN) {
            sb.append(switch (piece.getType()) {
                case KING -> 'K';
                case QUEEN -> 'Q';
                case ROOK -> 'R';
                case BISHOP -> 'B';
                case KNIGHT -> 'N';
                default -> '?';
            });
        }

        Piece target = board.getPieceAt(to);
        boolean isCapture = target != null;
        if (piece.getType() == PieceType.PAWN && from.getFile() != to.getFile() && target == null) {
            isCapture = true;
        }

        if (piece.getType() == PieceType.PAWN && isCapture) {
            sb.append((char) ('a' + from.getFile()));
        }

        if (isCapture) {
            sb.append('x');
        }

        sb.append((char) ('a' + to.getFile()));
        sb.append((char) ('1' + to.getRank()));

        if (move.getPromotion() != null) {
            sb.append('=').append(switch (move.getPromotion()) {
                case QUEEN -> 'Q';
                case ROOK -> 'R';
                case BISHOP -> 'B';
                case KNIGHT -> 'N';
                default -> 'Q';
            });
        }

        return sb.toString();
    }
}
