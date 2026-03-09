package state;

import atm.ATM;
import model.Account;
import model.Card;
import service.AuthenticationService;

/**
 * Card has been inserted. Valid operations: enterPin, ejectCard.
 */
public class CardInsertedState implements ATMStateHandler {

    private final ATM atm;
    private final AuthenticationService authService;

    public CardInsertedState(ATM atm, AuthenticationService authService) {
        this.atm = atm;
        this.authService = authService;
    }

    @Override
    public void enterPin(String pin) {
        Card card = atm.getCurrentCard();
        Account account = atm.getCurrentAccount();

        // Validate card and account linkage first
        if (!authService.validateCard(card, account)) {
            atm.ejectCardAndReset();
            return;
        }

        // Verify PIN — handles blocking internally
        boolean pinCorrect = authService.verifyPin(card, account, pin);
        if (pinCorrect) {
            atm.setState(atm.getPinVerifiedState());
            System.out.println("[CardInsertedState] → Moved to PIN_VERIFIED state.");
        } else if (card.isBlocked()) {
            atm.ejectCardAndReset();
        }
        // If PIN wrong but card not yet blocked, stay in CARD_INSERTED for retry
    }

    @Override
    public void ejectCard() {
        System.out.println("[CardInsertedState] Card ejected by user.");
        atm.ejectCardAndReset();
    }
}
