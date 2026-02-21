package services;

import models.Restaurant;
import models.Food;
import enums.FoodStatus;
import enums.FoodCategory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RestaurantService {
    private final Map<String, Restaurant> restaurants;
    private final Map<String, List<Restaurant>> restaurantsByCuisine;
    private final Map<String, List<Restaurant>> restaurantsByArea;

    public RestaurantService() {
        this.restaurants = new ConcurrentHashMap<>();
        this.restaurantsByCuisine = new ConcurrentHashMap<>();
        this.restaurantsByArea = new ConcurrentHashMap<>();
    }

    // Restaurant Management
    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getRestaurantId(), restaurant);
        updateCuisineIndex(restaurant);
        updateAreaIndex(restaurant);
    }

    public void removeRestaurant(String restaurantId) {
        Restaurant restaurant = restaurants.remove(restaurantId);
        if (restaurant != null) {
            removeFromCuisineIndex(restaurant);
            removeFromAreaIndex(restaurant);
        }
    }

    public Restaurant getRestaurant(String restaurantId) {
        return restaurants.get(restaurantId);
    }

    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants.values());
    }

    public List<Restaurant> getAvailableRestaurants() {
        return restaurants.values().stream()
                .filter(Restaurant::isCurrentlyOpen)
                .collect(Collectors.toList());
    }

    public List<Restaurant> getRestaurantsByCuisine(String cuisineType) {
        return restaurantsByCuisine.getOrDefault(cuisineType, new ArrayList<>())
                .stream()
                .filter(Restaurant::isCurrentlyOpen)
                .collect(Collectors.toList());
    }

    public List<Restaurant> getRestaurantsByArea(String area) {
        return restaurantsByArea.getOrDefault(area, new ArrayList<>())
                .stream()
                .filter(Restaurant::isCurrentlyOpen)
                .collect(Collectors.toList());
    }

    public List<Restaurant> searchRestaurants(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return restaurants.values().stream()
                .filter(restaurant -> restaurant.getName().toLowerCase().contains(lowerSearchTerm) ||
                                     restaurant.getAddress().toLowerCase().contains(lowerSearchTerm) ||
                                     restaurant.getCuisineTypes().stream()
                                             .anyMatch(cuisine -> cuisine.toLowerCase().contains(lowerSearchTerm)))
                .filter(Restaurant::isCurrentlyOpen)
                .collect(Collectors.toList());
    }

    public List<Restaurant> getTopRatedRestaurants(int limit) {
        return restaurants.values().stream()
                .filter(Restaurant::isCurrentlyOpen)
                .sorted((r1, r2) -> Double.compare(r2.getRating(), r1.getRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Restaurant> getRestaurantsByRating(double minRating) {
        return restaurants.values().stream()
                .filter(restaurant -> restaurant.getRating() >= minRating)
                .filter(Restaurant::isCurrentlyOpen)
                .sorted((r1, r2) -> Double.compare(r2.getRating(), r1.getRating()))
                .collect(Collectors.toList());
    }

    // Restaurant Status Management
    public void openRestaurant(String restaurantId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.openRestaurant();
        }
    }

    public void closeRestaurant(String restaurantId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.closeRestaurant();
        }
    }

    public boolean isRestaurantOpen(String restaurantId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null && restaurant.isCurrentlyOpen();
    }

    // Food Management
    public boolean addFoodToRestaurant(String restaurantId, Food food) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.addFoodToMenu(food);
            return true;
        }
        return false;
    }

    public boolean removeFoodFromRestaurant(String restaurantId, String foodId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.removeFoodFromMenu(foodId);
            return true;
        }
        return false;
    }

    public Food getFoodFromRestaurant(String restaurantId, String foodId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.getFoodById(foodId) : null;
    }

    public List<Food> getAvailableFoodsFromRestaurant(String restaurantId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.getAvailableFoods() : new ArrayList<>();
    }

    public List<Food> getFoodsByCategoryFromRestaurant(String restaurantId, FoodCategory category) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.getFoodsByCategory(category) : new ArrayList<>();
    }

    public List<Food> searchFoodInRestaurant(String restaurantId, String searchTerm) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.searchFoodByName(searchTerm) : new ArrayList<>();
    }

    public boolean updateFoodStatus(String restaurantId, String foodId, FoodStatus status) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.updateFoodStatus(foodId, status);
            return true;
        }
        return false;
    }

    public boolean updateFoodQuantity(String restaurantId, String foodId, int quantity) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.updateFoodQuantity(foodId, quantity);
            return true;
        }
        return false;
    }

    // Delivery Management
    public boolean canDeliverTo(String restaurantId, String deliveryAddress) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null && restaurant.canDeliverTo(deliveryAddress);
    }

    public double calculateDeliveryFee(String restaurantId, String deliveryAddress) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.calculateDeliveryFee(deliveryAddress) : -1;
    }

    public int getEstimatedDeliveryTime(String restaurantId, String deliveryAddress) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.getEstimatedDeliveryTime(deliveryAddress) : -1;
    }

    // Rating Management
    public void updateRestaurantRating(String restaurantId, double newRating) {
        Restaurant restaurant = restaurants.get(restaurantId);
        if (restaurant != null) {
            restaurant.updateRating(newRating);
        }
    }

    public double getRestaurantRating(String restaurantId) {
        Restaurant restaurant = restaurants.get(restaurantId);
        return restaurant != null ? restaurant.getRating() : 0.0;
    }

    // Statistics and Analytics
    public Map<String, Object> getRestaurantStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRestaurants", restaurants.size());
        stats.put("openRestaurants", getAvailableRestaurants().size());
        stats.put("closedRestaurants", restaurants.size() - getAvailableRestaurants().size());
        
        // Calculate average rating
        double avgRating = restaurants.values().stream()
                .mapToDouble(Restaurant::getRating)
                .average()
                .orElse(0.0);
        stats.put("averageRating", avgRating);
        
        // Count restaurants by cuisine
        Map<String, Integer> cuisineCount = new HashMap<>();
        for (Restaurant restaurant : restaurants.values()) {
            for (String cuisine : restaurant.getCuisineTypes()) {
                cuisineCount.put(cuisine, cuisineCount.getOrDefault(cuisine, 0) + 1);
            }
        }
        stats.put("cuisineDistribution", cuisineCount);
        
        return stats;
    }

    public Map<String, Integer> getCuisineStatistics() {
        Map<String, Integer> cuisineStats = new HashMap<>();
        for (Restaurant restaurant : restaurants.values()) {
            for (String cuisine : restaurant.getCuisineTypes()) {
                cuisineStats.put(cuisine, cuisineStats.getOrDefault(cuisine, 0) + 1);
            }
        }
        return cuisineStats;
    }

    public List<Restaurant> getRestaurantsWithMostFoodItems(int limit) {
        return restaurants.values().stream()
                .filter(Restaurant::isCurrentlyOpen)
                .sorted((r1, r2) -> Integer.compare(r2.getMenu().size(), r1.getMenu().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Utility Methods
    private void updateCuisineIndex(Restaurant restaurant) {
        for (String cuisine : restaurant.getCuisineTypes()) {
            restaurantsByCuisine.computeIfAbsent(cuisine, k -> new ArrayList<>()).add(restaurant);
        }
    }

    private void removeFromCuisineIndex(Restaurant restaurant) {
        for (String cuisine : restaurant.getCuisineTypes()) {
            List<Restaurant> cuisineRestaurants = restaurantsByCuisine.get(cuisine);
            if (cuisineRestaurants != null) {
                cuisineRestaurants.remove(restaurant);
                if (cuisineRestaurants.isEmpty()) {
                    restaurantsByCuisine.remove(cuisine);
                }
            }
        }
    }

    private void updateAreaIndex(Restaurant restaurant) {
        // This is a simplified area indexing - in real implementation, you'd use proper geocoding
        String area = extractAreaFromAddress(restaurant.getAddress());
        if (area != null) {
            restaurantsByArea.computeIfAbsent(area, k -> new ArrayList<>()).add(restaurant);
        }
    }

    private void removeFromAreaIndex(Restaurant restaurant) {
        String area = extractAreaFromAddress(restaurant.getAddress());
        if (area != null) {
            List<Restaurant> areaRestaurants = restaurantsByArea.get(area);
            if (areaRestaurants != null) {
                areaRestaurants.remove(restaurant);
                if (areaRestaurants.isEmpty()) {
                    restaurantsByArea.remove(area);
                }
            }
        }
    }

    private String extractAreaFromAddress(String address) {
        // Simplified area extraction - in real implementation, use proper address parsing
        if (address == null || address.isEmpty()) {
            return null;
        }
        
        String[] parts = address.split(",");
        if (parts.length > 1) {
            return parts[parts.length - 1].trim();
        }
        return address.trim();
    }

    public Set<String> getAllCuisineTypes() {
        return restaurantsByCuisine.keySet();
    }

    public Set<String> getAllAreas() {
        return restaurantsByArea.keySet();
    }

    public boolean restaurantExists(String restaurantId) {
        return restaurants.containsKey(restaurantId);
    }

    public int getTotalRestaurants() {
        return restaurants.size();
    }

    public int getOpenRestaurantsCount() {
        return getAvailableRestaurants().size();
    }
}

