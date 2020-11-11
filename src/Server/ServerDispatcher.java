package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Client's request manager
public class ServerDispatcher implements Runnable {
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Server server;
    Socket socket;
    BankAccount sharedAccount;

    public ServerDispatcher(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        dataInputStream = null;
        dataOutputStream = null;
        sharedAccount = new BankAccount();
    }

    @Override
    public void run() {
        try{
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            while(!socket.isClosed()){
                if(!this.sharedAccount.getHandledStatus()){

                    // Blocks the resource
                    this.sharedAccount.setHandledStatus(false);
                    dataOutputStream.writeDouble(this.sharedAccount.getBalance());
                    String action="";
                    double ammount = 0;

                    action = dataInputStream.readUTF();
                    dataOutputStream.writeUTF("SERVER: Operation request: " + action);

                    ammount = dataInputStream.readDouble();
                    System.out.println(": " + ammount);

                    switch (action) {
                        case "withdraw" -> {
                            sharedAccount.withdraw(ammount);
                            dataOutputStream.writeUTF(
                                    "SERVER: $" + ammount + " withdrawn. " +
                                            "\nFinal balance: $" + sharedAccount.getBalance()
                                            );
                        }
                        case "deposit" -> {
                            sharedAccount.deposit(ammount);
                            dataOutputStream.writeUTF(
                                    "SERVER: $" + ammount + " deposited. " +
                                            "\nFinal balance: $" + sharedAccount.getBalance()
                            );
                        }
                        default -> {
                            dataOutputStream.writeUTF("SERVER: Bank account: Invalid operation!");
                        }
                    }
                    dataOutputStream.flush();

                    // Release the resource and notifies
                    this.sharedAccount.setHandledStatus(true);
                    notifyAll();
                } else {
                    dataOutputStream.writeUTF("SERVER: server busy, the client will try to connect in the next 10 seconds ...");
                    wait();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("SERVER: Connection closed: " + socket.getRemoteSocketAddress());
            server.closeClient();
        }

    }
}
