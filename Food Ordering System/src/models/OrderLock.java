package models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class OrderLock {
    private final String lockId;
    private final Order order;
    private final User user;
    private final Map<Food, Integer> lockedItems; // Food and locked quantity
    private final LocalDateTime lockTime;
    private LocalDateTime expiryTime;
    private final ReentrantLock lock;
    private boolean isActive;
    private final String orderId; // Associated order ID

    // Lock duration in minutes
    private static final int DEFAULT_LOCK_DURATION = 15;

    public OrderLock(Order order, User user) {
        this.lockId = UUID.randomUUID().toString();
        this.order = order;
        this.user = user;
        this.lockedItems = new HashMap<>();
        this.lockTime = LocalDateTime.now();
        this.expiryTime = lockTime.plusMinutes(DEFAULT_LOCK_DURATION);
        this.lock = new ReentrantLock();
        this.isActive = false; // Will be set to true only after successful lock
        this.orderId = order.getOrderId();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public boolean isLocked() {
        return isActive && !isExpired();
    }

    public boolean canLockAllItems() {
        // Check if all food items in the order are available
        Map<Food, Integer> orderItems = order.getOrderItems();
        for (Map.Entry<Food, Integer> entry : orderItems.entrySet()) {
            Food food = entry.getKey();
            int quantity = entry.getValue();
            if (!food.isAvailable() || food.getAvailableQuantity() < quantity) {
                return false;
            }
        }
        return true;
    }

    public List<String> getUnavailableItems() {
        List<String> unavailableItems = new ArrayList<>();
        Map<Food, Integer> orderItems = order.getOrderItems();
        
        for (Map.Entry<Food, Integer> entry : orderItems.entrySet()) {
            Food food = entry.getKey();
            int quantity = entry.getValue();
            
            if (!food.isAvailable()) {
                unavailableItems.add(food.getName() + " - Not Available");
            } else if (food.getAvailableQuantity() < quantity) {
                unavailableItems.add(food.getName() + " - Only " + food.getAvailableQuantity() + " available, need " + quantity);
            }
        }
        return unavailableItems;
    }

    public boolean acquireLock() {
        if (!canLockAllItems() || isExpired()) {
            return false;
        }

        try {
            if (lock.tryLock()) {
                // Double-check availability after acquiring lock
                if (canLockAllItems()) {
                    Map<Food, Integer> orderItems = order.getOrderItems();
                    
                    // Lock all items
                    for (Map.Entry<Food, Integer> entry : orderItems.entrySet()) {
                        Food food = entry.getKey();
                        int quantity = entry.getValue();
                        food.reduceQuantity(quantity);
                        lockedItems.put(food, quantity);
                    }
                    
                    isActive = true;
                    return true;
                } else {
                    lock.unlock();
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void releaseLock() {
        try {
            if (lock.isHeldByCurrentThread()) {
                if (isActive) {
                    // Release all locked items
                    for (Map.Entry<Food, Integer> entry : lockedItems.entrySet()) {
                        Food food = entry.getKey();
                        int quantity = entry.getValue();
                        food.addQuantity(quantity);
                    }
                    lockedItems.clear();
                    isActive = false;
                }
                lock.unlock();
            }
        } catch (Exception e) {
            // Handle exception if needed
        }
    }

    public void extendLock(int additionalMinutes) {
        if (isLocked()) {
            // Create new expiry time by adding additional minutes to current time
            this.expiryTime = LocalDateTime.now().plusMinutes(additionalMinutes);
        }
    }

    public long getRemainingTimeInMinutes() {
        if (isExpired()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), expiryTime).toMinutes();
    }

    public boolean isAssociatedWithOrder() {
        return orderId != null;
    }

    public void convertToOrder() {
        // This method can be called when the lock is converted to an actual order
        // The food quantities remain reduced, and the lock becomes inactive
        isActive = false;
    }

    public boolean belongsToUser(User user) {
        return this.user.equals(user);
    }

    public boolean containsFood(Food food) {
        return lockedItems.containsKey(food);
    }

    public double getLockedValue() {
        double totalValue = 0.0;
        for (Map.Entry<Food, Integer> entry : lockedItems.entrySet()) {
            Food food = entry.getKey();
            int quantity = entry.getValue();
            totalValue += food.getPrice() * quantity;
        }
        return totalValue;
    }

    public int getTotalLockedItems() {
        return lockedItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void forceRelease() {
        // Force release the lock regardless of current state
        try {
            if (lock.isHeldByCurrentThread()) {
                if (isActive) {
                    // Release all locked items
                    for (Map.Entry<Food, Integer> entry : lockedItems.entrySet()) {
                        Food food = entry.getKey();
                        int quantity = entry.getValue();
                        food.addQuantity(quantity);
                    }
                    lockedItems.clear();
                }
                isActive = false;
                lock.unlock();
            }
        } catch (Exception e) {
            // Handle exception if needed
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderLock orderLock = (OrderLock) obj;
        return lockId.equals(orderLock.lockId);
    }

    @Override
    public int hashCode() {
        return lockId.hashCode();
    }

    @Override
    public String toString() {
        return "OrderLock{" +
                "lockId='" + lockId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", user=" + user.getName() +
                ", totalItems=" + getTotalLockedItems() +
                ", lockTime=" + lockTime +
                ", expiryTime=" + expiryTime +
                ", isActive=" + isActive +
                '}';
    }
}