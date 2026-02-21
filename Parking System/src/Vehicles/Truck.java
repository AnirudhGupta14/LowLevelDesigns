package Vehicles;

class Truck extends Vehicle {
    public Truck(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        this.type = ParkingLotEnums.VehicleType.TRUCK;
    }
}