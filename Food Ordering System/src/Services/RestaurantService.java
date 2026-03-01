package Services;

import entities.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantService {

    // ─── Singleton ────────────────────────────────────────────

    private static RestaurantService instance;

    private final Map<String, Restaurant> restaurants;

    private RestaurantService() {
        this.restaurants = new HashMap<>();
    }

    public static synchronized RestaurantService getInstance() {
        if (instance == null) {
            instance = new RestaurantService();
        }
        return instance;
    }

    // ─── Restaurant Management ────────────────────────────────

    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        System.out.printf("  🏪 Registered restaurant: %s (%s)%n", restaurant.getName(), restaurant.getAddress());
    }

    public Restaurant getRestaurant(String id) {
        return restaurants.get(id);
    }

    public List<Restaurant> searchByName(String keyword) {
        return restaurants.values().stream()
                .filter(r -> r.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants.values());
    }

    // ─── Display ──────────────────────────────────────────────

    public void displayAllRestaurants() {
        System.out.println("  🏪 Registered Restaurants:");
        System.out.println("  ─────────────────────────────────────────────");
        for (Restaurant r : restaurants.values()) {
            System.out.printf("    %s%n", r);
        }
        System.out.println("  ─────────────────────────────────────────────\n");
    }
}
