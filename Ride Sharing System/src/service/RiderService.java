package service;

import models.Rider;
import repository.RiderRepository;

/**
 * Service handling rider-related business logic:
 * registration, profile management, and rating updates.
 */
public class RiderService {
    private final RiderRepository riderRepository;

    public RiderService(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public void registerRider(Rider rider) {
        riderRepository.save(rider);
        System.out.printf("[RiderService] Registered: %s%n", rider);
    }

    public Rider getRider(String riderId) {
        Rider rider = riderRepository.findById(riderId);
        if (rider == null)
            throw new IllegalArgumentException("Rider not found: " + riderId);
        return rider;
    }

    public void updateRating(String riderId, double rating) {
        Rider rider = getRider(riderId);
        rider.updateRating(rating);
        System.out.printf("[RiderService] %s new rating: %.2f%n", rider.getName(), rider.getRating());
    }

    public boolean hasActiveRide(String riderId) {
        return getRider(riderId).hasActiveRide();
    }
}
