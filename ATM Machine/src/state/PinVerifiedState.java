package state;

import atm.ATM;

/**
 * PIN has been verified. Valid operations: selectTransaction, ejectCard.
 */
public class PinVerifiedState implements ATMStateHandler {

    private final ATM atm;

    public PinVerifiedState(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void selectTransaction(String transactionType) {
        System.out.println("[PinVerifiedState] Transaction selected: " + transactionType);
        atm.setState(atm.getTransactionState());
        System.out.println("[PinVerifiedState] → Moved to TRANSACTION state.");
    }

    @Override
    public void ejectCard() {
        System.out.println("[PinVerifiedState] Card ejected by user.");
        atm.ejectCardAndReset();
    }
}
