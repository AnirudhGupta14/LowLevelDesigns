package ATMHandler;

import Enums.ATMStatus;
import Models.ATMContext;
import Services.Banking;

// Idle State Implementation
public class IdleState implements ATMState {

    Banking banking = new Banking();

    public IdleState() {
        System.out.println("ATM is in Idle State - Please insert your card");
    }

    @Override
    public ATMStatus getStateName() {
        return ATMStatus.IDLE;
    }

    @Override
    public ATMState next(ATMContext context) {

        String cardNumber = context.getCardNumber();

        if (cardNumber != null &&
                banking.validateCardNumber(cardNumber)) {
            return context.getStateFactory().createHasCardState();
        }
        return this;
    }
}


