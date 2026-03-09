package state;

import model.Account;
import model.Transaction;
import model.Card;

/**
 * Strategy interface for each ATM state.
 *
 * Each concrete state only implements the operations it supports.
 * Invalid operations throw IllegalStateException immediately — this
 * makes debugging trivial and prevents silent invalid-state usage.
 *
 * Default methods below act as "do-nothing guards" for operations
 * that are not valid in a given state.
 *
 * Design: State Pattern — each state encapsulates only valid behavior.
 * Compatible with Java 8+.
 */
public interface ATMStateHandler {

    default void insertCard(Card card) {
        throw new IllegalStateException("[ATM] insertCard() not allowed in current state.");
    }

    default void ejectCard() {
        throw new IllegalStateException("[ATM] ejectCard() not allowed in current state.");
    }

    default void enterPin(String pin) {
        throw new IllegalStateException("[ATM] enterPin() not allowed in current state.");
    }

    default void selectTransaction(String transactionType) {
        throw new IllegalStateException("[ATM] selectTransaction() not allowed in current state.");
    }

    default Transaction withdraw(double amount) {
        throw new IllegalStateException("[ATM] withdraw() not allowed in current state.");
    }

    default Transaction deposit(double amount) {
        throw new IllegalStateException("[ATM] deposit() not allowed in current state.");
    }

    default Transaction checkBalance() {
        throw new IllegalStateException("[ATM] checkBalance() not allowed in current state.");
    }

    default Transaction transfer(Account toAccount, double amount) {
        throw new IllegalStateException("[ATM] transfer() not allowed in current state.");
    }
}
