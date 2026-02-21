package Vehicles;

public abstract class Vehicle {
    public String licenseNumber;
    public ParkingLotEnums.VehicleType type;

    public ParkingLotEnums.ParkingSpotType getSpotTypeForVehicle(Vehicle vehicle) {
        return switch (vehicle.type) {
            case MOTORBIKE -> ParkingLotEnums.ParkingSpotType.MINI;
            case CAR -> ParkingLotEnums.ParkingSpotType.COMPACT;
            case TRUCK -> ParkingLotEnums.ParkingSpotType.LARGE;
            default -> throw new IllegalArgumentException("Unknown vehicle type!");
        };
    }
}
