package entities;

import enums.LockerSize;
import enums.PackageStatus;

public class Package {
    private String id;
    private LockerSize size;
    private PackageStatus status;
    private Customer customer;

    public Package(String id, LockerSize size, Customer customer) {
        this.id = id;
        this.size = size;
        this.customer = customer;
        this.status = PackageStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public LockerSize getSize() {
        return size;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }
}
