package enums;

/**
 * Represents the lifecycle states of the ATM machine.
 *
 * State Transitions:
 * IDLE → CARD_INSERTED → PIN_VERIFIED → TRANSACTION → IDLE
 * Any state → OUT_OF_SERVICE (on hardware failure)
 */
public enum ATMState {
    IDLE, // Waiting for a card to be inserted
    CARD_INSERTED, // Card inserted; awaiting PIN entry
    PIN_VERIFIED, // PIN validated; awaiting transaction selection
    TRANSACTION, // A transaction is in progress
    OUT_OF_SERVICE // ATM is unavailable (e.g., cash empty, maintenance)
}
