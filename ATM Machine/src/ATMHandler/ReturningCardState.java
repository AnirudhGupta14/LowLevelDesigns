package ATMHandler;

import Enums.ATMStatus;
import Models.ATMContext;

public class ReturningCardState implements ATMState {

    public ReturningCardState() {
        System.out.println("ATM is in returning State - Please take your card");
    }

    @Override
    public ATMStatus getStateName() {
        return ATMStatus.RETURNING_CARD;
    }

    @Override
    public ATMState next(ATMContext context) {
        return  context.getStateFactory().createIdleState();
    }
}
