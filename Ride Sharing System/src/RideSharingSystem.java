import enums.VehicleType;
import models.*;
import repository.*;
import service.*;
import strategy.*;

/**
 * Singleton Facade for the entire Ride Sharing Platform.
 *
 * Wires all services together and exposes a clean top-level API.
 * In a Spring Boot app this would be replaced by DI container wiring;
 * here it's the manual composition root.
 *
 * PATTERN: Singleton + Facade
 */
public class RideSharingSystem {

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile RideSharingSystem instance;

    // ── Repositories (data access layer) ─────────────────────────────────────
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    // ── Services (business logic layer) ──────────────────────────────────────
    public final RiderService riderService;
    public final DriverService driverService;
    public final RideService rideService;
    public final PaymentService paymentService;
    public final NotificationService notificationService;

    // ── Private constructor (Singleton) ──────────────────────────────────────
    private RideSharingSystem() {
        // Repositories
        this.riderRepository = new RiderRepository();
        this.driverRepository = new DriverRepository();
        this.rideRepository = new RideRepository();

        // Default strategies (swappable at runtime)
        PricingStrategy pricingStrategy = new BasePricingStrategy();
        DriverMatchingStrategy matchingStrategy = new NearestDriverStrategy();

        // Services
        this.riderService = new RiderService(riderRepository);
        this.driverService = new DriverService(driverRepository);
        this.rideService = new RideService(rideRepository, driverRepository,
                pricingStrategy, matchingStrategy);
        this.paymentService = new PaymentService();
        this.notificationService = new NotificationService();

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   🚗  Ride Sharing System Initialized   ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /** Thread-safe double-checked locking singleton */
    public static RideSharingSystem getInstance() {
        if (instance == null) {
            synchronized (RideSharingSystem.class) {
                if (instance == null) {
                    instance = new RideSharingSystem();
                }
            }
        }
        return instance;
    }

    // ── Convenience API (Facade) ──────────────────────────────────────────────

    public void registerRider(Rider rider) {
        riderService.registerRider(rider);
    }

    public void registerDriver(Driver driver) {
        driverService.registerDriver(driver);
    }

    public Ride bookRide(Rider rider, Location pickup, Location dropoff, VehicleType type) {
        return rideService.requestRide(rider, pickup, dropoff, type);
    }

    /** Switches pricing strategy at runtime (e.g., enable surge pricing) */
    public void activateSurgePricing(double multiplier) {
        rideService.setPricingStrategy(
                new SurgePricingStrategy(new BasePricingStrategy(), multiplier));
    }

    public void deactivateSurgePricing() {
        rideService.setPricingStrategy(new BasePricingStrategy());
    }

    /** Switches driver matching algorithm at runtime */
    public void useHighestRatedMatching() {
        rideService.setMatchingStrategy(new HighestRatedDriverStrategy());
    }

    public void useNearestMatching() {
        rideService.setMatchingStrategy(new NearestDriverStrategy());
    }
}
