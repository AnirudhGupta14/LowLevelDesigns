package atm;

import hardware.CashDispenser;
import model.Account;
import model.Card;
import observer.ATMObserver;
import service.AuthenticationService;
import service.BankService;
import service.BankServiceImpl;
import service.TransactionService;
import state.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The core ATM machine — Singleton.
 *
 * Responsibilities:
 * - Holds the current state (State Pattern).
 * - Delegates all operations to the current state.
 * - Manages the card/account session.
 * - Maintains the list of observers.
 *
 * Why Singleton?
 * There is only one physical ATM unit; a global single instance
 * reflects the real-world constraint and avoids duplicate state.
 */
public class ATM {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static ATM instance;

    public static synchronized ATM getInstance(BankService bankService, CashDispenser cashDispenser) {
        if (instance == null) {
            instance = new ATM(bankService, cashDispenser);
        }
        return instance;
    }

    // ── Core dependencies ─────────────────────────────────────────────────────

    private final BankService bankService;
    private final CashDispenser cashDispenser;
    private final AuthenticationService authService;
    private final TransactionService transactionService;

    // ── State pattern — all concrete states pre-built ──────────────────────────

    private final ATMStateHandler idleState;
    private final ATMStateHandler cardInsertedState;
    private final ATMStateHandler pinVerifiedState;
    private final ATMStateHandler transactionState;

    private ATMStateHandler currentState;

    // ── Current session ───────────────────────────────────────────────────────

    private Card currentCard;
    private Account currentAccount;

    // ── Private constructor ───────────────────────────────────────────────────

    private ATM(BankService bankService, CashDispenser cashDispenser) {
        this.bankService = bankService;
        this.cashDispenser = cashDispenser;

        this.authService = new AuthenticationService(bankService);
        this.transactionService = new TransactionService(bankService, cashDispenser);

        // Build all states, passing 'this' so they can trigger state transitions
        this.idleState = new IdleState(this);
        this.cardInsertedState = new CardInsertedState(this, authService);
        this.pinVerifiedState = new PinVerifiedState(this);
        this.transactionState = new TransactionState(this, transactionService);

        this.currentState = idleState; // Start in IDLE
    }

    // ── State delegation (facade for external callers) ────────────────────────

    public void insertCard(Card card) {
        // Eagerly load account at insert-time so states have it available
        currentAccount = bankService.getAccount(card);
        currentState.insertCard(card);
    }

    public void ejectCard() {
        currentState.ejectCard();
    }

    public void enterPin(String pin) {
        currentState.enterPin(pin);
    }

    public void selectTransaction(String type) {
        currentState.selectTransaction(type);
    }

    public model.Transaction withdraw(double amount) {
        return currentState.withdraw(amount);
    }

    public model.Transaction deposit(double amount) {
        return currentState.deposit(amount);
    }

    public model.Transaction checkBalance() {
        return currentState.checkBalance();
    }

    public model.Transaction transfer(Account toAccount, double amount) {
        return currentState.transfer(toAccount, amount);
    }

    // ── Session management ────────────────────────────────────────────────────

    /** Called by states to cleanly end a session and return to IDLE. */
    public void ejectCardAndReset() {
        System.out.println("[ATM] Card ejected. Session ended. Returning to IDLE.");
        this.currentCard = null;
        this.currentAccount = null;
        this.currentState = idleState;
    }

    // ── Observer management ───────────────────────────────────────────────────

    public void addObserver(ATMObserver observer) {
        transactionService.addObserver(observer);
    }

    public void removeObserver(ATMObserver observer) {
        transactionService.removeObserver(observer);
    }

    // ── State getters (used by concrete states to trigger transitions) ────────

    public ATMStateHandler getIdleState() {
        return idleState;
    }

    public ATMStateHandler getCardInsertedState() {
        return cardInsertedState;
    }

    public ATMStateHandler getPinVerifiedState() {
        return pinVerifiedState;
    }

    public ATMStateHandler getTransactionState() {
        return transactionState;
    }

    public void setState(ATMStateHandler state) {
        this.currentState = state;
    }

    // ── Session accessors ─────────────────────────────────────────────────────

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card card) {
        this.currentCard = card;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }
}
