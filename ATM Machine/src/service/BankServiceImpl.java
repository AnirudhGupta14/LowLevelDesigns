package service;

import model.Account;
import model.Card;

import java.util.HashMap;
import java.util.Map;

/**
 * Concrete implementation of the bank's core banking operations.
 *
 * Locking Strategy:
 * Each withdraw/deposit/transfer acquires the account's own ReentrantLock.
 * For transfers, locks are acquired in a deterministic order (by accountId)
 * to prevent deadlocks (standard "lock ordering" pattern).
 *
 * In production: this would make network calls to the core banking system.
 */
public class BankServiceImpl implements BankService {

    // In-memory account store (simulates a database)
    private final Map<String, Account> accountStore = new HashMap<>();

    public void registerAccount(Account account) {
        accountStore.put(account.getAccountId(), account);
    }

    @Override
    public Account getAccount(Card card) {
        return accountStore.get(card.getAccountId());
    }

    @Override
    public boolean verifyPin(Account account, String rawPin) {
        // Simplified: compare directly. Production: BCrypt.checkpw(rawPin,
        // account.getHashedPin())
        return account.getHashedPin().equals(rawPin);
    }

    @Override
    public boolean withdraw(Account account, double amount) {
        account.getLock().lock();
        try {
            if (account.getBalance() < amount)
                return false;
            account.debit(amount);
            return true;
        } finally {
            account.getLock().unlock();
        }
    }

    @Override
    public void deposit(Account account, double amount) {
        account.getLock().lock();
        try {
            account.credit(amount);
        } finally {
            account.getLock().unlock();
        }
    }

    /**
     * Transfer between two accounts.
     *
     * Deadlock prevention: always lock the account with the lexicographically
     * smaller accountId first, regardless of which is "from" and which is "to".
     */
    @Override
    public boolean transfer(Account fromAccount, Account toAccount, double amount) {
        Account first = fromAccount.getAccountId().compareTo(toAccount.getAccountId()) < 0
                ? fromAccount
                : toAccount;
        Account second = (first == fromAccount) ? toAccount : fromAccount;

        first.getLock().lock();
        try {
            second.getLock().lock();
            try {
                if (fromAccount.getBalance() < amount)
                    return false;
                fromAccount.debit(amount);
                toAccount.credit(amount);
                return true;
            } finally {
                second.getLock().unlock();
            }
        } finally {
            first.getLock().unlock();
        }
    }

    @Override
    public double getBalance(Account account) {
        account.getLock().lock();
        try {
            return account.getBalance();
        } finally {
            account.getLock().unlock();
        }
    }
}
