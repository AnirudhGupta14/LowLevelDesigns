package atm;

import atm.ATM;
import enums.TransactionType;
import model.Account;
import model.Card;
import model.Transaction;

/**
 * Facade over the ATM core — provides a clean, readable API for clients.
 *
 * Design: Facade Pattern — hides internal wiring (state management).
 * Makes the Main.java demo clean and easy to follow in interviews.
 */
public class ATMController {

    private final ATM atm;

    public ATMController(ATM atm) {
        this.atm = atm;
    }

    /** Step 1: Insert a card. */
    public void insertCard(Card card) {
        printStep("INSERT CARD");
        atm.insertCard(card);
    }

    /** Step 2: Enter PIN. */
    public void enterPin(String pin) {
        printStep("ENTER PIN");
        atm.enterPin(pin);
    }

    /** Step 3a: Select WITHDRAW and provide amount. */
    public Transaction withdraw(double amount) {
        printStep("WITHDRAW: " + amount);
        atm.selectTransaction(TransactionType.WITHDRAW.name());
        return atm.withdraw(amount);
    }

    /** Step 3b: Select DEPOSIT and provide amount. */
    public Transaction deposit(double amount) {
        printStep("DEPOSIT: " + amount);
        atm.selectTransaction(TransactionType.DEPOSIT.name());
        return atm.deposit(amount);
    }

    /** Step 3c: Check account balance. */
    public Transaction checkBalance() {
        printStep("CHECK BALANCE");
        atm.selectTransaction(TransactionType.CHECK_BALANCE.name());
        return atm.checkBalance();
    }

    /** Step 3d: Transfer funds to another account. */
    public Transaction transfer(Account toAccount, double amount) {
        printStep("TRANSFER: " + amount + " → " + toAccount.getAccountId());
        atm.selectTransaction(TransactionType.TRANSFER.name());
        return atm.transfer(toAccount, amount);
    }

    /** Eject card at any step to cancel. */
    public void ejectCard() {
        printStep("EJECT CARD");
        atm.ejectCard();
    }

    private void printStep(String msg) {
        System.out.println("\n══════════ " + msg + " ══════════");
    }
}
