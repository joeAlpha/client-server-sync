package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

// Driver class
public class Server {
    private final int PORT = 1026;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private BankAccount sharedAccount;

    public static void main(String args[]) {
        Server server = new Server();
        server.startServer();
    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }

    private void startServer() {
        sharedAccount = new BankAccount();
        serverSocket = null;

        // Only one thread will be letting the access to the shared
        // account
        executorService = Executors.newSingleThreadExecutor();

        try {
            serverSocket = new ServerSocket(PORT);
            acceptClients(serverSocket);
        } catch (IOException e) {
            System.err.println(getTime() + ": Couldn't listen on port: " + PORT);
            System.exit(1);
        }
    }

    private void acceptClients(ServerSocket serverSocket) {
        System.out.println(getTime() + ": Server starts port = " + serverSocket.getLocalSocketAddress());

        // Accepting at least 3 client's connections
        // in these threads
        for(int i = 0; i < 3; i++) {
            new Thread(new Receiver("Receiver " + (i+1), serverSocket, sharedAccount, executorService)).start();
        }

    }
}
