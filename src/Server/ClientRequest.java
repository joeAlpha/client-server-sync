package Server;

import java.util.concurrent.Callable;

// Client's request manager
// Callable are similar to Runnable but this interface
// returns a value after its execution.
public class ClientRequest implements Callable<String> {
    private String operation;
    private double ammount;
    BankAccount sharedAccount;

    public ClientRequest(BankAccount sharedAccount, String operation, double ammount) {
        this.sharedAccount = sharedAccount;
        this.operation = operation;
        this.ammount = ammount;
    }

    @Override
    public String call() throws Exception {
        switch (operation) {
            case "withdraw" -> {
                if (sharedAccount.getBalance() > ammount) {
                    sharedAccount.withdraw(ammount);
                    return "---- OPERATION DETAILS ----\n" +
                            ammount + " withdrawn. " +
                            "\nFinal balance: $" + sharedAccount.getBalance();
                } else {
                    return "---- OPERATION DETAILS ----\n" +
                            "Balance: $" + sharedAccount.getBalance() +
                            "You don't have enough money to withdraw!";
                }
            }
            case "deposit" -> {
                sharedAccount.deposit(ammount);
                return "---- OPERATION DETAILS ----\n" +
                        ammount + " deposited. " +
                        "\nFinal balance: $" + sharedAccount.getBalance();
            }
            default -> {
                return "Invalid operation!";
            }
        }
    }
}
