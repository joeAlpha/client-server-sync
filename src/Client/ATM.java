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

    public int operation;

    @Override
    public void run() {
        try {
            if(connectToServer()) {
                for (int i = 0; i < TRANSACTIONS; i++) {
                    operation = ThreadLocalRandom.current().nextInt(0, 2);
                    switch (operation) {
                        case 0:
                            withdraw = ThreadLocalRandom.current().nextDouble(5, 21);
                            withdraw(withdraw);
                            break;
                        case 1:
                            deposit = ThreadLocalRandom.current().nextDouble(5, 21);
                            deposit(deposit);
                            break;
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Log to file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

    private synchronized void deposit(double deposit) {
        System.out.printf("> Balance: $%.2f\n", account.getBalance());
        account.deposit(deposit);
        System.out.printf("> User: %s deposit $%.2f\n> New balance: $%.2f\n\n", Thread.currentThread().getName(),
                deposit, account.getBalance());
        notifyAll();
    }

    private boolean connectToServer() throws IOException {
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
                    System.out.println("Data socket connected!");
                    return true;
                } else {
                    System.out.println("A client is handling the account");
                }
            } else return false;
    }

}
