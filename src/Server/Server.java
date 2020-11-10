package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Driver class
public class Server {
    private final int MAX_CLIENTS_CONNECTED = 3;
    private final int PORT = 1026;
    private int currentConnections;
    private ServerSocket serverSocket;
    private boolean serverIsHandled;

    public static void main(String args[]) {
        Server server = new Server();
        server.startServer();
    }

    // Getters
    public void changeServerHandledStatus(boolean newStatus) {
        this.serverIsHandled = newStatus;
    }

    public int getCurrentConnections() {
        return this.currentConnections;
    }

    public int getMaxClientConnected() {
        return this.MAX_CLIENTS_CONNECTED;
    }


    // Methods
    public void closeClient() {
        currentConnections -= 1;
    }


    public boolean getServerHandledStatus() {
        return this.serverIsHandled;
    }

    private void startServer() {
        serverSocket = null;
        try {
            Thread notificationService = new Thread(new NotificationService(this));
            notificationService.start();

            serverSocket = new ServerSocket(PORT);
            acceptClients(serverSocket);
        } catch (IOException e){
            System.err.println("Couldn't listen on port: " + PORT);
            System.exit(1);
        }
    }

    private void acceptClients(ServerSocket serverSocket) {
        System.out.println("Server starts port = " + serverSocket.getLocalSocketAddress());
        while(true) {

            // While there are less than 3 clients using the server
            // another one can use it.
            if(currentConnections < MAX_CLIENTS_CONNECTED) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connected from: " + socket.getRemoteSocketAddress());

                    // Server thread who dispatch client's connection
                    ServerDispatcher serverDispatcher = new ServerDispatcher(this, socket);
                    Thread thread = new Thread(serverDispatcher);
                    thread.start();
                    currentConnections++;
                    System.out.println("Current clients connected: " + currentConnections);
                } catch (IOException ex) {
                    System.out.println("Accept failed on port: " + PORT);
                }
            } else {
                System.out.print("");
            }
        }
    }
}
