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
                    String operacion="";
                    int a = 0, b=0, resp = 0;
                    Operaciones op = new Operaciones();

                    operacion = dataInputStream.readUTF();
                    System.out.println("Mensaje recibido " + operacion);
                    //dataOutputStream.writeChars("Introduzca el primer valor\n");

                    a = dataInputStream.readInt();
                    System.out.println("Primer operando recibido: " + a);

                    //dataOutputStream.writeChars("Introduzca el segundo valor\n");
                    b = dataInputStream.readInt();
                    System.out.println("Segundo operando recibido: " + b);
                    switch (operacion) {
                        case "+":
                            resp = operaciones.sumaNumeros(a, b);
                            break;
                        case "-":
                            resp = operaciones.restaNumeros(a, b);
                            break;
                        case "*":
                            resp = operaciones.multNumeros(a, b);
                            break;
                        case "/":
                            resp = operaciones.divNumeros(a, b);
                            break;
                        case "%":
                            resp = operaciones.modNumeros(a, b);
                            break;
                        default:
                            resp = -1000;
                            System.out.println("Invalid operation!");
                    }
                    System.out.println("Resultado es " + resp);
                    dataOutputStream.writeInt(resp);
                    //dataOutputStream.writeChars("Resultado es " + resp+"\n");
                    dataOutputStream.flush();

                }


            }
        } catch (IOException e) {
            System.out.println("Connection closed: " + socket.getRemoteSocketAddress());
            server.closeClient();
            System.out.println(server.clientConnected);
        }

    }
}
