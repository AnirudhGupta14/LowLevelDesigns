package service;

import enums.TransactionStatus;
import enums.TransactionType;
import hardware.CashDispenser;
import model.Account;
import model.Transaction;
import observer.ATMObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates end-to-end ATM transactions and notifies observers.
 *
 * Design:
 * - Uses BankService (DIP) — swappable implementation.
 * - Notifies registered ATMObservers (Observer pattern) after each transaction.
 * - Does NOT manage state transitions (that's ATM's job) — SRP.
 */
public class TransactionService {

    private final BankService bankService;
    private final CashDispenser cashDispenser;
    private final List<ATMObserver> observers = new ArrayList<>();

    public TransactionService(BankService bankService, CashDispenser cashDispenser) {
        this.bankService = bankService;
        this.cashDispenser = cashDispenser;
    }

    // ── Observer management ────────────────────────────────────────────────────

    public void addObserver(ATMObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ATMObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Transaction transaction) {
        for (ATMObserver observer : observers) {
            observer.onTransaction(transaction);
        }
    }

    // ── Transaction operations ─────────────────────────────────────────────────

    /**
     * Withdraw cash from account and physically dispense notes.
     */
    public Transaction withdraw(Account account, double amount) {
        TransactionStatus status;

        if (!cashDispenser.hasSufficientCash(amount)) {
            System.out.println("[Transaction] ATM has insufficient cash.");
            status = TransactionStatus.FAILED;
        } else if (bankService.withdraw(account, amount)) {
            boolean dispensed = cashDispenser.dispense(amount);
            if (dispensed) {
                System.out.printf("[Transaction] Withdrawal of %.2f successful. New balance: %.2f%n",
                        amount, account.getBalance());
                status = TransactionStatus.SUCCESS;
            } else {
                // Dispenser failed — rollback the debit
                bankService.deposit(account, amount);
                System.out.println("[Transaction] Dispenser error. Amount rolled back.");
                status = TransactionStatus.FAILED;
            }
        } else {
            System.out.printf("[Transaction] Insufficient funds. Balance: %.2f, Requested: %.2f%n",
                    account.getBalance(), amount);
            status = TransactionStatus.INSUFFICIENT_FUNDS;
        }

        Transaction transaction = new Transaction(account.getAccountId(), TransactionType.WITHDRAW, amount, status);
        notifyObservers(transaction);
        return transaction;
    }

    /**
     * Deposit cash into account.
     */
    public Transaction deposit(Account account, double amount) {
        cashDispenser.accept(amount);
        bankService.deposit(account, amount);
        System.out.printf("[Transaction] Deposit of %.2f successful. New balance: %.2f%n",
                amount, account.getBalance());

        Transaction transaction = new Transaction(account.getAccountId(), TransactionType.DEPOSIT, amount,
                TransactionStatus.SUCCESS);
        notifyObservers(transaction);
        return transaction;
    }

    /**
     * Check account balance.
     */
    public Transaction checkBalance(Account account) {
        double balance = bankService.getBalance(account);
        System.out.printf("[Transaction] Account balance: %.2f%n", balance);

        Transaction transaction = new Transaction(account.getAccountId(), TransactionType.CHECK_BALANCE, 0,
                TransactionStatus.SUCCESS);
        notifyObservers(transaction);
        return transaction;
    }

    /**
     * Transfer amount to another account.
     */
    public Transaction transfer(Account fromAccount, Account toAccount, double amount) {
        boolean success = bankService.transfer(fromAccount, toAccount, amount);
        TransactionStatus status = success ? TransactionStatus.SUCCESS : TransactionStatus.INSUFFICIENT_FUNDS;

        if (success) {
            System.out.printf("[Transaction] Transferred %.2f from %s to %s%n",
                    amount, fromAccount.getAccountId(), toAccount.getAccountId());
        } else {
            System.out.println("[Transaction] Transfer failed — insufficient funds.");
        }

        Transaction transaction = new Transaction(fromAccount.getAccountId(), TransactionType.TRANSFER, amount, status);
        notifyObservers(transaction);
        return transaction;
    }
}
