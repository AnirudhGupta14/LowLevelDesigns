package ATMHandler;

import Enums.ATMStatus;
import Models.ATMContext;
import Observer.ATMListener;
import Services.Banking;

// Has Card State Implementation
public class HasCardState implements ATMState {

    Banking banking = new Banking();

    public HasCardState() {
        System.out.println("ATM is in Has Card State - Please enter your PIN");
    }

    @Override
    public ATMStatus getStateName() {
        return ATMStatus.HAS_CARD;
    }

    @Override
    public ATMState next(ATMContext context) {

        String cardNumber = context.getCardNumber();
        Integer pinNumber = context.getPinNumber();

        if (cardNumber != null &&
                banking.validateCardNumber(cardNumber) &&
                banking.validatePin(cardNumber, pinNumber)) {
            return context.getStateFactory().createSelectOperationState();
        }

        return context.getStateFactory().createIdleState();
    }
}