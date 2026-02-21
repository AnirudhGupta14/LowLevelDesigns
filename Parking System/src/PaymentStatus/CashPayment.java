package PaymentStatus;

import Constants.ParkingLotEnums;

public class CashPayment implements Payment {
    public ParkingLotEnums.PaymentStatus pay(double amount) {
        System.out.println("Paid $" + amount + " by Cash");
        return ParkingLotEnums.PaymentStatus.PAID;
    }
}
