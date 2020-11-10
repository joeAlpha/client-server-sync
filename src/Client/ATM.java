package Client;

import Server.BankAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class ATM implements Runnable {
    public final int TRANSACTIONS = 10;
    public double deposit, withdraw;
    public BankAccount account = new BankAccount();
    private Socket notificationSocket, dataSocket;
    private final int NOTIFICATION_PORT = 1025;
    private final int DATA_PORT = 1026;
    private DataInputStream disNotification, disData;
    private DataOutputStream dosNotification, dosData;
    private final int timeOut = 10;
    public int operation;
    private String id;

    public ATM(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    // Thread entry point
    @Override
    public void run() {
        try {
            if(connectToServer() == 1) {
                makeRequest();
            } else {
                // Timeout for avoid infinite wait
                System.out.println(
                        this.id +
                        " : Server isn't free or is handling by a client" +
                                "\nWaiting for connection ..."
                );

                // Asks each second if the server
                for(int i = 0; i < this.timeOut; i++) {
                    if(connectToServer() == 1) {
                        System.out.println(this.id + ": Server is free!");
                        makeRequest();
                        break;
                    } else System.out.println(this.id + ": Server still handled!");
                    Thread.sleep(1000);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    // Calls the withdraw and deposit methods
    private void makeRequest() {
        for (int i = 0; i < TRANSACTIONS; i++) {
            operation = ThreadLocalRandom.current().nextInt(0, 2);
            switch (operation) {
                case 0 -> {
                    withdraw = ThreadLocalRandom.current().nextDouble(5, 21);
                    withdraw(withdraw);
                }
                case 1 -> {
                    deposit = ThreadLocalRandom.current().nextDouble(5, 21);
                    deposit(deposit);
                }
            }
        }
    }

    // Withdraws money
    private synchronized void withdraw(double withdraw) {
        if (account.getBalance() >= withdraw) {
            System.out.printf("> Balance: $%.2f\n", account.getBalance());
            account.withdraw(withdraw);
            System.out.printf("> User: %s witdraw $%.2f\n> New balance: $%.2f\n\n", Thread.currentThread().getName(),
                    withdraw, account.getBalance());
        } else {
            System.out.printf("The balance isn't enough to withdraw\n");
            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyAll();
    }

    // Deposits money
    private synchronized void deposit(double deposit) {
        System.out.printf("> Balance: $%.2f\n", account.getBalance());
        account.deposit(deposit);
        System.out.printf("> User: %s deposit $%.2f\n> New balance: $%.2f\n\n", Thread.currentThread().getName(),
                deposit, account.getBalance());
        notifyAll();
    }

    // Sets the sockets
    private int connectToServer() throws IOException {
            // Notification socket: for check if the server is free
            notificationSocket = new Socket("127.0.0.1", NOTIFICATION_PORT);
            disNotification = new DataInputStream(notificationSocket.getInputStream());
            dosNotification = new DataOutputStream(notificationSocket.getOutputStream());

            // Checks if there are less than 3 clients
            // connected.
            if(disNotification.readBoolean()) {
                // Data socket: arithmetic operations
                // Then checks if there no clients handling the account
                if(disNotification.readBoolean()) {
                    dataSocket = new Socket("127.0.0.1", DATA_PORT);
                    disData = new DataInputStream(dataSocket.getInputStream());
                    dosData = new DataOutputStream(dataSocket.getOutputStream());
                    System.out.println(this.id + ": Data socket connected!");
                    return 1; // Server free and isn't handled
                } else {
                    System.out.println(this.id + ": A client is handling the account");
                    return -2; // Server is handled
                }
            } else return -3; // Server isn't free
    }

}
