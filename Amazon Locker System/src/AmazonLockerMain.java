import entities.Customer;
import entities.Locker;
import entities.Package;
import enums.LockerSize;
import observer.EmailNotificationService;
import services.DeliveryService;
import services.LockerService;
import services.PickupService;
import strategy.SizeBasedLockerAssignmentStrategy;

public class AmazonLockerMain {
    public static void main(String[] args) {
        System.out.println("--- Bootstrapping Amazon Locker System ---");

        // 1. Setup Services & Patterns
        LockerService lockerService = LockerService.getInstance();
        lockerService.setAssignmentStrategy(new SizeBasedLockerAssignmentStrategy());

        DeliveryService deliveryService = DeliveryService.getInstance();
        deliveryService.addSubscriber(new EmailNotificationService()); // Observer pattern

        PickupService pickupService = PickupService.getInstance();

        // 2. Add Lockers
        lockerService.addLocker(new Locker("L1", LockerSize.SMALL));
        lockerService.addLocker(new Locker("L2", LockerSize.MEDIUM));
        lockerService.addLocker(new Locker("L3", LockerSize.LARGE));
        System.out.println("Added SMALL, MEDIUM, LARGE lockers to system.\n");

        // 3. Create Customers & Packages
        Customer john = new Customer("C1", "John Doe", "john@example.com");
        Package smallPkg1 = new Package("PKG_1001", LockerSize.SMALL, john);

        Customer jane = new Customer("C2", "Jane Smith", "jane@example.com");
        Package medPkg1 = new Package("PKG_1002", LockerSize.MEDIUM, jane);

        Customer bob = new Customer("C3", "Bob Builder", "bob@example.com");
        Package smallPkg2 = new Package("PKG_1003", LockerSize.SMALL, bob);

        // 4. Test Case 1: Successful Delivery and exact match Locker Assignment
        System.out.println(">>> Test Case 1: Deliver Small Package for John");
        deliveryService.deliverPackage(smallPkg1);
        System.out.println();

        // 5. Test Case 2: Delivery finds next available bigger locker
        // Lockers available: L2(MEDIUM), L3(LARGE)
        // We deliver a small package, should be assigned to L2 since it's >= SMALL
        System.out.println(">>> Test Case 2: Deliver Small Package for Bob (uses bigger locker)");
        deliveryService.deliverPackage(smallPkg2);
        System.out.println();

        // 6. Test Case 3: Delivery when exact size unavailable but larger size
        // available
        // Lockers available: L3(LARGE). Deliver MEDIUM package -> uses L3
        System.out.println(">>> Test Case 3: Deliver Medium Package for Jane");
        deliveryService.deliverPackage(medPkg1);
        System.out.println();

        // 7. Test Case 4: No locker available
        System.out.println(">>> Test Case 4: Deliver Package when system is full");
        Package largePkg = new Package("PKG_1004", LockerSize.LARGE, john);
        deliveryService.deliverPackage(largePkg);
        System.out.println();

        // Let's get the OTP for john's package manually from the locker object to
        // simulate pickup.
        // In reality, John gets it from email. We know it went to L1.
        Locker l1 = lockerService.getLockerById("L1");
        String correctOtpForL1 = l1.getOtp();

        // 8. Test Case 5: Failed Pickup (Wrong OTP)
        System.out.println(">>> Test Case 5: Pickup with Wrong OTP");
        pickupService.pickupPackage("L1", "000000");
        System.out.println();

        // 9. Test Case 6: Successful Pickup
        System.out.println(">>> Test Case 6: Pickup with Correct OTP");
        pickupService.pickupPackage("L1", correctOtpForL1);
        System.out.println();

        // 10. Test Case 7: Deliver Large package now that L1 is free
        // Package needs LARGE, L1 is SMALL. Should fail.
        System.out.println(">>> Test Case 7: System has free SMALL, but package needs LARGE");
        deliveryService.deliverPackage(largePkg);
        System.out.println("\n--- Demo Concluded ---");
    }
}
