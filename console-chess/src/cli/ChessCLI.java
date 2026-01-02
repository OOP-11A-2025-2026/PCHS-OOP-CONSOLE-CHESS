package cli;

import board.Board;
import board.Move;
import enums.Color;
import game.Game;
import game.Game.GameState;
import input.MoveParser;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import pgn.PGNExporter;
import pgn.PGNParser;
import timer.GameTimer;

/**
 * Command Line Interface for the chess game.
 * Handles user interaction, menu navigation, game display, and input processing.
 */
public class ChessCLI {

    private final Scanner scanner = new Scanner(System.in);
    private Game game;
    private GameTimer timer;

    /**
     * Starts the chess CLI application.
     * Shows the main menu and handles navigation.
     */
    public void start() {
        showMainMenu();
    }

    /**
     * Displays and handles the main menu.
     * Options: New Game, Load Game, Exit
     */
    private void showMainMenu() {
        while (true) {
            clearScreen();
            printBox("CONSOLE CHESS", 50);
            System.out.println();
            printMenuOption("1", "New Game");
            printMenuOption("2", "Load Game");
            printMenuOption("3", "Exit");
            System.out.println();
            printSeparator(50);
            System.out.print("  > ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> startNewGame();
                case "2" -> loadGame();
                case "3" -> {
                    System.out.println("\n  Thanks for playing!");
                    return;
                }
                default -> {
                    System.out.println("\n  Invalid choice.");
                    pause();
                }
            }
        }
    }

    /**
     * Starts a new chess game with fresh board and timer.
     */
    private void startNewGame() {
        game = new Game();
        timer = new GameTimer(10);
        timer.start();
        gameLoop();
    }

    /**
     * Loads a game from a PGN file.
     * Prompts user for filename and parses the PGN content.
     */
    private void loadGame() {
        clearScreen();
        printBox("LOAD GAME", 50);
        System.out.println();
        System.out.print("  Enter PGN filename: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("\n  No filename entered.");
            pause();
            return;
        }

        try {
            String pgnText = PGNParser.readFile(Path.of(filename));
            PGNParser parser = new PGNParser();

            game = new Game();
            Board board = game.getBoard();

            java.util.List<String> moves = parser.parseMoves(pgnText);
            boolean success = parser.loadToBoard(board, pgnText);

            if (success) {
                game.setMoveHistory(moves);
                game.setCurrentPlayer(moves.size() % 2 == 0 ? Color.WHITE : Color.BLACK);

                timer = new GameTimer(10);
                timer.start();

                System.out.println("\n  Loaded " + filename + " successfully!");
                System.out.println("  " + moves.size() + " moves applied.");
                pause();

                gameLoop();
            } else {
                System.out.println("\n  Failed to load PGN file. Check the file format.");
                pause();
            }
        } catch (Exception e) {
            System.out.println("\n  Error loading file: " + e.getMessage());
            pause();
        }
    }

    /**
     * Main game loop that handles turns, input, and display.
     * Continues until the game ends or player exits.
     */
    private void gameLoop() {
        while (true) {
            clearScreen();
            printSeparator(60);
            BoardPrinter.print(game.getBoard());
            printSeparator(60);

            printGameStatus();
            timer.printTime();

            if (isGameOver()) {
                System.out.println("\n  Press Enter to return to main menu...");
                scanner.nextLine();
                return;
            }

            if (game.isDrawOffered() && game.getDrawOfferedBy() != game.getCurrentPlayer()) {
                handlePendingDrawOffer();
                if (isGameOver()) {
                    System.out.println("\n  Press Enter to return to main menu...");
                    scanner.nextLine();
                    return;
                }
            }

            printTurnPrompt();
            printInGameMenu();

            System.out.print("\n  Enter move: ");
            String input = scanner.nextLine().trim();

            handleCommand(input);
        }
    }

    /**
     * Processes user input commands and moves.
     * Handles: save, resign, draw offers, and move input.
     * 
     * @param input The user's input string
     */
    private void handleCommand(String input) {
        if (input.isEmpty()) return;

        input = input.trim();

        if (input.equalsIgnoreCase("save")) {
            saveGame();
            return;
        }

        if (input.equalsIgnoreCase("resign")) {
            game.resign();
            return;
        }

        if (input.equalsIgnoreCase("draw")) {
            if (game.isDrawOffered()) {
                System.out.println("\n  Draw already offered.");
            } else {
                game.offerDraw();
                System.out.println("\n  Draw offered.");
            }
            pause();
            return;
        }

        Move move = MoveParser.parse(input, game.getBoard(), game.getCurrentPlayer());
        if (move == null || !game.makeMove(move)) {
            System.out.println("\n  Illegal move!");
            pause();
        } else {
            timer.switchTurn();
        }
    }

