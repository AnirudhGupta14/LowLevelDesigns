package Services;

import Models.Account;

import java.util.HashMap;
import java.util.Map;

public class Banking {

    private final Map<String, Account> cardAccountMap = new HashMap<>();

    public void addAccount(String cardNumber, Account account) {
        cardAccountMap.put(cardNumber, account);
    }

    public boolean validateCardNumber(String cardNumber) {
        return cardAccountMap.containsKey(cardNumber);
    }

    public boolean validatePin(String cardNumber, int enteredPin) {
        Account account = cardAccountMap.get(cardNumber);
        if (account == null) return false;
        return account.getPinNumber().equals(enteredPin);
    }

    public double checkBalance(String cardNumber) {
        Account account = cardAccountMap.get(cardNumber);
        if (account == null) throw new IllegalArgumentException("Invalid card number");
        return account.getBalance();
    }

    public void addCash(String cardNumber, double amount) {
        Account account = cardAccountMap.get(cardNumber);
        if (account != null) {
            account.deposit(amount);
        }
    }

    public boolean withdrawCash(String cardNumber, double amount) {
        Account account = cardAccountMap.get(cardNumber);
        if (account != null) {
            return account.withdraw(amount);
        }
        return false;
    }
}
