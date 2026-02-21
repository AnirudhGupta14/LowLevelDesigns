import constants.VehicleTypes;
import models.Store;
import models.Vehicle;

public class Main {

    public static void main(String[] args) {

        Store store1 = new Store("STORE_1");
        Store store2 = new Store("STORE_2");
        Store store3 = new Store("STORE_3");

        // Vehicles for store1
        Vehicle s1v1 = new Vehicle("MH01", "Red", 400000, 2020, "Honda");
        Vehicle s1v2 = new Vehicle("MH02", "Black", 500000, 2018, "Toyota");
        Vehicle s1v3 = new Vehicle("MH03", "White", 700000, 2022, "Kia");

        store1.getVehicleStock().addVehicle(VehicleTypes.ECONOMY, s1v1);
        store1.getVehicleStock().addVehicle(VehicleTypes.LUXURY, s1v2);
        store1.getVehicleStock().addVehicle(VehicleTypes.SUV, s1v3);

        // Vehicles for store2
        Vehicle s2v1 = new Vehicle("DL01", "Blue", 600000, 2021, "Hyundai");
        Vehicle s2v2 = new Vehicle("DL02", "Silver", 900000, 2019, "Skoda");
        Vehicle s2v3 = new Vehicle("DL03", "Black", 1200000, 2020, "BMW");

        store2.getVehicleStock().addVehicle(VehicleTypes.ECONOMY, s2v1);
        store2.getVehicleStock().addVehicle(VehicleTypes.LUXURY, s2v2);
        store2.getVehicleStock().addVehicle(VehicleTypes.SUV, s2v3);

        // Vehicles for store3
        Vehicle s3v1 = new Vehicle("KA01", "Green", 350000, 2017, "Tata");
        Vehicle s3v2 = new Vehicle("KA02", "Grey", 800000, 2023, "Mercedes");
        Vehicle s3v3 = new Vehicle("KA03", "White", 1000000, 2022, "Audi");

        store3.getVehicleStock().addVehicle(VehicleTypes.ECONOMY, s3v1);
        store3.getVehicleStock().addVehicle(VehicleTypes.LUXURY, s3v2);
        store3.getVehicleStock().addVehicle(VehicleTypes.SUV, s3v3);

        System.out.println("Vehicles have been successfully added to all stores.");
    }


}