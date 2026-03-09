package state;

import atm.ATM;
import model.Account;
import model.Transaction;
import service.TransactionService;

/**
 * A transaction is in progress.
 * All financial operations (withdraw, deposit, balance, transfer) happen here.
 *
 * After each operation the ATM returns to IDLE (card ejected).
 */
public class TransactionState implements ATMStateHandler {

    private final ATM atm;
    private final TransactionService transactionService;

    public TransactionState(ATM atm, TransactionService transactionService) {
        this.atm = atm;
        this.transactionService = transactionService;
    }

    @Override
    public Transaction withdraw(double amount) {
        Account account = atm.getCurrentAccount();
        Transaction result = transactionService.withdraw(account, amount);
        atm.ejectCardAndReset();
        return result;
    }

    @Override
    public Transaction deposit(double amount) {
        Account account = atm.getCurrentAccount();
        Transaction result = transactionService.deposit(account, amount);
        atm.ejectCardAndReset();
        return result;
    }

    @Override
    public Transaction checkBalance() {
        Account account = atm.getCurrentAccount();
        Transaction result = transactionService.checkBalance(account);
        atm.ejectCardAndReset();
        return result;
    }

    @Override
    public Transaction transfer(Account toAccount, double amount) {
        Account fromAccount = atm.getCurrentAccount();
        Transaction result = transactionService.transfer(fromAccount, toAccount, amount);
        atm.ejectCardAndReset();
        return result;
    }

    @Override
    public void ejectCard() {
        System.out.println("[TransactionState] Transaction cancelled. Card ejected.");
        atm.ejectCardAndReset();
    }
}
