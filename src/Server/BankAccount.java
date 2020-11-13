package Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Shared resource
public class BankAccount {
    private double balance = 1000.0D;

    public String getTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public double getBalance() {
        return this.balance;
    }

    public String withdraw(double withdraw) {
        this.balance -= withdraw;
        return getTime();
    }

    public String deposit(double deposit) {
        this.balance += deposit;
        return getTime();
    }
}
