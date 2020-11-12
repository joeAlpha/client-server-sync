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
    private Socket dataSocket;
    private final int DATA_PORT = 1026;
    private DataInputStream disData;
    private DataOutputStream dosData;
    public int operation;
    private String id;

    public ATM(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void cancelRequest() {
        Thread.currentThread().interrupt();
    }


    // Thread entry point
    @Override
    public void run() {
        try {
            if (connectToServer()) {
                Thread timer = new Thread(this);
                timer.start();
                makeRequest();
                timer.interrupt();
            } else System.out.println(this.id + " : Error setting up the socket with the server");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Calls the withdraw and deposit methods
    private synchronized void makeRequest() throws IOException {
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
        // Sends the operation and the ammount to the server dispatcher
        dosData.writeUTF("withdraw");
        dosData.writeDouble(withdraw);

        // Response
        System.out.println(disData.readUTF());
    }

    // Deposits money
    private synchronized void deposit(double deposit) throws IOException {
        // Sends the operation and the ammount to the server dispatcher
        dosData.writeUTF("deposit");
        dosData.writeDouble(deposit);
        System.out.println(disData.readUTF());
    }

    // Sets the sockets
    private synchronized boolean connectToServer() throws IOException {
        dataSocket = new Socket("127.0.0.1", DATA_PORT);
        disData = new DataInputStream(dataSocket.getInputStream());
        dosData = new DataOutputStream(dataSocket.getOutputStream());
        System.out.println(this.id + ": Data socket connected!");
        return true; // Server free and isn't handled
    }

}
