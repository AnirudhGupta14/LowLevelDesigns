package ATMHandler;

import Enums.ATMStatus;
import Models.ATMContext;

public interface ATMState {
    // Get the name of the current state
    ATMStatus getStateName();

    // Method to handle state transitions
    ATMState next(ATMContext context);
}