package Models;

public class ATMRequest {

    String cardNumber;
    String pinNumber;

    public ATMRequest(String cardNumber, String pinNumber) {
        this.cardNumber = cardNumber;
        this.pinNumber = pinNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }
}
