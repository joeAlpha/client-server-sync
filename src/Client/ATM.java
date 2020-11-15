package Client;


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
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    public int operation;
    private String id;
    private int port;
    String event;
    Logger logger;

    public ATM(String id, int port) {
        this.id = id;
        this.port = port;
        event = "";
        logger = new Logger();
    }

    public String getId() {
        return this.id;
    }

    public void cancelConnection() throws IOException {
        this.dataInputStream.close();
        this.dataOutputStream.close();
        this.socket.close();

        event = getTime() + ": " + getId() + " -> disconnected!\n";
        System.out.println(event);
        logger.writeEvent(event);
    }

    public synchronized String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }

    // Thread entry point
    @Override
    public void run() {
        try {
            for (int i = 0; i < TRANSACTIONS; i++) {
                connectToServer();
                makeRequest();
                dataOutputStream.flush();
                cancelConnection();
                Thread.sleep(1000);
            }
        } catch (InterruptedException | IOException e) {
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
            default -> {
                event = getTime() + ": " + this.id + " -> Unexpected value: " + operation;
                logger.writeEvent(event);
                throw new IllegalStateException(event);
            }
        }
    }

    // Withdraws money
    private synchronized void withdraw(double withdraw) throws IOException {
        dataOutputStream.writeUTF(this.id);
        dataOutputStream.writeUTF("withdraw");
        dataOutputStream.writeDouble(withdraw);

        // Log event
        event = getTime() + ": " + id + " sent a withdraw request\n";
        System.out.println(event);
        logger.writeEvent(event);

        event = dataInputStream.readUTF();
        System.out.println(event);
        logger.writeEvent(event);
    }

    // Deposits money
    private synchronized void deposit(double deposit) throws IOException {
        // Sends the operation and the ammount to the server dispatcher
        dataOutputStream.writeUTF(this.id);
        dataOutputStream.writeUTF("deposit");
        dataOutputStream.writeDouble(deposit);

        event = getTime() + ": " + id + " sent a deposit request\n";
        System.out.println(event);
        logger.writeEvent(event);

        event = dataInputStream.readUTF();
        System.out.println(event);
        logger.writeEvent(event);
    }

    // Sets the sockets
    private synchronized void connectToServer() throws IOException {
        socket = new Socket("127.0.0.1", port);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        // Timer starts
        Timer timer = new Timer(this, 20);
        Thread timerThread = new Thread(timer);
        timerThread.start();

        // Waits for server response
        if (dataInputStream.readBoolean()) {
            timer.changeRunningStatus(false); // Cancels the time out
            timerThread.interrupt();
            event = getTime() + ": " + id + " -> connected on port: " + socket.getRemoteSocketAddress(); // Connected
            System.out.println(event);
            logger.writeEvent(event);
        } else {
            event = getTime() + ": " + this.id + " -> Error setting up the socket with the server";
            System.out.println(event);
            logger.writeEvent(event);
        }
    }

}
