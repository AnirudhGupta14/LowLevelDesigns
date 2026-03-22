package repository;

import enums.RideStatus;
import models.Ride;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory repository for Ride entities.
 * In production: replaced with a DB-backed JPA repository.
 */
public class RideRepository {
    private final Map<String, Ride> store = new HashMap<>();

    public void save(Ride ride) {
        store.put(ride.getId(), ride);
    }

    public Ride findById(String id) {
        return store.get(id);
    }

    public Collection<Ride> findAll() {
        return store.values();
    }

    public List<Ride> findByRiderId(String riderId) {
        return store.values().stream()
                .filter(r -> r.getRider().getId().equals(riderId))
                .collect(Collectors.toList());
    }

    public List<Ride> findByDriverId(String driverId) {
        return store.values().stream()
                .filter(r -> r.getDriver() != null
                        && r.getDriver().getId().equals(driverId))
                .collect(Collectors.toList());
    }

    public List<Ride> findByStatus(RideStatus status) {
        return store.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
}
