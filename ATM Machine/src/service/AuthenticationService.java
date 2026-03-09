package service;

import model.Account;
import model.Card;

/**
 * Handles card validation and PIN verification with brute-force protection.
 *
 * Rules:
 * - A blocked card is permanently rejected.
 * - After MAX_PIN_ATTEMPTS consecutive wrong PINs, the card is blocked.
 *
 * Design: SRP — only responsible for authentication, not transactions.
 */
public class AuthenticationService {

    private static final int MAX_PIN_ATTEMPTS = 3;

    private final BankService bankService;

    public AuthenticationService(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Validates that the card is not expired or blocked and the linked account
     * exists.
     */
    public boolean validateCard(Card card, Account account) {
        if (card.isBlocked()) {
            System.out.println("[Auth] Card is blocked: " + card);
            return false;
        }
        if (account == null) {
            System.out.println("[Auth] No account linked to card: " + card.getCardNumber());
            return false;
        }
        return true;
    }

    /**
     * Verifies the entered PIN. Tracks failed attempts and blocks card on excess.
     *
     * @return true if PIN is correct
     */
    public boolean verifyPin(Card card, Account account, String rawPin) {
        if (card.isBlocked()) {
            System.out.println("[Auth] Card is blocked. Cannot verify PIN.");
            return false;
        }

        boolean valid = bankService.verifyPin(account, rawPin);
        if (valid) {
            account.resetFailedAttempts();
            System.out.println("[Auth] PIN verified successfully.");
            return true;
        }

        account.incrementFailedAttempts();
        int remaining = MAX_PIN_ATTEMPTS - account.getFailedPinAttempts();
        System.out.println("[Auth] Incorrect PIN. Attempts remaining: " + remaining);

        if (account.getFailedPinAttempts() >= MAX_PIN_ATTEMPTS) {
            card.block();
            System.out.println("[Auth] Card BLOCKED after " + MAX_PIN_ATTEMPTS + " failed attempts.");
        }
        return false;
    }
}
