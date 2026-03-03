import constants.VehicleType;
import factory.VehicleFactory;
import models.Reservation;
import models.Store;
import models.User;
import models.Vehicle;
import observer.EmailNotificationObserver;
import service.ReservationService;
import service.UserService;
import service.VehicleService;
import strategy.DailyPricingStrategy;
import strategy.HourlyPricingStrategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Car Renting System — LLD Demo
 *
 * Design Patterns:
 * 1. Strategy — HourlyPricingStrategy / DailyPricingStrategy (swappable at
 * runtime)
 * 2. Factory — VehicleFactory auto-generates IDs
 * 3. Observer — EmailNotificationObserver reacts to reservation events
 * 4. Singleton — ReservationService has one global instance
 *
 * Demo Flow:
 * 1. Set up store + add vehicles (via Factory)
 * 2. Register users
 * 3. Search available vehicles
 * 4. Create reservation (Hourly pricing)
 * 5. Try to book same vehicle again (blocked)
 * 6. Cancel the reservation → vehicle freed
 * 7. Book again with Daily pricing (strategy swap)
 * 8. Complete the reservation → invoice sent
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Setup Store ───────────────────────────────────────────────────────
        printHeader("1. Setting Up Store & Adding Vehicles (Factory Pattern)");
        Store store = new Store("ST-01", "Mumbai");
        VehicleService vehicleService = new VehicleService(store);

        // Factory creates vehicles with auto-generated IDs
        Vehicle car1 = VehicleFactory.createVehicle("Toyota", "Camry", VehicleType.CAR, 150.0);
        Vehicle car2 = VehicleFactory.createVehicle("Honda", "City", VehicleType.CAR, 120.0);
        Vehicle bike1 = VehicleFactory.createVehicle("Royal Enfield", "Bullet", VehicleType.BIKE, 60.0);
        Vehicle truck1 = VehicleFactory.createVehicle("Tata", "Ace", VehicleType.TRUCK, 200.0);

        vehicleService.addVehicle(car1);
        vehicleService.addVehicle(car2);
        vehicleService.addVehicle(bike1);
        vehicleService.addVehicle(truck1);

        // ── 2. Register Users ────────────────────────────────────────────────────
        printHeader("2. Registering Users");
        UserService userService = new UserService();
        User alice = userService.registerUser("Alice", "alice@email.com", "9876543210");
        User bob = userService.registerUser("Bob", "bob@email.com", "9123456789");

        // ── 3. Search Available Vehicles ─────────────────────────────────────────
        printHeader("3. Searching Available Vehicles (type = CAR)");
        List<Vehicle> available = vehicleService.searchAvailableVehicles(VehicleType.CAR);
        available.forEach(v -> System.out.println("     → " + v));

        // ── 4. Setup ReservationService (Singleton + Strategy + Observer) ────────
        printHeader("4. Setting Up ReservationService (Singleton, Hourly Strategy, Email Observer)");
        ReservationService reservationService = ReservationService.getInstance();
        reservationService.setPricingStrategy(new HourlyPricingStrategy());
        reservationService.addObserver(new EmailNotificationObserver());

        // ── 5. Create Reservation ────────────────────────────────────────────────
        printHeader("5. Creating Reservation — Alice books Toyota Camry (5 hours)");
        LocalDateTime start = LocalDateTime.of(2026, 3, 4, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 4, 15, 0);
        Reservation res1 = reservationService.createReservation(alice, car1, start, end);

        // ── 6. Try to book same vehicle again (should be blocked) ────────────────
        printHeader("6. Bob tries to book same Toyota Camry (should be blocked)");
        reservationService.createReservation(bob, car1, start, end);

        // ── 7. Cancel reservation → vehicle freed ────────────────────────────────
        printHeader("7. Alice cancels her reservation");
        assert res1 != null;
        reservationService.cancelReservation(res1.getReservationId());

        // ── 8. Strategy Swap — Switch to Daily Pricing ───────────────────────────
        printHeader("8. Swapping to Daily Pricing Strategy (Strategy Pattern)");
        reservationService.setPricingStrategy(new DailyPricingStrategy());

        // ── 9. Bob books the now-available car with daily pricing ─────────────────
        printHeader("9. Bob books Toyota Camry for 26 hours (Daily Pricing)");
        LocalDateTime start2 = LocalDateTime.of(2026, 3, 5, 9, 0);
        LocalDateTime end2 = LocalDateTime.of(2026, 3, 6, 11, 0); // 26 hours → 2 days
        Reservation res2 = reservationService.createReservation(bob, car1, start2, end2);

        // ── 10. Complete reservation ─────────────────────────────────────────────
        printHeader("10. Completing Bob's reservation");
        assert res2 != null;
        reservationService.completeReservation(res2.getReservationId());

        // ── 11. Car is available again ───────────────────────────────────────────
        printHeader("11. Checking car availability after completion");
        List<Vehicle> afterComplete = vehicleService.searchAvailableVehicles(VehicleType.CAR);
        afterComplete.forEach(v -> System.out.println("     → " + v));

        printHeader("Demo Complete!");
    }

    private static void printHeader(String title) {
        System.out.println("\n╔══ " + title + " ══");
    }
}