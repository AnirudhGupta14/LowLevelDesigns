package Observer;

import Enums.ATMOperation;
import Enums.ATMStatus;
import Enums.TransactionResult;

public class ATMScreen implements ATMObserver {

    @Override
    public void onStateChange(ATMStatus newState) {
        System.out.println("Screen: Current ATM state → " + newState);
    }

    @Override
    public void onOperationSelected(ATMOperation operation) {
        System.out.println("Screen: Operation chosen → " + operation);
    }

    @Override
    public void onTransactionResultChange(TransactionResult transactionResult) {
        System.out.println("Screen: Transaction result → " + transactionResult);
    }
}