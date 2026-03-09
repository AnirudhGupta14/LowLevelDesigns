import atm.ATM;
import atm.ATMController;
import hardware.CashDispenser;
import model.Account;
import model.Card;
import model.Transaction;
import observer.AlertService;
import observer.TransactionLogger;
import service.BankServiceImpl;

/**
 * ═══════════════════════════════════════════════════════════════════
 * ATM Machine — Low-Level Design Demo
 * ═══════════════════════════════════════════════════════════════════
 *
 * Demonstrates:
 * 1. Normal flow : Insert card → Enter PIN → Withdraw → Eject
 * 2. Balance check: Insert card → Enter PIN → Check Balance
 * 3. Transfer : Insert card → Enter PIN → Transfer funds
 * 4. Wrong PIN : Card gets blocked after 3 failures
 * 5. Concurrency : Two threads racing to withdraw from the same
 * account — only one succeeds (locking demo)
 *
 * Design Patterns Used:
 * - Singleton : ATM (single machine instance)
 * - State : IdleState → CardInsertedState → PinVerifiedState → TransactionState
 * - Observer : TransactionLogger, AlertService
 * - Facade : ATMController
 * - Strategy : BankService interface (swap implementations)
 *
 * SOLID:
 * - S: Each class has a single responsibility
 * - O: New transaction types added without modifying ATM core
 * - L: BankServiceImpl substitutes BankService everywhere
 * - I: ATMStateHandler, ATMObserver, BankService are narrow interfaces
 * - D: ATM depends on BankService interface, not BankServiceImpl
 *
 * Locking:
 * - Per-account ReentrantLock (BankServiceImpl.withdraw/deposit/transfer)
 * - CashDispenser ReentrantLock (prevents double-dispensing)
 * - Deadlock-safe transfer (deterministic lock ordering by accountId)
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ── Setup ─────────────────────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       ATM Machine — LLD Demo                 ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // Bank service (in-memory) — could be swapped for a real bank API
        BankServiceImpl bankService = new BankServiceImpl();

        // Accounts (PIN stored as plain string; use BCrypt in production)
        Account alice = new Account("ACC001", "1234", 10000.0);
        Account bob = new Account("ACC002", "5678", 5000.0);
        bankService.registerAccount(alice);
        bankService.registerAccount(bob);

        // Cards
        Card aliceCard = new Card("4111111111110001", "ACC001", "12/26");
        Card bobCard = new Card("4111111111110002", "ACC002", "06/25");

        // Hardware — 50 notes of ₹100
        CashDispenser dispenser = new CashDispenser(50, 100);

        // ATM Singleton
        ATM atm = ATM.getInstance(bankService, dispenser);
        atm.addObserver(new TransactionLogger());
        atm.addObserver(new AlertService());

        ATMController controller = new ATMController(atm);

        // ─────────────────────────────────────────────────────────────────────
        // Scenario 1: Normal Withdrawal
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n━━━ Scenario 1: Normal Withdrawal ━━━");
        controller.insertCard(aliceCard);
        controller.enterPin("1234");
        Transaction t1 = controller.withdraw(3000.0);
        System.out.println("Result: " + t1.getStatus());

        // ─────────────────────────────────────────────────────────────────────
        // Scenario 2: Balance Check
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n━━━ Scenario 2: Check Balance ━━━");
        controller.insertCard(aliceCard);
        controller.enterPin("1234");
        Transaction t2 = controller.checkBalance();
        System.out.println("Result: " + t2.getStatus());

        // ─────────────────────────────────────────────────────────────────────
        // Scenario 3: Transfer Alice → Bob
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n━━━ Scenario 3: Transfer ━━━");
        controller.insertCard(aliceCard);
        controller.enterPin("1234");
        Transaction t3 = controller.transfer(bob, 2000.0);
        System.out.println("Result: " + t3.getStatus());

        // ─────────────────────────────────────────────────────────────────────
        // Scenario 4: Insufficient Funds
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n━━━ Scenario 4: Insufficient Funds ━━━");
        controller.insertCard(aliceCard);
        controller.enterPin("1234");
        Transaction t4 = controller.withdraw(99999.0);
        System.out.println("Result: " + t4.getStatus());

        // ─────────────────────────────────────────────────────────────────────
        // Scenario 5: Wrong PIN — Card Blocked
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n━━━ Scenario 5: Wrong PIN (card blocking) ━━━");
        controller.insertCard(bobCard);
        controller.enterPin("0000"); // Wrong
        controller.enterPin("0000"); // Wrong
        controller.enterPin("0000"); // Wrong → blocked
        System.out.println("Bob's card blocked: " + bobCard.isBlocked());

        // ─────────────────────────────────────────────────────────────────────
        // Scenario 6: Concurrent Withdrawal — Locking Demo
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n━━━ Scenario 6: Concurrent Withdrawal (Locking Demo) ━━━");

        // Alice's remaining balance should be ~5000 after scenarios 1,3,4
        // Two threads each try to withdraw 4000 — only one should succeed
        Account sharedAccount = alice;
        Card threadCard1 = new Card("4111111111110001", "ACC001", "12/26");
        Card threadCard2 = new Card("4111111111110001", "ACC001", "12/26");

        System.out.println("Alice's balance before concurrent withdrawals: " + alice.getBalance());

        Thread t = new Thread(() -> {
            // Direct bank service call to demonstrate locking at service layer
            boolean success = bankService.withdraw(sharedAccount, 4000.0);
            System.out.println("[Thread-1] Withdraw 4000: " + (success ? "SUCCESS" : "FAILED"));
        });

        Thread t2Thread = new Thread(() -> {
            boolean success = bankService.withdraw(sharedAccount, 4000.0);
            System.out.println("[Thread-2] Withdraw 4000: " + (success ? "SUCCESS" : "FAILED"));
        });

        t.start();
        t2Thread.start();
        t.join();
        t2Thread.join();

        System.out.println("Alice's balance after concurrent withdrawals: " + alice.getBalance());

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n\n╔══════════════════════════════════════════════╗");
        System.out.println("║          Demo Complete                        ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }
}