package cli;

import board.Board;
import game.Game;
import game.Game.GameState;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import pgn.PGNExporter;
import pgn.PGNParser;

public class ChessCLI {

    private final Scanner scanner = new Scanner(System.in);
    private Game game;

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

            String choice = scanner.nextLine();

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
                game.setCurrentPlayer(moves.size() % 2 == 0 ? enums.Color.WHITE : enums.Color.BLACK);

                System.out.println("\n  Loaded " + filename + " successfully!");
                System.out.println("  " + moves.size() + " moves applied.");
                pause();

                clearScreen();
                printSeparator(60);
                BoardPrinter.print(board);
                printSeparator(60);
                System.out.println("\n  Position loaded from: " + filename);
                System.out.println("\n  Press Enter to continue to game, or type 'back' to return to menu.");
                System.out.print("  > ");
                String input = scanner.nextLine().trim().toLowerCase();

                if (!input.equals("back")) {
                    gameLoop();
                }
            } else {
                System.out.println("\n  Failed to load PGN file. Check the file format.");
                pause();
            }
        } catch (Exception e) {
            System.out.println("\n  Error loading file: " + e.getMessage());
            pause();
        }
    }

    private void startNewGame() {
        game = new Game();
        gameLoop();
    }

    private void gameLoop() {
        while (true) {
            clearScreen();
            printSeparator(60);
            BoardPrinter.print(game.getBoard());
            printSeparator(60);

            printGameStatus();

            if (isGameOver()) {
                pause();
                return;
            }

            printTurnPrompt();
            printInGameMenu();

            System.out.print("\n  Enter move: ");
            String input = scanner.nextLine();

            handleCommand(input);
        }
    }

    private void printTurnPrompt() {
        System.out.println();
        printCentered(game.getCurrentPlayer() + " TO MOVE", 60);
    }

    private void printGameStatus() {
        System.out.println();

        switch (game.getState()) {
            case CHECK ->
                    printHighlight("CHECK!", 60);
            case CHECKMATE ->
                    printHighlight("CHECKMATE — " + game.getWinner() + " WINS!", 60);
            case STALEMATE ->
                    printHighlight("STALEMATE — DRAW", 60);
            case DRAW ->
                    printHighlight("DRAW AGREED", 60);
            case RESIGNED ->
                    printHighlight("GAME OVER — " + game.getWinner() + " WINS!", 60);
        }

        if (game.isDrawOffered()
                && game.getDrawOfferedBy() != game.getCurrentPlayer()
                && game.getState() == GameState.ONGOING) {

            System.out.println();
            printHighlight(
                    "DRAW OFFERED BY " + game.getDrawOfferedBy(),
                    60
            );
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

        if (!game.isDrawOffered()) {
            System.out.print(" [draw]");
        }
        else if (game.getDrawOfferedBy() != game.getCurrentPlayer()) {
            System.out.print(" [accept] [decline]");
        }

        System.out.println();
        printSeparator(60);
    }


    private void handleCommand(String input) {
        input = input.trim().toLowerCase();

        if (input.equals("save")) {
            saveGame();
            return;
        }

        if (input.equals("resign")) {
            game.resign();
            return;
        }

        if (input.equals("draw")) {
            game.offerDraw();
            System.out.println("\n  Draw offered.");
            scanner.nextLine();
            return;
        }

        if (input.equals("accept")) {
            game.acceptDraw();
            return;
        }

        if (input.equals("decline")) {
            game.declineDraw();
            return;
        }

        System.out.println("Invalid command.");
        return;
    }



    private void saveGame() {
        System.out.print("\n  Enter filename to save (e.g., mygame.pgn): ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("  No filename entered.");
            pause();
            return;
        }

        if (!filename.endsWith(".pgn")) {
            filename += ".pgn";
        }

        try {
            Map<String, String> tags = new LinkedHashMap<>();
            tags.put("Event", "Console Chess Game");
            tags.put("Site", "Local");
            tags.put("Date", LocalDate.now().toString().replace("-", "."));
            tags.put("Round", "1");
            tags.put("White", "Player1");
            tags.put("Black", "Player2");

            String result = switch (game.getState()) {
                case CHECKMATE -> game.getCurrentPlayer() == enums.Color.WHITE ? "0-1" : "1-0";
                case DRAW, STALEMATE -> "1/2-1/2";
                case RESIGNED -> game.getCurrentPlayer() == enums.Color.WHITE ? "0-1" : "1-0";
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

    private void pause() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

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

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
