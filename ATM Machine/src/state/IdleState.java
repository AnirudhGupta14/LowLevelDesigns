package state;

import atm.ATM;
import model.Card;

/**
 * ATM is idle — waiting for a card to be inserted.
 * Only insertCard() is valid here.
 */
public class IdleState implements ATMStateHandler {

    private final ATM atm;

    public IdleState(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void insertCard(Card card) {
        System.out.println("[IdleState] Card inserted: " + card);
        atm.setCurrentCard(card);
        atm.setState(atm.getCardInsertedState());
        System.out.println("[IdleState] → Moved to CARD_INSERTED state.");
    }
}
