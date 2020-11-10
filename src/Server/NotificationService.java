package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Thread for ask server availability
public class NotificationService implements Runnable {
    private Server server;
    final int NOTIFICATION_PORT = 1025;
    ServerSocket notificationServerSocket;
    Socket notificationSocket;
    DataInputStream notificationInputStream;
    DataOutputStream notificationOutputStream;

    public NotificationService(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            notificationServerSocket = new ServerSocket(NOTIFICATION_PORT);
            System.out.println("Server's availability notification service running on port: " + NOTIFICATION_PORT);

            while(true) {
                try {
                    notificationSocket = notificationServerSocket.accept();
                    notificationInputStream = new DataInputStream(notificationSocket.getInputStream());
                    notificationOutputStream = new DataOutputStream(notificationSocket.getOutputStream());
                    System.out.println("New client asking for server availability from: " + notificationSocket.getRemoteSocketAddress());
                    notificationOutputStream.writeBoolean(this.server.getCurrentConnections() < this.server.getMaxClientConnected());
                    notificationOutputStream.writeBoolean(this.server.getServerHandledStatus());
                } catch (IOException ex) {
                    System.out.println("Accept failed on port: " + NOTIFICATION_PORT);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
