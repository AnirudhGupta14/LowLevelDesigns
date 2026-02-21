package services;

import models.*;
import enums.OrderingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OrderingService {
    private final Map<String, Order> orders;
    private final Map<String, List<Order>> userOrders;
    private final Map<String, List<Order>> restaurantOrders;
    private final Map<String, OrderLock> activeLocks;
    private final RestaurantService restaurantService;
    private final RiderService riderService;

    public OrderingService() {
        this.orders = new ConcurrentHashMap<>();
        this.userOrders = new ConcurrentHashMap<>();
        this.restaurantOrders = new ConcurrentHashMap<>();
        this.activeLocks = new ConcurrentHashMap<>();
        this.restaurantService = new RestaurantService();
        this.riderService = new RiderService();
    }

    // Order Creation and Management
    public Order createOrder(User customer, String deliveryAddress, String restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurant(restaurantId);
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found: " + restaurantId);
        }

        if (!restaurant.isCurrentlyOpen()) {
            throw new IllegalStateException("Restaurant is currently closed");
        }

        Order order = new Order(customer, deliveryAddress);
        orders.put(order.getOrderId(), order);
        
        // Update user orders
        userOrders.computeIfAbsent(customer.getUserId(), k -> new ArrayList<>()).add(order);
        
        // Update restaurant orders
        restaurantOrders.computeIfAbsent(restaurantId, k -> new ArrayList<>()).add(order);

        return order;
    }

    public boolean addFoodToOrder(String orderId, String foodId, int quantity) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (order.getOrderingStatus() != OrderingStatus.CREATED) {
            return false; // Can only modify created orders
        }

        // Find the restaurant and food
        Restaurant restaurant = findRestaurantByOrder(order);
        if (restaurant == null) {
            return false;
        }

        Food food = restaurant.getFoodById(foodId);
        if (food == null || !food.isAvailable() || food.getAvailableQuantity() < quantity) {
            return false;
        }

        order.addFoodItem(food, quantity);
        return true;
    }

    public boolean removeFoodFromOrder(String orderId, String foodId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (order.getOrderingStatus() != OrderingStatus.CREATED) {
            return false;
        }

        Restaurant restaurant = findRestaurantByOrder(order);
        if (restaurant == null) {
            return false;
        }

        Food food = restaurant.getFoodById(foodId);
        if (food == null) {
            return false;
        }

        order.removeFoodItem(food);
        return true;
    }

    public boolean updateFoodQuantity(String orderId, String foodId, int newQuantity) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (order.getOrderingStatus() != OrderingStatus.CREATED) {
            return false;
        }

        Restaurant restaurant = findRestaurantByOrder(order);
        if (restaurant == null) {
            return false;
        }

        Food food = restaurant.getFoodById(foodId);
        if (food == null) {
            return false;
        }

        order.updateFoodQuantity(food, newQuantity);
        return true;
    }

    // Order Locking
    public OrderLock createOrderLock(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        if (activeLocks.containsKey(order.getOrderId())) {
            throw new IllegalStateException("Order is already locked");
        }

        OrderLock orderLock = new OrderLock(order, order.getCustomer());
        activeLocks.put(order.getOrderId(), orderLock);
        
        return orderLock;
    }

    public boolean lockOrder(String orderId) {
        OrderLock orderLock = activeLocks.get(orderId);
        if (orderLock == null) {
            return false;
        }

        return orderLock.acquireLock();
    }

    public boolean releaseOrderLock(String orderId) {
        OrderLock orderLock = activeLocks.get(orderId);
        if (orderLock == null) {
            return false;
        }

        orderLock.releaseLock();
        activeLocks.remove(orderId);
        return true;
    }

    public OrderLock getOrderLock(String orderId) {
        return activeLocks.get(orderId);
    }

    // Order Processing
    public boolean confirmOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (order.getOrderingStatus() != OrderingStatus.CREATED) {
            return false;
        }

        // Check if order is locked
        OrderLock orderLock = activeLocks.get(orderId);
        if (orderLock == null || !orderLock.isLocked()) {
            return false;
        }

        order.confirmOrder();
        orderLock.convertToOrder();
        
        // Assign delivery rider
