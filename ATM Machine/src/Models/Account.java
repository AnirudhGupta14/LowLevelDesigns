package Models;

public class Account {

    private final String accountNumber;
    private double balance;
    private final Integer pinNumber;

    public Account(String accountNumber, double initialBalance, Integer pinNumber) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.pinNumber = pinNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public Integer getPinNumber() {
        return pinNumber;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}