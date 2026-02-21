package observer;

import models.*;

import java.time.LocalTime;
import java.util.*;

public class ObserverExample {
    public static void main(String[] args) {
        // Create notification service
        NotificationService notificationService = new NotificationService();
        
        // Create entities
        User user = new User("John Doe", "john@example.com", "123-456-7890");
        List<String> cuisines = new ArrayList<>();
        cuisines.add("Italian");
        cuisines.add("Pizza");
        
        Set<String> areas = new HashSet<>();
        areas.add("Downtown");
        areas.add("Midtown");
        
        Restaurant restaurant = new Restaurant(
            "Pizza Palace", "123 Main St", "555-0123", "info@pizzapalace.com",
            cuisines, LocalTime.of(9, 0), LocalTime.of(22, 0),
            5.0, 2.50, 30
        );
        DeliveryRider rider = new DeliveryRider(
            "Mike Smith", "555-0456", "mike@example.com", "Bike", "BIKE123", "LIC456",
            areas, 3.0, 2
        );
        
        // Create observable entities
        ObservableUser observableUser = new ObservableUser(user);
        ObservableRestaurant observableRestaurant = new ObservableRestaurant(restaurant);
        ObservableRider observableRider = new ObservableRider(rider);
        
        // Register observers with specific entities
        observableUser.addObserver(notificationService);
        observableRestaurant.addObserver(notificationService);
        observableRider.addObserver(notificationService);
        
        // Simulate events
        System.out.println("=== Simulating User Events ===");
        observableUser.updateUser("John Smith", "johnsmith@example.com", "123-456-7891");
        
        System.out.println("\n=== Simulating Restaurant Events ===");
        observableRestaurant.openRestaurant();
        observableRestaurant.updateRating(4.5);
        
        System.out.println("\n=== Simulating Rider Events ===");
        observableRider.goOnline();
        observableRider.updateLocation("Downtown Mall");
        
        // Create an order and simulate order events
        System.out.println("\n=== Simulating Order Events ===");
        Order order = new Order(user, "456 Oak Ave");
        ObservableOrder observableOrder = new ObservableOrder(order);
        observableOrder.addObserver(notificationService);
        
        // Add food items
        Food pizza = new Food("Margherita Pizza", "Classic tomato and mozzarella", 12.99, 
                             enums.FoodCategory.VEG, 15, 10);
        observableOrder.addFoodItem(pizza, 2);
        
        // Confirm order
        observableOrder.confirmOrder();
        
        // Complete order
        observableOrder.completeOrder();
        
        // Display notification statistics
        System.out.println("\n=== Notification Statistics ===");
        Map<String, Object> notificationStats = notificationService.getNotificationStatistics();
        System.out.println("Total Notifications: " + notificationStats.get("totalNotifications"));
        System.out.println("Entity Type Distribution: " + notificationStats.get("entityTypeDistribution"));
        System.out.println("Event Type Distribution: " + notificationStats.get("eventTypeDistribution"));
    }
}

// Observable Restaurant wrapper
class ObservableRestaurant extends ObservableEntity {
    private final Restaurant restaurant;

    public ObservableRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void openRestaurant() {
        restaurant.openRestaurant();
        notifyObservers(NotificationService.EventTypes.RESTAURANT_OPENED, null);
    }

    public void closeRestaurant() {
        restaurant.closeRestaurant();
        notifyObservers(NotificationService.EventTypes.RESTAURANT_CLOSED, null);
    }

    public void updateRating(double newRating) {
        restaurant.updateRating(newRating);
        notifyObservers(NotificationService.EventTypes.RESTAURANT_RATING_UPDATED, newRating);
    }

    @Override
    protected void notifyObserver(Observer observer, String eventType, Object data) {
        observer.update(restaurant, eventType, data);
    }
}

// Observable Rider wrapper
class ObservableRider extends ObservableEntity {
    private final DeliveryRider rider;

    public ObservableRider(DeliveryRider rider) {
        this.rider = rider;
    }

    public void goOnline() {
        rider.goOnline();
        notifyObservers(NotificationService.EventTypes.RIDER_ONLINE, null);
    }

    public void goOffline() {
        rider.goOffline();
        notifyObservers(NotificationService.EventTypes.RIDER_OFFLINE, null);
    }

    public void updateLocation(String location) {
        rider.updateLocation(location);
        notifyObservers(NotificationService.EventTypes.RIDER_LOCATION_UPDATED, location);
    }

    @Override
    protected void notifyObserver(Observer observer, String eventType, Object data) {
        observer.update(rider, eventType, data);
    }
}

// Observable Order wrapper
class ObservableOrder extends ObservableEntity {
    private final Order order;

    public ObservableOrder(Order order) {
        this.order = order;
    }

    public void addFoodItem(Food food, int quantity) {
        order.addFoodItem(food, quantity);
        Map<String, Object> data = new HashMap<>();
        data.put("food", food.getName());
        data.put("quantity", quantity);
        notifyObservers(NotificationService.EventTypes.ORDER_ITEM_ADDED, data);
    }

    public void confirmOrder() {
        order.confirmOrder();
        notifyObservers(NotificationService.EventTypes.ORDER_CONFIRMED, null);
    }

    public void completeOrder() {
        order.setDeliveryDate(java.time.LocalDateTime.now());
        notifyObservers(NotificationService.EventTypes.ORDER_COMPLETED, null);
    }

    @Override
    protected void notifyObserver(Observer observer, String eventType, Object data) {
        observer.update(order, eventType, data);
    }
}
