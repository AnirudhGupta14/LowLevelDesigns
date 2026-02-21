package Observer;

import Enums.ATMOperation;
import Enums.ATMStatus;
import Enums.TransactionResult;

public interface ATMObserver {
    void onStateChange(ATMStatus newState);
    void onOperationSelected(ATMOperation operation);
    void onTransactionResultChange(TransactionResult transactionResult);
}
