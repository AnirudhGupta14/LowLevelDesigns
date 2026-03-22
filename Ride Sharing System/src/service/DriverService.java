package service;

import models.Driver;
import models.Location;
import repository.DriverRepository;
import java.util.List;

/**
 * Service handling driver-related business logic:
 * registration, location updates, availability toggling.
 */
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public void registerDriver(Driver driver) {
        driverRepository.save(driver);
        System.out.printf("[DriverService] Registered: %s%n", driver);
    }

    public void updateLocation(String driverId, Location newLocation) {
        Driver driver = driverRepository.findById(driverId);
        if (driver == null)
            throw new IllegalArgumentException("Driver not found: " + driverId);
        driver.setCurrentLocation(newLocation);
        System.out.printf("[DriverService] Location updated for %s → %s%n",
                driver.getName(), newLocation);
    }

    public void setAvailability(String driverId, boolean available) {
        Driver driver = driverRepository.findById(driverId);
        if (driver == null)
            throw new IllegalArgumentException("Driver not found: " + driverId);
        driver.setAvailable(available);
        System.out.printf("[DriverService] %s availability → %b%n", driver.getName(), available);
    }

    public Driver getDriver(String driverId) {
        return driverRepository.findById(driverId);
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    public void updateRating(String driverId, double rating) {
        Driver driver = driverRepository.findById(driverId);
        if (driver == null)
            throw new IllegalArgumentException("Driver not found: " + driverId);
        driver.updateRating(rating);
        System.out.printf("[DriverService] %s new rating: %.2f%n", driver.getName(), driver.getRating());
    }
}
