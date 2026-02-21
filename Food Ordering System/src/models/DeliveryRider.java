package models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class DeliveryRider {
    private final String riderId;
    private final String name;
    private final String phoneNumber;
    private final String email;
    private final String vehicleType; // Bike, Scooter, Car, etc.
    private final String vehicleNumber;
    private final String licenseNumber;
    private final Set<String> deliveryAreas; // Areas where rider can deliver
    private final double maxDeliveryRadius; // in kilometers
    private final int maxOrderCapacity; // Maximum number of orders rider can carry
    private boolean isAvailable;
    private boolean isOnline;
    private LocalDateTime lastActiveTime;
    private String currentLocation; // Current location of the rider
    private final List<Order> assignedOrders;
    private final List<Order> completedOrders;
    private double rating;
    private int totalDeliveries;
    private int totalEarnings;
    private final LocalDateTime joinDate;

    public DeliveryRider(String name, String phoneNumber, String email, String vehicleType, 
                        String vehicleNumber, String licenseNumber, Set<String> deliveryAreas,
                        double maxDeliveryRadius, int maxOrderCapacity) {
        this.riderId = UUID.randomUUID().toString();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.licenseNumber = licenseNumber;
        this.deliveryAreas = new HashSet<>(deliveryAreas);
        this.maxDeliveryRadius = maxDeliveryRadius;
        this.maxOrderCapacity = maxOrderCapacity;
        this.isAvailable = true;
        this.isOnline = false;
        this.lastActiveTime = LocalDateTime.now();
        this.currentLocation = null;
        this.assignedOrders = new ArrayList<>();
        this.completedOrders = new ArrayList<>();
        this.rating = 0.0;
        this.totalDeliveries = 0;
        this.totalEarnings = 0;
        this.joinDate = LocalDateTime.now();
    }

    public void goOnline() {
        this.isOnline = true;
        this.lastActiveTime = LocalDateTime.now();
    }

    public void goOffline() {
        this.isOnline = false;
        this.lastActiveTime = LocalDateTime.now();
    }

    public void updateAvailability(boolean available) {
        this.isAvailable = available;
        if (available) {
            this.lastActiveTime = LocalDateTime.now();
        }
    }

    public void updateLocation(String location) {
        this.currentLocation = location;
        this.lastActiveTime = LocalDateTime.now();
    }

    public boolean canDeliverTo(String deliveryAddress) {
        if (!isOnline || !isAvailable) {
            return false;
        }
        
        // Check if delivery address is in rider's delivery areas
        for (String area : deliveryAreas) {
            if (deliveryAddress.toLowerCase().contains(area.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean canAcceptOrder() {
        return isOnline && isAvailable && assignedOrders.size() < maxOrderCapacity;
    }

    public boolean assignOrder(Order order) {
        if (!canAcceptOrder() || !canDeliverTo(order.getDeliveryAddress())) {
            return false;
        }
        
        assignedOrders.add(order);
        updateAvailability(assignedOrders.size() < maxOrderCapacity);
        return true;
    }

    public boolean completeOrder(Order order) {
        if (!assignedOrders.contains(order)) {
            return false;
        }
        
        assignedOrders.remove(order);
        completedOrders.add(order);
        totalDeliveries++;
        
        // Calculate earnings (assuming fixed delivery fee per order)
        int deliveryFee = 50; // Base delivery fee
        totalEarnings += deliveryFee;
        
        updateAvailability(assignedOrders.size() < maxOrderCapacity);
        return true;
    }

    public void cancelOrder(Order order) {
        if (assignedOrders.contains(order)) {
            assignedOrders.remove(order);
            updateAvailability(assignedOrders.size() < maxOrderCapacity);
        }
    }

    public int getCurrentOrderCount() {
        return assignedOrders.size();
    }

    public int getRemainingCapacity() {
        return maxOrderCapacity - assignedOrders.size();
    }

    public List<Order> getAssignedOrders() {
        return new ArrayList<>(assignedOrders);
    }

    public List<Order> getCompletedOrders() {
        return new ArrayList<>(completedOrders);
    }

    public void updateRating(double newRating) {
        if (newRating >= 0 && newRating <= 5) {
            // Calculate weighted average rating
            double totalRatingPoints = rating * totalDeliveries;
            totalRatingPoints += newRating;
            totalDeliveries++;
            rating = totalRatingPoints / totalDeliveries;
        }
    }

    public double getAverageDeliveryTime() {
        if (completedOrders.isEmpty()) {
            return 0.0;
        }
        
        // This would need to be calculated based on actual delivery times
        // For now, returning a placeholder
        return 30.0; // 30 minutes average
    }

    public boolean isInDeliveryArea(String area) {
        return deliveryAreas.stream()
                .anyMatch(deliveryArea -> area.toLowerCase().contains(deliveryArea.toLowerCase()));
    }

    public void addDeliveryArea(String area) {
        deliveryAreas.add(area);
    }

    public void removeDeliveryArea(String area) {
        deliveryAreas.remove(area);
    }

    public long getMinutesSinceLastActive() {
        return java.time.Duration.between(lastActiveTime, LocalDateTime.now()).toMinutes();
    }

    public boolean isRecentlyActive(int minutesThreshold) {
        return getMinutesSinceLastActive() <= minutesThreshold;
    }

    public String getStatus() {
        if (!isOnline) {
            return "OFFLINE";
        } else if (!isAvailable) {
            return "BUSY";
        } else if (assignedOrders.isEmpty()) {
            return "AVAILABLE";
        } else {
            return "DELIVERING";
        }
    }

    public Map<String, Object> getRiderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDeliveries", totalDeliveries);
        stats.put("totalEarnings", totalEarnings);
        stats.put("rating", rating);
        stats.put("currentOrders", assignedOrders.size());
        stats.put("completedOrders", completedOrders.size());
        stats.put("averageDeliveryTime", getAverageDeliveryTime());
        stats.put("status", getStatus());
        return stats;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DeliveryRider rider = (DeliveryRider) obj;
        return riderId.equals(rider.riderId);
    }

    @Override
    public int hashCode() {
        return riderId.hashCode();
    }

    @Override
    public String toString() {
        return "DeliveryRider{" +
                "riderId='" + riderId + '\'' +
                ", name='" + name + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", isOnline=" + isOnline +
                ", isAvailable=" + isAvailable +
                ", currentOrders=" + assignedOrders.size() +
                ", rating=" + rating +
                ", status=" + getStatus() +
                '}';
    }
}
