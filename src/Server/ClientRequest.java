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

    public ClientRequest(BankAccount sharedAccount, String ATM, String operation, double ammount) {
        this.sharedAccount = sharedAccount;
        this.ATM = ATM;
        this.operation = operation;
        this.ammount = ammount;
        operationDetails = "---- OPERATION DETAILS ----\n" + "Client: " + ATM + "\n";

    }

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "[" + dtf.format(now) + "]";
    }

    public String getATM() {
        return this.ATM;
    }

    public void cancelRequest() {
        Thread.currentThread().interrupt();
    }

    @Override
    public String call() throws Exception {
        operationDetails += getTime() + "\nInitial balance: $" + String.format("%.2f", sharedAccount.getBalance()) + "\n";

        switch (operation) {
            case "withdraw" -> {
                if (sharedAccount.getBalance() > ammount) {
                    sharedAccount.withdraw(ammount);
                    operationDetails += "$" + String.format("%.2f", ammount) + " withdrawn. \n" +
                            "Final balance: $" + String.format("%.2f", sharedAccount.getBalance()) + "\n\n";
                } else operationDetails += "You do not have enough money to withdraw!";
            }
            case "deposit" -> {
                sharedAccount.deposit(ammount);
                operationDetails += "$" + String.format("%.2f", ammount) + " deposited. \n" +
                        "Final balance: $" + String.format("%.2f", sharedAccount.getBalance()) + "\n\n";
            }
            default -> {
                operationDetails += "Invalid operation!";
            }
        }

        Thread.sleep(2000);
        return  operationDetails;
    }
}
