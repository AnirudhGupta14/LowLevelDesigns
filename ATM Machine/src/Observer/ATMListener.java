package Observer;

import Enums.ATMOperation;
import Enums.ATMStatus;
import Enums.TransactionResult;

import java.util.ArrayList;
import java.util.List;

public class ATMListener {

    private final List<ATMObserver> observers = new ArrayList<>();

    public void addObserver(ATMObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ATMObserver observer) {
        observers.remove(observer);
    }

    public void notifyOnStateChange(ATMStatus state) {
        for (ATMObserver observer : observers) {
            observer.onStateChange(state);
        }
    }

    private void notifyOnOperationSelected(ATMOperation operation) {
        for (ATMObserver observer : observers) {
            observer.onOperationSelected(operation);
        }
    }

    private void notifyOnTransactionResultChange(TransactionResult operationResult) {
        for (ATMObserver observer : observers) {
            observer.onTransactionResultChange(operationResult);
        }
    }
}
