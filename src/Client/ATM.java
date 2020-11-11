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

                System.out.printf("%s: timeout reached, thread finished.", this.id);
                Thread.currentThread().interrupt();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    // Calls the withdraw and deposit methods
    private void makeRequest() throws IOException {
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
                default -> throw new IllegalStateException(this.id + ": Unexpected value: " + operation);
            }
        }
    }

    // Withdraws money
    private synchronized void withdraw(double withdraw) throws IOException {
        // Read balance
        double currentBalance = disData.readDouble();
        
        if (currentBalance >= withdraw) {
            System.out.printf("%s: Balance: $%.2f\n", this.id, currentBalance);

            // Sends the operation and the ammount to the server dispatcher
            dosData.writeUTF("withdraw");
            dosData.writeDouble(withdraw);

            // Response
            System.out.println(disData.readUTF());
        } else {
            System.out.printf("%s: The balance isn't enough to withdraw\n", this.id);
        }
    }

    // Deposits money
    private synchronized void deposit(double deposit) throws IOException {
        // Read balance
        double currentBalance = disData.readDouble();

        // Sends the operation and the ammount to the server dispatcher
        dosData.writeUTF("deposit");
        dosData.writeDouble(deposit);
        System.out.println(disData.readUTF());
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
