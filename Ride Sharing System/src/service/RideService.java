package service;

import enums.RideStatus;
import enums.VehicleType;
import models.Driver;
import models.Location;
import models.Ride;
import models.Rider;
import observer.DriverNotificationObserver;
import observer.RiderNotificationObserver;
import repository.DriverRepository;
import repository.RideRepository;
import strategy.DriverMatchingStrategy;
import strategy.PricingStrategy;
import java.util.List;
import java.util.UUID;

/**
 * Core orchestrator service — coordinates driver matching, fare calculation,
 * and the full ride lifecycle state machine.
 *
 * KEY DESIGN PATTERNS USED:
 * - Strategy: swappable PricingStrategy and DriverMatchingStrategy
 * - Observer: RiderNotificationObserver, DriverNotificationObserver
 * - Repository: RideRepository, DriverRepository for persistence abstraction
 */
public class RideService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private PricingStrategy pricingStrategy;
    private DriverMatchingStrategy matchingStrategy;

    public RideService(RideRepository rideRepository,
            DriverRepository driverRepository,
            PricingStrategy pricingStrategy,
            DriverMatchingStrategy matchingStrategy) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.pricingStrategy = pricingStrategy;
        this.matchingStrategy = matchingStrategy;
    }

    // ── Strategy Setters (Runtime swap) ──────────────────────────────────────
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
        System.out.println("[RideService] Pricing strategy switched to: "
                + strategy.getClass().getSimpleName());
    }

    public void setMatchingStrategy(DriverMatchingStrategy strategy) {
        this.matchingStrategy = strategy;
        System.out.println("[RideService] Matching strategy switched to: "
                + strategy.getClass().getSimpleName());
    }

    // ── Ride Request ─────────────────────────────────────────────────────────

    /**
     * Rider requests a ride — matches a driver and calculates fare.
     * 
     * @return created Ride if a driver was found, null otherwise
     */
    public Ride requestRide(Rider rider, Location pickup,
            Location dropoff, VehicleType vehicleType) {
        System.out.printf("%n[RideService] %s requesting %s ride...%n",
                rider.getName(), vehicleType);

        if (rider.hasActiveRide()) {
            System.out.println("[RideService] Rejected: Rider already has an active ride.");
            return null;
        }

        List<Driver> availableDrivers = driverRepository.findAvailableDrivers();
        Driver matchedDriver = matchingStrategy.matchDriver(pickup, availableDrivers, vehicleType);

        if (matchedDriver == null) {
            System.out.println("[RideService] No available driver found for: " + vehicleType);
            return null;
        }

        // Calculate fare
        double fare = pricingStrategy.calculateFare(pickup, dropoff, vehicleType);

        // Create Ride
        String rideId = UUID.randomUUID().toString().substring(0, 8);
        Ride ride = new Ride(rideId, rider, pickup, dropoff, vehicleType);
        ride.setDriver(matchedDriver);
        ride.setFare(fare);

        // Attach observers
        ride.addObserver(new RiderNotificationObserver());
        ride.addObserver(new DriverNotificationObserver());

        // Transition state
        ride.updateStatus(RideStatus.DRIVER_ASSIGNED);

        // Persist
        rideRepository.save(ride);

        // Mark entities as busy
        matchedDriver.setAvailable(false);
        rider.setActiveRide(true);

        System.out.printf("[RideService] Ride[%s] created | Driver: %s | Fare: ₹%.2f%n",
                ride.getId(), matchedDriver.getName(), fare);
        return ride;
    }

    // ── State Machine Transitions ─────────────────────────────────────────────

    public void driverEnRoute(String rideId) {
        Ride ride = getRide(rideId);
        assertStatus(ride, RideStatus.DRIVER_ASSIGNED);
        ride.updateStatus(RideStatus.DRIVER_EN_ROUTE);
    }

    public void startRide(String rideId) {
        Ride ride = getRide(rideId);
        assertStatus(ride, RideStatus.DRIVER_EN_ROUTE);
        ride.updateStatus(RideStatus.RIDE_STARTED);
        System.out.printf("[RideService] Ride[%s] started.%n", rideId);
    }

    public void completeRide(String rideId) {
        Ride ride = getRide(rideId);
        assertStatus(ride, RideStatus.RIDE_STARTED);
        ride.updateStatus(RideStatus.COMPLETED);

        // Release driver and rider
        ride.getDriver().setAvailable(true);
        ride.getRider().setActiveRide(false);

        System.out.printf("[RideService] Ride[%s] completed. Fare: ₹%.2f%n",
                rideId, ride.getFare());
    }

    public void cancelRide(String rideId) {
        Ride ride = getRide(rideId);
        if (ride.getStatus() == RideStatus.RIDE_STARTED || ride.getStatus() == RideStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel: ride is already " + ride.getStatus());
        }

        if (ride.getDriver() != null) {
            ride.getDriver().setAvailable(true);
        }
        ride.getRider().setActiveRide(false);
        ride.updateStatus(RideStatus.CANCELLED);

        System.out.printf("[RideService] Ride[%s] cancelled.%n", rideId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public Ride getRide(String rideId) {
        Ride ride = rideRepository.findById(rideId);
        if (ride == null)
            throw new IllegalArgumentException("Ride not found: " + rideId);
        return ride;
    }

    private void assertStatus(Ride ride, RideStatus expected) {
        if (ride.getStatus() != expected) {
            throw new IllegalStateException(
                    String.format("Invalid transition: expected %s but got %s", expected, ride.getStatus()));
        }
    }
}
