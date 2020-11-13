package Client;

import Server.BankAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

// TODO: register all request and connections to console
public class ATM implements Runnable {
    public final int TRANSACTIONS = 10;
    public double deposit, withdraw;
    private Socket dataSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    public int operation;
    private String id;

    public ATM(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }

    // Thread entry point
    @Override
    public void run() {
        try {
            while (true) {
                if (connectToServer()) makeRequest();
                else System.out.println(this.id + " : Error setting up the socket with the server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Calls the withdraw and deposit methods
    private synchronized void makeRequest() throws IOException {
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

    // Withdraws money
    private synchronized void withdraw(double withdraw) throws IOException {
        // Sends the operation and the ammount to the server dispatcher
        dataOutputStream.writeUTF(this.id);
        dataOutputStream.writeUTF("withdraw");
        dataOutputStream.writeDouble(withdraw);
        System.out.println(getTime() + ": " + id + " sent a withdraw request (enque)"); // Connected

        // Response
        System.out.println(dataInputStream.readUTF());
    }

    // Deposits money
    private synchronized void deposit(double deposit) throws IOException {
        // Sends the operation and the ammount to the server dispatcher
        dataOutputStream.writeUTF(this.id);
        dataOutputStream.writeUTF("deposit");
        dataOutputStream.writeDouble(deposit);
        System.out.println(getTime() + ": " + id + " sent a deposit request (enque)"); // Connected
        System.out.println(dataInputStream.readUTF()); // Dispatched
    }

    // Sets the sockets
    private synchronized boolean connectToServer() throws IOException {
        dataSocket = new Socket("127.0.0.1", ThreadLocalRandom.current().nextInt(1026, 1029));
        dataInputStream = new DataInputStream(dataSocket.getInputStream());
        dataOutputStream = new DataOutputStream(dataSocket.getOutputStream());
        System.out.println(getTime() + ": " + id + " -> connected on port: " + dataSocket.getRemoteSocketAddress()); // Connected
        return true;
    }

}
