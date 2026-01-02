import cli.ChessCLI;

/**
 * Main entry point for the Console Chess application.
 * Initializes and starts the chess game CLI interface.
 */
public class ChessGame {
    /**
     * Application entry point. Creates a new ChessCLI instance and starts the game.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        ChessCLI cli = new ChessCLI();
        cli.start();
    }
}
