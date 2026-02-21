package Models;

import ATMFactory.ATMStateFactory;
import ATMHandler.ATMState;
import ATMHandler.HasCardState;
import ATMHandler.IdleState;
import ATMHandler.TransactionState;
import Enums.ATMOperation;
import Enums.TransactionResult;
import Services.ATMInventory;

import javax.smartcardio.Card;

public class ATMContext {

    String cardNumber;
    Integer pinNumber;
    ATMOperation selectedOperation;
    TransactionResult transactionResult;
    double amount;
    ATMState currentState;
    ATMInventory atmInventory;

    // Constructor
    public ATMContext() {
        this.stateFactory = ATMStateFactory.getInstance();
        this.currentState = stateFactory.createIdleState();
        this.atmInventory = new ATMInventory();
        System.out.println("ATM initialized in: " + currentState.getStateName());
    }

    public ATMStateFactory getStateFactory() {
        return stateFactory;
    }

    private ATMStateFactory stateFactory;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(Integer pinNumber) {
        this.pinNumber = pinNumber;
    }

    public ATMOperation getSelectedOperation() {
        return selectedOperation;
    }

    public void setSelectedOperation(ATMOperation selectedOperation) {
        this.selectedOperation = selectedOperation;
    }

    public TransactionResult getTransactionResult() {
        return transactionResult;
    }

    public void setTransactionResult(TransactionResult transactionResult) {
        this.transactionResult = transactionResult;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Method to advance to the next state
    public void advanceState() {
        ATMState nextState = currentState.next(this);
        currentState = nextState;
        System.out.println("Current state: " + currentState.getStateName());
    }


    // Card insertion operation
    public void insertCard(Card card) {
        if (currentState instanceof IdleState) {
            System.out.println("Card inserted");
            this.currentCard = card;
            advanceState();
        } else {
            System.out.println(
                    "Cannot insert card in " + currentState.getStateName());
        }
    }

    // PIN authentication operation
    public void enterPin(int pin) {
        if (currentState instanceof HasCardState) {
            if (currentCard.validatePin(pin)) {
                System.out.println("PIN authenticated successfully");
                currentAccount = accounts.get(currentCard.getAccountNumber());
                advanceState();
            } else {
                System.out.println("Invalid PIN. Please try again");
                // Could implement PIN retry logic here
            }
        } else {
            System.out.println("Cannot enter PIN in " + currentState.getStateName());
        }
    }

    // Select operation (withdrawal, balance check, etc.)
    public void selectOperation(TransactionType transactionType) {
        if (currentState instanceof SelectOperationState) {
            System.out.println("Selected operation: " + transactionType);
            this.selectedOperation = transactionType;
            advanceState();
        } else {
            System.out.println(
                    "Cannot select operation in " + currentState.getStateName());
        }
    }

    // Perform the selected transaction
    public void performTransaction(double amount) {
        if (currentState instanceof TransactionState) {
            try {
                if (selectedOperation == TransactionType.WITHDRAW_CASH) {
                    performWithdrawal(amount);
                } else if (selectedOperation == TransactionType.CHECK_BALANCE) {
                    checkBalance();
                }
                // Ask if user wants another transaction
                advanceState();
            } catch (Exception e) {
                System.out.println("Transaction failed: " + e.getMessage());
                // Go back to select operation state
                currentState = stateFactory.createSelectOperationState();
            }
        } else {
            System.out.println(
                    "Cannot perform transaction in " + currentState.getStateName());
        }
    }

    // Return card to user
    public void returnCard() {
        if (currentState instanceof HasCardState
                || currentState instanceof SelectOperationState
                || currentState instanceof TransactionState) {
            System.out.println("Card returned to customer");
            resetATM();
        } else {
            System.out.println("No card to return in " + currentState.getStateName());
        }
    }

    // Cancel current transaction
    public void cancelTransaction() {
        if (currentState instanceof TransactionState
                || currentState instanceof TransactionState) {
            System.out.println("Transaction cancelled");
            returnCard();
        } else {
            System.out.println(
                    "No transaction to cancel in " + currentState.getStateName());
        }
    }


    // Reset ATM state
    private void resetATM() {
        this.currentCard = null;
        this.currentAccount = null;
        this.selectedOperation = null;
        this.currentState = stateFactory.createIdleState();
    }



}
