package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class Receiver implements Runnable {
    private ServerSocket serverSocket;
    private BankAccount sharedAccount;
    private ExecutorService executorService;
    private String name;

    public Receiver(String name, ServerSocket serverSocket, BankAccount sharedAccount, ExecutorService executorService) {
        this.name = name;
        this.serverSocket = serverSocket;
        this.sharedAccount = sharedAccount;
        this.executorService = executorService;
    }

    public String getName() { return this.name; }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }

    @Override
    public void run() {
        System.out.println(getTime()+ ": " + getName() + " start listening");
        while (true) {
            try {
                Socket firstSocket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(firstSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(firstSocket.getOutputStream());
                String atm = dis.readUTF();
                String operation = dis.readUTF();
                double ammount = dis.readDouble();

                ClientRequest request = new ClientRequest(
                        sharedAccount,
                        atm,
                        operation,
                        ammount
                );
                System.out.println(getTime() + ": Request from " +
                        atm +
                        " -> " +
                        getName()
                );

                // Go to the queue's executor
                Future<String> taskResult = executorService.submit(request);

                String result = "-";

                try {
                    result = taskResult.get(15, TimeUnit.SECONDS);
                } catch (TimeoutException | InterruptedException | ExecutionException e) {
                    System.out.println(getTime()+ ": " + "Time out reached!");
                } finally {
                    dos.writeUTF(result);
                }

            } catch (IOException ex) {
                System.out.println(getTime()+ ": " + "ATM connection rejected on port: " + serverSocket.getLocalPort());
            }
        }
    }
}
