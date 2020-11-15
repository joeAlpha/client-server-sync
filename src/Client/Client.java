package Client;

import java.util.concurrent.ThreadLocalRandom;

public class Client {
    public static void main(String args[]) {

        // Each ATM represents a client accessing to the shared
        // account
        Thread[] threads = new Thread[4];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(
                    new ATM(
                            "ATM-" + (i + 1),
                            ThreadLocalRandom.current().nextInt(1026, 1029)
                    )
            );
            threads[i].start();
        }

    }
}