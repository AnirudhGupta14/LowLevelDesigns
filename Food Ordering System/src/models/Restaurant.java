package models;

import enums.FoodCategory;
import enums.FoodStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Restaurant {
    private final String restaurantId;
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final List<String> cuisineTypes;
    private final Map<String, Food> menu;
    private final Map<String, Order> orders;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final double deliveryRadius; // in kilometers
    private final double deliveryFee;
    private final int estimatedDeliveryTime; // in minutes
    private boolean isOpen;
    private double rating;
    private int totalReviews;

    public Restaurant(String name, String address, String phoneNumber, String email, 
                     List<String> cuisineTypes, LocalTime openingTime, LocalTime closingTime,
                     double deliveryRadius, double deliveryFee, int estimatedDeliveryTime) {
        this.restaurantId = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.cuisineTypes = new ArrayList<>(cuisineTypes);
        this.menu = new HashMap<>();
        this.orders = new HashMap<>();
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.deliveryRadius = deliveryRadius;
        this.deliveryFee = deliveryFee;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.isOpen = false;
        this.rating = 0.0;
        this.totalReviews = 0;
    }

    public void addFoodToMenu(Food food) {
        if (food != null) {
            menu.put(food.getFoodId(), food);
        }
    }

    public void removeFoodFromMenu(String foodId) {
        menu.remove(foodId);
    }

    public Food getFoodById(String foodId) {
        return menu.get(foodId);
    }

    public List<Food> getAvailableFoods() {
        return menu.values().stream()
                .filter(Food::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Food> getFoodsByCategory(FoodCategory category) {
        return menu.values().stream()
                .filter(food -> food.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Food> getFoodsByCuisine(String cuisineType) {
        return menu.values().stream()
                .filter(food -> cuisineTypes.contains(cuisineType))
                .collect(Collectors.toList());
    }

    public List<Food> searchFoodByName(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return menu.values().stream()
                .filter(food -> food.getName().toLowerCase().contains(lowerSearchTerm) ||
                               food.getDescription().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    public void updateFoodStatus(String foodId, FoodStatus status) {
        Food food = menu.get(foodId);
        if (food != null) {
            food.setStatus(status);
        }
    }

    public void updateFoodQuantity(String foodId, int quantity) {
        Food food = menu.get(foodId);
        if (food != null) {
            food.setAvailableQuantity(quantity);
        }
    }

    public boolean isCurrentlyOpen() {
        LocalTime now = LocalTime.now();
        return isOpen && !now.isBefore(openingTime) && !now.isAfter(closingTime);
    }

    public void openRestaurant() {
        this.isOpen = true;
    }

    public void closeRestaurant() {
        this.isOpen = false;
    }

    public boolean canDeliverTo(String customerAddress) {
        // This is a simplified check - in a real system, you'd calculate actual distance
        return isCurrentlyOpen() && isOpen;
    }

    public double calculateDeliveryFee(String customerAddress) {
        if (canDeliverTo(customerAddress)) {
            return deliveryFee;
        }
        return -1; // Cannot deliver
    }

    public int getEstimatedDeliveryTime(String customerAddress) {
        if (canDeliverTo(customerAddress)) {
            return estimatedDeliveryTime;
        }
        return -1; // Cannot deliver
    }

    public void updateRating(double newRating) {
        if (newRating >= 0 && newRating <= 5) {
            totalReviews++;
            rating = ((rating * (totalReviews - 1)) + newRating) / totalReviews;
        }
    }

    public boolean hasFoodAvailable(String foodId, int quantity) {
        Food food = menu.get(foodId);
        return food != null && food.isAvailable() && food.getAvailableQuantity() >= quantity;
    }

    public void reserveFood(String foodId, int quantity) {
        Food food = menu.get(foodId);
        if (food != null && hasFoodAvailable(foodId, quantity)) {
            food.reduceQuantity(quantity);
        }
    }

    public void releaseFood(String foodId, int quantity) {
        Food food = menu.get(foodId);
        if (food != null) {
            food.addQuantity(quantity);
        }
    }

    public Map<String, Integer> getMenuSummary() {
        Map<String, Integer> summary = new HashMap<>();
        for (Food food : menu.values()) {
            summary.put(food.getName(), food.getAvailableQuantity());
        }
        return summary;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Restaurant restaurant = (Restaurant) obj;
        return restaurantId.equals(restaurant.restaurantId);
    }

    @Override
    public int hashCode() {
        return restaurantId.hashCode();
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantId='" + restaurantId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", isOpen=" + isCurrentlyOpen() +
                ", rating=" + rating +
                ", menuSize=" + menu.size() +
                '}';
    }
}
