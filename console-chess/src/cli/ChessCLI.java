package cli;

import board.Board;
import board.Move;
import enums.Color;
import game.Game;
import game.Game.GameState;
import input.MoveParser;
import pgn.PGNExporter;
import pgn.PGNParser;
import timer.GameTimer;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ChessCLI {

    private final Scanner scanner = new Scanner(System.in);
    private Game game;
    private GameTimer timer;

    public void start() {
        showMainMenu();
    }

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

    private void startNewGame() {
        game = new Game();
        timer = new GameTimer(10);
        timer.start();
        gameLoop();
    }

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

    private void printTurnPrompt() {
        System.out.println();
        String player = game.getCurrentPlayer() == Color.WHITE ? "WHITE" : "BLACK";
        printCentered(player + " TO MOVE", 60);
    }

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

    private boolean isGameOver() {
        GameState state = game.getState();
        return state == GameState.CHECKMATE
                || state == GameState.STALEMATE
                || state == GameState.DRAW
                || state == GameState.RESIGNED;
    }

    private void printInGameMenu() {
        System.out.println();
        printSeparator(60);
        System.out.print("  Commands: [save] [resign]");
        if (!game.isDrawOffered()) System.out.print(" [draw]");
        System.out.println();
        printSeparator(60);
    }

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
    private void printBox(String text, int width) {
        printSeparator(width);
        printCentered(text, width);
        printSeparator(width);
    }

    private void printSeparator(int width) {
        System.out.println("  " + "─".repeat(width));
    }

    private void printCentered(String text, int width) {
        int padding = (width - text.length()) / 2;
        System.out.println("  " + " ".repeat(Math.max(0, padding)) + text);
    }

    private void printHighlight(String text, int width) {
        int padding = (width - text.length() - 4) / 2;
        System.out.println("  " + " ".repeat(Math.max(0, padding)) + "[ " + text + " ]");
    }

    private void printMenuOption(String number, String label) {
        System.out.printf("    %s. %s%n", number, label);
    }

    private void pause() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
