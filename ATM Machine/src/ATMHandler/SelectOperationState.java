package ATMHandler;

import Enums.ATMStatus;
import Models.ATMContext;
import Services.Banking;

public class SelectOperationState implements ATMState {

    Banking banking = new Banking();

    public SelectOperationState() {
        System.out.println("ATM is in Select Operation State - Please select an operation");
        System.out.println("1. Withdraw Cash");
        System.out.println("2. Check Balance");
        System.out.println("3. Deposit Cash");
    }
    @Override
    public ATMStatus getStateName() {
        return ATMStatus.SELECT_OPERATIONS;
    }
    @Override
    public ATMState next(ATMContext context) {

        String cardNumber = context.getCardNumber();
        Integer pinNumber = context.getPinNumber();

        if (cardNumber != null &&
                banking.validateCardNumber(cardNumber) &&
                banking.validatePin(cardNumber, pinNumber) &&
                context.getSelectedOperation() != null) {
            return context.getStateFactory().createTransactionState();
        }

        return context.getStateFactory().createIdleState();
    }
}