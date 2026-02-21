package PaymentStatus;

import Constants.ParkingLotEnums;

public interface Payment {
    ParkingLotEnums.PaymentStatus pay(double amount);
}
