package repository;

import models.Driver;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory repository for Driver entities.
 * In production: replaced with a DB-backed JPA repository.
 */
public class DriverRepository {
    private final Map<String, Driver> store = new HashMap<>();

    public void save(Driver driver) {
        store.put(driver.getId(), driver);
    }

    public Driver findById(String id) {
        return store.get(id);
    }

    public Collection<Driver> findAll() {
        return store.values();
    }

    public List<Driver> findAvailableDrivers() {
        return store.values().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean exists(String id) {
        return store.containsKey(id);
    }

    public void delete(String id) {
        store.remove(id);
    }
}
