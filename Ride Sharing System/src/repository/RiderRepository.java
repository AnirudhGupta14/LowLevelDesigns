package repository;

import models.Rider;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory repository for Rider entities.
 * In production: replaced with a DB-backed JPA repository.
 */
public class RiderRepository {
    private final Map<String, Rider> store = new HashMap<>();

    public void save(Rider rider) {
        store.put(rider.getId(), rider);
    }

    public Rider findById(String id) {
        return store.get(id);
    }

    public Collection<Rider> findAll() {
        return store.values();
    }

    public boolean exists(String id) {
        return store.containsKey(id);
    }

    public void delete(String id) {
        store.remove(id);
    }
}
