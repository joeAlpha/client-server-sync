package Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

// Client's request manager
// Callable are similar to Runnable but this interface
// returns a value after its execution.
public class ClientRequest implements Callable<String> {
    private String operation;
    private double ammount;
    BankAccount sharedAccount;
    private String ATM;
    private String operationDetails;
    private String arriveTime;

    public ClientRequest(BankAccount sharedAccount, String ATM, String operation, double ammount, String arriveTime) {
        this.sharedAccount = sharedAccount;
        this.ATM = ATM;
        this.operation = operation;
        this.ammount = ammount;
        this.arriveTime = arriveTime;
        operationDetails = "---- OPERATION DETAILS ----\n" + "Client: " + ATM + "\n";

    }


    public String getATM() {
        return this.ATM;
    }

    public void cancelRequest() {
        Thread.currentThread().interrupt();
    }

    @Override
    public String call() throws Exception {
        operationDetails += "Initial balance: $" + String.format("%.2f", sharedAccount.getBalance()) + "\n" +
            "Arrive time: " + this.arriveTime + "\n";
        String operationTime;

        switch (operation) {
            case "withdraw" -> {
                if (sharedAccount.getBalance() > ammount) {
                    operationTime = sharedAccount.withdraw(ammount);
                    operationDetails += "Serve time: " + operationTime + "\nOperation: $" + String.format("%.2f", ammount) + " withdrawn. \n" +
                            "Final balance: $" + String.format("%.2f", sharedAccount.getBalance()) + "\n";
                } else operationDetails += "You do not have enough money to withdraw!";
            }
            case "deposit" -> {
                operationTime = sharedAccount.deposit(ammount);
                operationDetails += "Serve time: " + operationTime + "\nOperation: $" + String.format("%.2f", ammount) + " deposited. \n" +
                        "Final balance: $" + String.format("%.2f", sharedAccount.getBalance()) + "\n";
            }
            default -> {
                operationDetails += "Invalid operation!";
            }
        }

        return operationDetails;
    }
}
