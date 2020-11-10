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
                if(dataOutputStream != null){
                    String action="";
                    int ammount = 0;

                    action = dataInputStream.readUTF();
                    System.out.println("Operation request: " + action);

                    ammount = dataInputStream.readInt();
                    System.out.println(": " + a);

                    switch (action) {
                        case "withdraw" -> sharedAccount.withdraw(ammount);
                        case "deposit" -> sharedAccount.deposit(ammount);
                        default -> {
                            System.out.println("Bank account: Invalid operation!");
                        }
                    }
                    System.out.println("Bank account: final balance" + sharedAccount.getBalance());
                    dataOutputStream.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Connection closed: " + socket.getRemoteSocketAddress());
            server.closeClient();
        }

    }
}
