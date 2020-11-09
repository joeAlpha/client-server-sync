package Server;

// Shared resource
public class BankAccount {
    private double balance = 1000.0D;

    public double getBalance() {
        return this.balance;
    }

    public void withdraw(double withdraw) {
        this.balance -= withdraw;
    }

    public void deposit(double deposit) {
        this.balance += deposit;
    }
}
