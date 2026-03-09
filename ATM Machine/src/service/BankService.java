package service;

import model.Account;
import model.Card;

/**
 * Abstraction over the bank's core banking system.
 *
 * ISP / DIP: ATM depends on this interface, not on concrete bank
 * implementations.
 * Swap implementations (e.g., mock for testing) without touching ATM code.
 */
public interface BankService {

    /** Retrieve account linked to the card. Returns null if not found. */
    Account getAccount(Card card);

    /** Verify a raw PIN against the account's stored PIN. */
    boolean verifyPin(Account account, String rawPin);

    /** Withdraw amount from account. Returns true if successful. */
    boolean withdraw(Account account, double amount);

    /** Deposit amount into account. */
    void deposit(Account account, double amount);

    /**
     * Transfer amount from source to target account. Returns true if successful.
     */
    boolean transfer(Account fromAccount, Account toAccount, double amount);

    /** Get current balance. */
    double getBalance(Account account);
}
