import Constants.ParkingLotEnums;
import ParkingSpots.CompactSpot;
import ParkingSpots.LargeSpot;
import ParkingSpots.MiniSpot;
import ParkingStrategy.NearestFirstParking;
import PaymentStatus.CreditCardPayment;
import PaymentStatus.Payment;
import Services.*;
import Vehicles.Car;
import Vehicles.Vehicle;

public class Main {
    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot();
        Admin admin = new Admin("Admin1");

        // Add floors
        Floor floor0 = new Floor(0);
        floor0.addSpot(new MiniSpot("M1"));
        floor0.addSpot(new CompactSpot("C1"));
        floor0.addSpot(new LargeSpot("L1"));
        admin.addFloor(lot, floor0);

        // Add entrance and exit
        ParkingAttendant attendant = new ParkingAttendant("Attendant1");
        admin.addEntrance(lot, new Entrance(1, attendant));
        admin.addExit(lot, new Exit(1));

        // Parking Vehicles (nearest first parking)
        Vehicle car = new Car("KA01AB1234");
        ParkingTicket ticket = attendant.createTicket(car, lot, new NearestFirstParking());

        Exit exit = lot.exits.get(0);
        Payment payment = new CreditCardPayment();
        double amount = ticket.calculateFee();

        if (payment.pay(amount)== ParkingLotEnums.PaymentStatus.PAID) {
            attendant.freeSpot(ticket.spot);
            lot.updateDisplay();
        }
    }
}