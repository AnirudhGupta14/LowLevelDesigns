
import Models.ATMRequest;
import Models.Account;
import Observer.ATMListener;
import Observer.ATMObserver;
import Observer.ATMScreen;
import Services.Banking;

public class Main {
    public static void main(String[] args) {

        Banking banking = new Banking();

        // Create accounts
        Account acc1 = new Account("AC001", 5000, 1234);
        Account acc2 = new Account("AC002", 2000, 4321);

        // Map card numbers to accounts
        banking.addAccount("1111222233334444", acc1);
        banking.addAccount("5555666677778888", acc2);

        ATMRequest request = new ATMRequest("1111222233334444", "1234");

        System.out.println("=========== Welcome to ATM Machine ==========");

        System.out.println("Adding ATM screen as Observer");
        ATMListener atmListener = new ATMListener();

        ATMObserver atmScreen = new ATMScreen();
        atmListener.addObserver(atmScreen);

//        System.out.println("ATM state changes handling");
//
//        ATMContext context = new ATMContext();
//
//        // Create handlers
//        ATMHandler idle = new IdleState();
//        ATMHandler hasCard = new HasCardState();
//        ATMHandler selectOp = new SelectOperationState();
//        ATMHandler returning = new ReturningCardState();
//
//        // Chain them together
//        idle.setNextHandler(hasCard);
//        hasCard.setNextHandler(selectOp);
//        selectOp.setNextHandler(returning);
//        returning.setNextHandler(null);
//
//        idle.handleRequest(context, atmListener);

    }
}