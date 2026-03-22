package enums;

/**
 * Represents the lifecycle states of a ride.
 */
public enum RideStatus {
    REQUESTED,       // Rider has requested a ride
    DRIVER_ASSIGNED, // A driver has been matched
    DRIVER_EN_ROUTE, // Driver is heading to pickup location
    RIDE_STARTED,    // Ride has begun (driver picked up rider)
    COMPLETED,       // Ride successfully finished
    CANCELLED        // Ride was cancelled by rider or driver
}
