package cli;

import game.Game;
import game.Game.GameState;
import enums.Color;

import java.util.Scanner;

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
                case "2" -> {
                    System.out.println("\n  Load Game — not implemented yet.");
                    pause();
                }
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

        // Draw offer indicator
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
        System.out.print("  Commands: [resign]");

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