//        DeliveryRider rider = findBestRiderForOrder(order);
//        if (rider != null) {
//            riderService.assignOrderToRider(rider.getRiderId(), order);
//        }

        return true;
    }

    public boolean cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (!order.canBeCancelled()) {
            return false;
        }

        // Release order lock if exists
        OrderLock orderLock = activeLocks.get(orderId);
        if (orderLock != null) {
            orderLock.releaseLock();
            activeLocks.remove(orderId);
        }

        // Remove from rider if assigned
        DeliveryRider rider = findRiderByOrder(order);
        if (rider != null) {
            riderService.cancelOrderForRider(rider.getRiderId(), order);
        }

        order.cancelOrder();
        return true;
    }

    public boolean completeOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return false;
        }

        if (order.getOrderingStatus() != OrderingStatus.CONFIRMED) {
            return false;
        }

        // Complete with rider
        DeliveryRider rider = findRiderByOrder(order);
        if (rider != null) {
            riderService.completeOrderForRider(rider.getRiderId(), order);
        }

        // Set delivery date
        order.setDeliveryDate(LocalDateTime.now());
        
        return true;
    }

    // Order Retrieval
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }

    public List<Order> getUserOrders(String userId) {
        return userOrders.getOrDefault(userId, new ArrayList<>());
    }

    public List<Order> getRestaurantOrders(String restaurantId) {
        return restaurantOrders.getOrDefault(restaurantId, new ArrayList<>());
    }

    public List<Order> getOrdersByStatus(OrderingStatus status) {
        return orders.values().stream()
                .filter(order -> order.getOrderingStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Order> getActiveOrders() {
        return orders.values().stream()
                .filter(order -> order.getOrderingStatus() == OrderingStatus.CREATED ||
                               order.getOrderingStatus() == OrderingStatus.CONFIRMED)
                .collect(Collectors.toList());
    }

    public List<Order> getRecentOrders(int limit) {
        return orders.values().stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Statistics and Analytics
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", orders.size());
        stats.put("activeOrders", getActiveOrders().size());
        stats.put("completedOrders", getOrdersByStatus(OrderingStatus.CONFIRMED).size());
        stats.put("cancelledOrders", getOrdersByStatus(OrderingStatus.CANCELLED).size());
        
        // Calculate total revenue
        double totalRevenue = orders.values().stream()
                .filter(order -> order.getOrderingStatus() == OrderingStatus.CONFIRMED)
                .mapToDouble(Order::getTotalAmount)
                .sum();
        stats.put("totalRevenue", totalRevenue);
        
        // Calculate average order value
        double avgOrderValue = orders.values().stream()
                .filter(order -> order.getOrderingStatus() == OrderingStatus.CONFIRMED)
                .mapToDouble(Order::getTotalAmount)
                .average()
                .orElse(0.0);
        stats.put("averageOrderValue", avgOrderValue);
        
        return stats;
    }

    public Map<String, Integer> getOrderStatusStatistics() {
        Map<String, Integer> statusStats = new HashMap<>();
        for (OrderingStatus status : OrderingStatus.values()) {
            int count = getOrdersByStatus(status).size();
            statusStats.put(status.name(), count);
        }
        return statusStats;
    }

    public Map<String, Integer> getRestaurantOrderStatistics() {
        Map<String, Integer> restaurantStats = new HashMap<>();
        for (Map.Entry<String, List<Order>> entry : restaurantOrders.entrySet()) {
            restaurantStats.put(entry.getKey(), entry.getValue().size());
        }
        return restaurantStats;
    }

    // Utility Methods
    private Restaurant findRestaurantByOrder(Order order) {
        return restaurantService.getAllRestaurants().stream()
                .filter(restaurant -> restaurantOrders.getOrDefault(restaurant.getRestaurantId(), new ArrayList<>())
                        .contains(order))
                .findFirst()
                .orElse(null);
    }

    private DeliveryRider findRiderByOrder(Order order) {
        return riderService.findRiderByOrder(order);
    }

    public boolean isOrderLocked(String orderId) {
        OrderLock orderLock = activeLocks.get(orderId);
        return orderLock != null && orderLock.isLocked();
    }

    public List<String> getUnavailableItems(String orderId) {
        OrderLock orderLock = activeLocks.get(orderId);
        if (orderLock == null) {
            return new ArrayList<>();
        }
        return orderLock.getUnavailableItems();
    }

    public boolean canModifyOrder(String orderId) {
        Order order = orders.get(orderId);
        return order != null && 
               order.getOrderingStatus() == OrderingStatus.CREATED && 
               !isOrderLocked(orderId);
    }

    public int getEstimatedDeliveryTime(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return -1;
        }

        int preparationTime = order.getEstimatedPreparationTime();
        int deliveryTime = 30; // Default delivery time in minutes
        
        DeliveryRider rider = findRiderByOrder(order);
        if (rider != null) {
            deliveryTime = (int) rider.getAverageDeliveryTime();
        }

        return preparationTime + deliveryTime;
    }

    public double calculateDeliveryFee(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return -1;
        }

        Restaurant restaurant = findRestaurantByOrder(order);
        if (restaurant == null) {
            return -1;
        }

        return restaurantService.calculateDeliveryFee(restaurant.getRestaurantId(), order.getDeliveryAddress());
    }

    public void cleanupExpiredLocks() {
        List<String> expiredLocks = activeLocks.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (String orderId : expiredLocks) {
            releaseOrderLock(orderId);
        }
    }
}
