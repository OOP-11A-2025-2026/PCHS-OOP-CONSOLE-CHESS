package timer;
import enums.Color;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameTimer {
    private int whiteTime;
    private int blackTime;
    private Color running = Color.WHITE;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public GameTimer(int minutesPerPlayer)
    {
        int seconds = minutesPerPlayer * 60;
        whiteTime = seconds;
        blackTime = seconds;
    }
    public void start()
    {
        executor.scheduleAtFixedRate(() ->
        {
            if(running == Color.WHITE)
            {
                whiteTime--;
                if(whiteTime <= 0)
                {
                    System.out.println("\n⏱ WHITE ran out of time!BLACK wins!");
                    System.exit(0);
                }
            }
            else
            {
                blackTime--;
                if(blackTime<=0)
                {
                    System.out.println("\n⏱ BLACK ran out of time! WHITE wins!");
                    System.exit(0);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void switchTurn()
    {
        running = (running == Color.WHITE) ? Color.BLACK : Color.WHITE;
        printTime();
    }
    public void printTime()
    {
        System.out.printf("⏱ White: %s   Black: %s\n",format(whiteTime), format(blackTime));
    }
    private String format(int seconds)
    {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
