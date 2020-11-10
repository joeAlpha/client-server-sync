package Server;

// Shared resource
public class BankAccount {
    private double balance = 1000.0D;
    private boolean isHandled = false;

    public double getBalance() {
        return this.balance;
    }

    public void withdraw(double withdraw) {
        this.balance -= withdraw;
    }

    public void deposit(double deposit) {
        this.balance += deposit;
    }

    public boolean getHandledStatus() {
        return this.isHandled;
    }

    public void setHandledStatus(boolean newStatus) {
        this.isHandled = newStatus;
    }
}
