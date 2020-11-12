package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

// Driver class
public class Server {
    private final int PORT = 1026;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private BankAccount sharedAccount;

    public static void main(String args[]) {
        Server server = new Server();
        server.startServer();
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
            System.err.println("Couldn't listen on port: " + PORT);
            System.exit(1);
        }
    }

    private void acceptClients(ServerSocket serverSocket) {
        System.out.println("Server starts port = " + serverSocket.getLocalSocketAddress());

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("ATM accesing from: " + socket.getRemoteSocketAddress());

                // Go to the queue's executor
                Future<String> taskResult = executorService.submit(new ClientRequest(
                        sharedAccount,
                        dataInputStream.readUTF(),
                        dataInputStream.readUTF(),
                        dataInputStream.readDouble()
                ));

                String result = "-";

                try {
                    result = taskResult.get(15, TimeUnit.SECONDS);
                } catch (TimeoutException | InterruptedException | ExecutionException e) {
                    System.out.println("Time out reached!");
                } finally {
                    dataOutputStream.writeUTF(result);
                }

            } catch (IOException ex) {
                System.out.println("ATM connection rejected on port: " + PORT);
            }
        }
    }
}
