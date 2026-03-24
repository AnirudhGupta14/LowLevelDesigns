package services;

import entities.Locker;
import enums.LockerState;
import enums.PackageStatus;

public class PickupService {
    private static PickupService instance;
    private LockerService lockerService;

    private PickupService() {
        this.lockerService = LockerService.getInstance();
    }

    public static synchronized PickupService getInstance() {
        if (instance == null) {
            instance = new PickupService();
        }
        return instance;
    }

    public void pickupPackage(String lockerId, String otp) {
        Locker locker = lockerService.getLockerById(lockerId);

        if (locker == null) {
            System.out.println("Pickup failed. Locker " + lockerId + " not found.");
            return;
        }

        if (locker.getState() != LockerState.BOOKED || locker.getCurrentPackage() == null) {
            System.out.println("Pickup failed. Locker " + lockerId + " is empty or not booked.");
            return;
        }

        if (!locker.getOtp().equals(otp)) {
            System.out.println("Pickup failed. Invalid OTP for Locker " + lockerId + ".");
            return;
        }

        // Successfully verified
        entities.Package pkg = locker.getCurrentPackage();
        pkg.setStatus(PackageStatus.PICKED_UP);
        locker.removePackage();
        System.out.println("Pickup successful! Package " + pkg.getId() + " retrieved from Locker " + lockerId + ".");
    }
}
