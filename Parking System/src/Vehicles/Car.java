package Vehicles;

public class Car extends Vehicle {
    public Car(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        this.type = ParkingLotEnums.VehicleType.CAR;
    }
}