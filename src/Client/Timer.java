package Client;

public class Timer implements Runnable {
    private int timeout;
    private ATM client;

    public Timer(ATM client, int seconds) {
        this.client = client;
        this.timeout = seconds;
    }

    @Override
    public void run() {
        for (int i = 0; i < timeout; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Timeout reached
        this.client.
    }
}
