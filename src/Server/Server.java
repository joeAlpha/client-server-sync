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
    private ServerSocket[] serverSockets;
    private ExecutorService executorService;
    private ThreadPoolExecutor pool;
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

        // Only one thread will be letting the access to the shared
        // account
        executorService = Executors.newFixedThreadPool(1);
        pool = (ThreadPoolExecutor) executorService;

        serverSockets = new ServerSocket[3];
        try {
            serverSockets[0] = new ServerSocket(1026);
            serverSockets[1] = new ServerSocket(1027);
            serverSockets[2] = new ServerSocket(1028);
            acceptClients();
        } catch (IOException | InterruptedException e) {
            System.err.println(getTime() + ": Couldn't listen on port: ");
            System.exit(1);
        }
    }

    private void acceptClients() throws InterruptedException {
        // Accepting at least 3 client's connections
        // in these threads whith isolated sockets
        for (int i = 0; i < 3; i++) {
            new Thread(
                    new Receiver(
                            "Receiver " + (i + 1),
                            serverSockets[i],
                            sharedAccount,
                            pool)
            ).start();
        }

        while(true) {
            System.out.println(
                    "\n-------------- REQUESTS STATUS --------------" +
                            "\n> " + getTime() +
                            "\n> Clients using the account: " + pool.getActiveCount()+
                            "\n> Requests completed: " + pool.getCompletedTaskCount()+
                            "\n> Requests waiting: " + pool.getTaskCount()
            );
            Thread.sleep(3000);
        }
    }
}
