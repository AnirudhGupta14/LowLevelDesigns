package PaymentStatus;

import Constants.ParkingLotEnums;

public class CreditCardPayment implements Payment {
    public ParkingLotEnums.PaymentStatus pay(double amount) {
        System.out.println("Paid $" + amount + " by Credit Card");
        return ParkingLotEnums.PaymentStatus.PAID;
    }
}