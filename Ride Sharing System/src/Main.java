import enums.PaymentMethod;
import enums.VehicleType;
import models.*;

/**
 * Demo runner showcasing the full ride lifecycle.
 * Covers: registration, normal ride, surge pricing, cancellation, and ratings.
 */
public class Main {

    public static void main(String[] args) {
        RideSharingSystem system = RideSharingSystem.getInstance();

        // ═══════════════════════════════════════════════════════════════════
        // 1. REGISTER RIDERS
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("\n══════ REGISTERING RIDERS ══════");
        Rider alice = new Rider("Alice", "alice@email.com", "+91-9000000001");
        Rider bob = new Rider("Bob", "bob@email.com", "+91-9000000002");
        system.registerRider(alice);
        system.registerRider(bob);

        // ═══════════════════════════════════════════════════════════════════
        // 2. REGISTER DRIVERS
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("\n══════ REGISTERING DRIVERS ══════");
        Vehicle sedanV = new Vehicle("V1", "Maruti", "Swift Dzire", "KA01AB1234", VehicleType.SEDAN);
        Vehicle suvV = new Vehicle("V2", "Toyota", "Innova", "KA02CD5678", VehicleType.SUV);
        Vehicle bikeV = new Vehicle("V3", "Honda", "Activa", "KA03EF9012", VehicleType.BIKE);

        Driver ravi = new Driver("Ravi", "ravi@email.com", "+91-9100000001", sedanV);
        Driver priya = new Driver("Priya", "priya@email.com", "+91-9100000002", suvV);
        Driver suresh = new Driver("Suresh", "suresh@email.com", "+91-9100000003", bikeV);

        system.registerDriver(ravi);
        system.registerDriver(priya);
        system.registerDriver(suresh);

        // Set driver locations (Bangalore coords)
        system.driverService.updateLocation(ravi.getId(), new Location(12.9352, 77.6245)); // Koramangala
        system.driverService.updateLocation(priya.getId(), new Location(12.9279, 77.6271)); // BTM Layout
        system.driverService.updateLocation(suresh.getId(), new Location(12.9350, 77.6140)); // JP Nagar

        // ═══════════════════════════════════════════════════════════════════
        // 3. ALICE BOOKS A SEDAN RIDE (Nearest Driver Strategy)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("\n══════ ALICE BOOKS A SEDAN RIDE ══════");
        Location alicePickup = new Location(12.9340, 77.6200); // Koramangala pickup
        Location aliceDropoff = new Location(12.9716, 77.5946); // MG Road dropoff

        Ride ride1 = system.bookRide(alice, alicePickup, aliceDropoff, VehicleType.SEDAN);

        if (ride1 != null) {
            // Driver heads to pickup
            system.rideService.driverEnRoute(ride1.getId());

            // Driver picks up Alice
            system.rideService.startRide(ride1.getId());

            // Complete ride and process payment
            system.rideService.completeRide(ride1.getId());
            system.paymentService.processPayment(ride1, PaymentMethod.UPI);
            system.notificationService.sendRideReceipt(ride1);

            // Post-ride ratings
            system.driverService.updateRating(ride1.getDriver().getId(), 4.8);
            system.riderService.updateRating(alice.getId(), 5.0);
        }

        // ═══════════════════════════════════════════════════════════════════
        // 4. BOB BOOKS WITH SURGE PRICING (1.8x)
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("\n══════ BOB BOOKS DURING SURGE (1.8x) ══════");
        system.activateSurgePricing(1.8);

        Location bobPickup = new Location(12.9279, 77.6271); // BTM Layout
        Location bobDropoff = new Location(12.9634, 77.5855); // Cubbon Park

        Ride ride2 = system.bookRide(bob, bobPickup, bobDropoff, VehicleType.SUV);

        if (ride2 != null) {
            system.rideService.driverEnRoute(ride2.getId());
            system.rideService.startRide(ride2.getId());
            system.rideService.completeRide(ride2.getId());
            system.paymentService.processPayment(ride2, PaymentMethod.CREDIT_CARD);
            system.notificationService.sendRideReceipt(ride2);
            system.driverService.updateRating(ride2.getDriver().getId(), 4.5);
        }
        system.deactivateSurgePricing();

        // ═══════════════════════════════════════════════════════════════════
        // 5. CANCELLATION DEMO — Alice books and cancels a bike ride
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("\n══════ ALICE BOOKS & CANCELS A BIKE RIDE ══════");
        system.useHighestRatedMatching(); // Switch to highest-rated strategy

        Location alicePickup2 = new Location(12.9350, 77.6140);
        Location aliceDropoff2 = new Location(12.9200, 77.6100);

        Ride ride3 = system.bookRide(alice, alicePickup2, aliceDropoff2, VehicleType.BIKE);

        if (ride3 != null) {
            System.out.println("[Main] Alice decides to cancel the ride...");
            system.rideService.cancelRide(ride3.getId());
            System.out.println("[Main] Ride cancelled. Suresh is available again: "
                    + suresh.isAvailable());
        }

        // ═══════════════════════════════════════════════════════════════════
        // 6. NO DRIVER AVAILABLE DEMO
        // ═══════════════════════════════════════════════════════════════════
        System.out.println("\n══════ NO DRIVER SCENARIO (All Sedans busy) ══════");
        system.useNearestMatching();
        // Ravi (only SEDAN driver) is available again after ride 1 completed
        // Let's manually set him as unavailable to simulate
        system.driverService.setAvailability(ravi.getId(), false);
        Ride ride4 = system.bookRide(bob, alicePickup, aliceDropoff, VehicleType.SEDAN);
        System.out.println("[Main] Ride4 result (expected null): " + ride4);

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║        ✅ Demo Complete                  ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
