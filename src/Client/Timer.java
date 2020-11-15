package Client;

import javax.swing.plaf.TableHeaderUI;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timer implements Runnable {
    private int timeout;
    private ATM client;
    private Logger logger;
    private String event;
    private boolean running;

    public Timer(ATM client, int seconds) {
        this.client = client;
        this.timeout = seconds;
        logger = new Logger();
        running = true;
    }

    public void changeRunningStatus(boolean status) {
        this.running = status;
    }

    public synchronized String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }

    @Override
    public void run() {
        for (int i = 0; i <= timeout && running; i++) {
            if(i == timeout) {
                event = getTime() + ": " + client.getId() + " -> connection cancelled by time out!\n";
                System.out.println(event);
                logger.writeEvent(event);

                // Finish threads and connections rightly!
                try {
                    client.cancelConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }

        System.out.println(client.getId() + " timer finish!");
    }
}