    /**
     * Handles a pending draw offer from the opponent.
     * Prompts the current player to accept or decline.
     */
    private void handlePendingDrawOffer() {
        System.out.print("\n  Opponent offers a draw. Accept? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
            game.acceptDraw();
            System.out.println("  Draw agreed.");
            printGameStatus();
        } else {
            game.declineDraw();
            System.out.println("  Draw declined.");
        }

        pause();
    }

    /**
     * Prints which player's turn it is.
     */
    private void printTurnPrompt() {
        System.out.println();
        String player = game.getCurrentPlayer() == Color.WHITE ? "WHITE" : "BLACK";
        printCentered(player + " TO MOVE", 60);
    }

    /**
     * Prints the current game status (check, checkmate, etc.).
     */
    private void printGameStatus() {
        System.out.println();
        switch (game.getState()) {
            case CHECK -> printHighlight("CHECK!", 60);
            case CHECKMATE -> printHighlight("CHECKMATE — " + game.getWinner() + " WINS!", 60);
            case STALEMATE -> printHighlight("STALEMATE — DRAW", 60);
            case DRAW -> printHighlight("DRAW AGREED", 60);
            case RESIGNED -> printHighlight("GAME OVER — " + game.getWinner() + " WINS!", 60);
        }
    }

    /**
     * Checks if the game has ended.
     * @return true if the game is over (checkmate, stalemate, draw, or resigned)
     */
    private boolean isGameOver() {
        GameState state = game.getState();
        return state == GameState.CHECKMATE
                || state == GameState.STALEMATE
                || state == GameState.DRAW
                || state == GameState.RESIGNED;
    }

    /**
     * Prints the in-game command menu.
     */
    private void printInGameMenu() {
        System.out.println();
        printSeparator(60);
        System.out.print("  Commands: [save] [resign]");
        if (!game.isDrawOffered()) System.out.print(" [draw]");
        System.out.println();
        printSeparator(60);
    }

    /**
     * Saves the current game to a PGN file.
     * Prompts user for filename and exports the game.
     */
    /* ================== SAVE GAME ================== */
    private void saveGame() {
        System.out.print("\n  Enter filename to save (e.g., mygame.pgn): ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("  No filename entered.");
            pause();
            return;
        }

        if (!filename.endsWith(".pgn")) filename += ".pgn";

        try {
            Map<String, String> tags = new LinkedHashMap<>();
            tags.put("Event", "Console Chess Game");
            tags.put("Site", "Local");
            tags.put("Date", LocalDate.now().toString().replace("-", "."));
            tags.put("Round", "1");
            tags.put("White", "Player1");
            tags.put("Black", "Player2");

            String result = switch (game.getState()) {
                case CHECKMATE -> game.getCurrentPlayer() == Color.WHITE ? "0-1" : "1-0";
                case DRAW, STALEMATE -> "1/2-1/2";
                case RESIGNED -> game.getCurrentPlayer() == Color.WHITE ? "0-1" : "1-0";
                default -> "*";
            };
            tags.put("Result", result);

            PGNExporter.saveToFile(Path.of(filename), tags, game.getMoveHistory(), result);
            System.out.println("\n  Game saved to: " + filename);
        } catch (Exception e) {
            System.out.println("\n  Error saving game: " + e.getMessage());
        }
        pause();
    }

    /* ================== UI HELPERS ================== */
    
    /**
     * Prints a text box with borders.
     * @param text The text to display inside the box
     * @param width The width of the box
     */
    private void printBox(String text, int width) {
        printSeparator(width);
        printCentered(text, width);
        printSeparator(width);
    }

    /**
     * Prints a horizontal separator line.
     * @param width The width of the separator
     */
    private void printSeparator(int width) {
        System.out.println("  " + "-".repeat(width));
    }

    /**
     * Prints text centered within a specified width.
     * @param text The text to center
     * @param width The total width for centering
     */
    private void printCentered(String text, int width) {
        int padding = (width - text.length()) / 2;
        System.out.println("  " + " ".repeat(Math.max(0, padding)) + text);
    }

    /**
     * Prints highlighted text with brackets.
     * @param text The text to highlight
     * @param width The total width for centering
     */
    private void printHighlight(String text, int width) {
        int padding = (width - text.length() - 4) / 2;
        System.out.println("  " + " ".repeat(Math.max(0, padding)) + "[ " + text + " ]");
    }

    /**
     * Prints a numbered menu option.
     * @param number The option number
     * @param label The option description
     */
    private void printMenuOption(String number, String label) {
        System.out.printf("    %s. %s%n", number, label);
    }

    /**
     * Pauses execution and waits for user to press Enter.
     */
    private void pause() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Clears the console screen using ANSI escape codes.
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
