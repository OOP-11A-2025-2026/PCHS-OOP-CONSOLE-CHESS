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

/**
 * Main game controller class that manages the chess game state.
 * Handles move execution, turn management, draw offers, resignations,
 * and game state detection (check, checkmate, stalemate).
 */
public class Game {
    private Board board;
    private Color currentPlayer;
    private GameState state;
    private boolean drawOffered;
    private Color drawOfferedBy;
    private List<String> moveHistory = new ArrayList<>();

    /**
     * Enum representing all possible game states.
     */
    public enum GameState {
        ONGOING,
        CHECK,
        CHECKMATE,
        STALEMATE,
        DRAW,
        RESIGNED
    }

    /**
     * Creates a new chess game with standard starting position.
     * White moves first.
     */
    public Game() {
        this.board = new Board();
        this.currentPlayer = Color.WHITE;
        this.state = GameState.ONGOING;
        this.drawOffered = false;
        this.drawOfferedBy = null;
        initializeBoard();
    }

    /**
     * Sets up the initial chess position with all pieces in their starting squares.
     */
    private void initializeBoard() {
        // Place pawns
        for (int file = 0; file < 8; file++){
            board.setPieceAt(new Square(file, 1), new Pawn(Color.WHITE, file, 1));
            board.setPieceAt(new Square(file, 6), new Pawn(Color.BLACK, file, 6));
        }

        // Place white pieces
        board.setPieceAt(new Square(0, 0), new Rook(Color.WHITE, 0, 0));
        board.setPieceAt(new Square(7, 0), new Rook(Color.WHITE, 7, 0));
        board.setPieceAt(new Square(1, 0), new Knight(Color.WHITE, 1, 0));
        board.setPieceAt(new Square(6, 0), new Knight(Color.WHITE, 6, 0));
        board.setPieceAt(new Square(2, 0), new Bishop(Color.WHITE, 2, 0));
        board.setPieceAt(new Square(5, 0), new Bishop(Color.WHITE, 5, 0));
        board.setPieceAt(new Square(3, 0), new Queen(Color.WHITE, 3, 0));
        board.setPieceAt(new Square(4, 0), new King(Color.WHITE, 4, 0));

        // Place black pieces
        board.setPieceAt(new Square(0, 7), new Rook(Color.BLACK, 0, 7));
        board.setPieceAt(new Square(7, 7), new Rook(Color.BLACK, 7, 7));
        board.setPieceAt(new Square(1, 7), new Knight(Color.BLACK, 1, 7));
        board.setPieceAt(new Square(6, 7), new Knight(Color.BLACK, 6, 7));
        board.setPieceAt(new Square(2, 7), new Bishop(Color.BLACK, 2, 7));
        board.setPieceAt(new Square(5, 7), new Bishop(Color.BLACK, 5, 7));
        board.setPieceAt(new Square(3, 7), new Queen(Color.BLACK, 3, 7));
        board.setPieceAt(new Square(4, 7), new King(Color.BLACK, 4, 7));
    }

    /**
     * Gets the game board.
     * @return The current Board object
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Gets the current player's color.
     * @return Color of the player whose turn it is
     */
    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current player (used when loading games).
     * @param color The color to set as current player
     */
    public void setCurrentPlayer(Color color) {
        this.currentPlayer = color;
    }

    /**
     * Gets the current game state.
     * @return The current GameState
     */
    public GameState getState() {
        return state;
    }

    /**
     * Checks if a draw has been offered.
     * @return true if a draw offer is pending
     */
    public boolean isDrawOffered() {
        return drawOffered;
    }

    /**
     * Gets the color of the player who offered a draw.
     * @return Color of the player who offered draw, or null
     */
    public Color getDrawOfferedBy() {
        return drawOfferedBy;
    }

    /**
     * Offers a draw to the opponent.
     */
    public void offerDraw() {
        drawOffered = true;
        drawOfferedBy = currentPlayer;
    }

    /**
     * Accepts a pending draw offer, ending the game in a draw.
     */
    public void acceptDraw() {
        if (drawOffered && drawOfferedBy != currentPlayer) {
            state = GameState.DRAW;
        }
    }

    /**
     * Declines a pending draw offer.
     */
    public void declineDraw() {
        drawOffered = false;
        drawOfferedBy = null;
    }

    /**
     * Resigns the game for the current player.
     */
    public void resign() {
        state = GameState.RESIGNED;
    }

    /**
     * Attempts to make a move on the board.
     * Validates the move, checks for self-check, applies the move,
     * records it in SAN notation, and updates game state.
     * 
     * @param move The move to attempt
     * @return true if the move was successful, false otherwise
     */
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


    /**
     * Updates the game state after a move.
     * Checks for check, checkmate, and stalemate conditions.
     */
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

    /**
     * Checks if a player has any legal moves available.
     * Used to determine checkmate and stalemate.
     * 
     * @param color The color to check for legal moves
     * @return true if the player has at least one legal move
     */
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

    /**
     * Finds the king of the specified color on the board.
     * 
     * @param board The board to search
     * @param color The color of the king to find
     * @return The Square containing the king, or null if not found
     */
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

    /**
     * Gets the opponent's color.
     * @param color The current color
     * @return BLACK if color is WHITE, WHITE if color is BLACK
     */
    private Color getOpponentColor(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    /**
     * Gets the winner of the game (if any).
     * @return "White" or "Black" if there's a winner, null otherwise
     */
    public String getWinner() {
        if (state == GameState.CHECKMATE || state == GameState.RESIGNED) {
            return currentPlayer == Color.WHITE ? "Black" : "White";
        }
        return null;
    }

    /**
     * Gets a copy of the move history.
     * @return List of moves in SAN notation
     */
    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Sets the move history (used when loading games).
     * @param history List of moves in SAN notation
     */
    public void setMoveHistory(List<String> history) {
        this.moveHistory = new ArrayList<>(history);
    }

    /**
     * Converts a move to Standard Algebraic Notation (SAN).
     * Handles piece notation, captures, castling, and promotions.
     * 
     * @param move The move to convert
     * @param piece The piece making the move
     * @return The move in SAN format (e.g., "Nf3", "O-O", "exd5")
     */
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
