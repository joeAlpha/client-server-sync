package Client;

public class Client {
    public static void main(String args[]) {

        // Each ATM represents a client accessing to the shared
        // account
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new ATM("ATM-" + (i+1), (1025+(i+1))));
            threads[i].start();
        }

    }
}