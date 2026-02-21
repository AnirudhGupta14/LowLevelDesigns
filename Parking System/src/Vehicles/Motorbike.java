package Vehicles;

class Motorbike extends Vehicle {
    public Motorbike(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        this.type = ParkingLotEnums.VehicleType.MOTORBIKE;
    }
}
