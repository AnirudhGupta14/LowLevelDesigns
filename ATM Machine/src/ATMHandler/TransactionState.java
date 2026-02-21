package ATMHandler;

import Enums.ATMOperation;
import Enums.ATMStatus;
import Models.ATMContext;
import Services.Banking;

public class TransactionState {

    Banking banking = new Banking();

    public TransactionState() {
        System.out.println("ATM is in Transaction State");
    }

    @Override
    public ATMStatus getStateName() {
        return ATMStatus.TRANSACTIONAL_STATE;
    }

    @Override
    public ATMState next(ATMContext context) {

        String cardNumber = context.getCardNumber();
        Integer pinNumber = context.getPinNumber();

        if (cardNumber != null &&
                banking.validateCardNumber(cardNumber) &&
                banking.validatePin(cardNumber, pinNumber) &&
                context.getSelectedOperation() != null) {
                performTransaction(context);
        }

        // After transaction completion, go back to select operation
        return context.getStateFactory().createReturningState();
    }

    public void performTransaction(ATMContext context) {

        ATMOperation operation = context.getSelectedOperation();
        String cardNumber = context.getCardNumber();
        double amount = context.getAmount();

         switch (operation) {
             case ATMOperation.CHECK_BALANCE:
                 double balance = banking.checkBalance(cardNumber);
                 System.out.println("[ATM] Your balance is: $" + balance);
                 break;

             case ATMOperation.CASH_WITHDRAWAL:
                 System.out.print("[ATM] Enter withdrawal amount: ");
                 if (banking.withdrawCash(cardNumber, amount)) {
                     System.out.println("[ATM] Please collect your cash: $" + amount);
                 } else {
                     System.out.println("[ATM] Withdrawal failed. Insufficient balance.");
                 }
                 break;
             case ATMOperation.CASH_DEPOSIT:
                 System.out.print("[ATM] Enter deposit amount: ");
                 banking.addCash(cardNumber, amount);
                 System.out.println("[ATM] Amount deposited successfully.");
                 break;
             default:
                 System.out.println("[ATM] Invalid option.");
         }
    }
}
