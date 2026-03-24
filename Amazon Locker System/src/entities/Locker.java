package entities;

import enums.LockerSize;
import enums.LockerState;

public class Locker {
    private String id;
    private LockerSize size;
    private LockerState state;
    private Package currentPackage;
    private String otp;

    public Locker(String id, LockerSize size) {
        this.id = id;
        this.size = size;
        this.state = LockerState.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public LockerSize getSize() {
        return size;
    }

    public LockerState getState() {
        return state;
    }

    public void setState(LockerState state) {
        this.state = state;
    }

    public Package getCurrentPackage() {
        return currentPackage;
    }

    public String getOtp() {
        return otp;
    }

    public void assignPackage(Package pkg, String otp) {
        this.currentPackage = pkg;
        this.otp = otp;
        this.state = LockerState.BOOKED;
    }

    public void removePackage() {
        this.currentPackage = null;
        this.otp = null;
        this.state = LockerState.AVAILABLE;
    }
}
