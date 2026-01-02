package timer;
import enums.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages chess game timers for both players.
 * Uses a scheduled executor to count down time for the current player.
 * Automatically ends the game when a player runs out of time.
 */
public class GameTimer {
    private int whiteTime;
    private int blackTime;
    private Color running = Color.WHITE;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates a new GameTimer with the specified time per player.
     * @param minutesPerPlayer The number of minutes each player starts with
     */
    public GameTimer(int minutesPerPlayer)
    {
        int seconds = minutesPerPlayer * 60;
        whiteTime = seconds;
        blackTime = seconds;
    }

    /**
     * Starts the timer countdown.
     * The timer decrements every second for the current player.
     * If a player's time reaches zero, the game ends automatically.
     */
    public void start()
    {
        executor.scheduleAtFixedRate(() ->
        {
            if(running == Color.WHITE)
            {
                whiteTime--;
                if(whiteTime <= 0)
                {
                    System.out.println("\n[!] WHITE ran out of time! BLACK wins!");
                    System.exit(0);
                }
            }
            else
            {
                blackTime--;
                if(blackTime<=0)
                {
                    System.out.println("\n[!] BLACK ran out of time! WHITE wins!");
                    System.exit(0);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Switches the active timer to the other player.
     * Called after each move to start counting down the opponent's time.
     */
    public void switchTurn()
    {
        running = (running == Color.WHITE) ? Color.BLACK : Color.WHITE;
        printTime();
    }

    /**
     * Prints the current time remaining for both players.
     * Format: [T] White: MM:SS   Black: MM:SS
     */
    public void printTime()
    {
        System.out.printf("[T] White: %s   Black: %s\n",format(whiteTime), format(blackTime));
    }

    /**
     * Formats seconds into MM:SS format.
     * @param seconds The number of seconds to format
     * @return Formatted time string (e.g., "05:30")
     */
    private String format(int seconds)
    {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
