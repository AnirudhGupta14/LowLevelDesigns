package services;

import models.DeliveryRider;
import models.Order;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RiderService {
    private final Map<String, DeliveryRider> riders;
    private final Map<String, List<DeliveryRider>> ridersByArea;
    private final Map<String, DeliveryRider> ridersByVehicleType;

    public RiderService() {
        this.riders = new ConcurrentHashMap<>();
        this.ridersByArea = new ConcurrentHashMap<>();
        this.ridersByVehicleType = new ConcurrentHashMap<>();
    }

    // Rider Management
    public void addRider(DeliveryRider rider) {
        riders.put(rider.getRiderId(), rider);
        updateAreaIndex(rider);
        updateVehicleTypeIndex(rider);
    }

    public void removeRider(String riderId) {
        DeliveryRider rider = riders.remove(riderId);
        if (rider != null) {
            removeFromAreaIndex(rider);
            removeFromVehicleTypeIndex(rider);
        }
    }

    public DeliveryRider getRider(String riderId) {
        return riders.get(riderId);
    }

    public List<DeliveryRider> getAllRiders() {
        return new ArrayList<>(riders.values());
    }

    public List<DeliveryRider> getAvailableRiders() {
        return riders.values().stream()
                .filter(DeliveryRider::canAcceptOrder)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getOnlineRiders() {
        return riders.values().stream()
                .filter(DeliveryRider::isOnline)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getOfflineRiders() {
        return riders.values().stream()
                .filter(rider -> !rider.isOnline())
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getRidersByArea(String area) {
        return ridersByArea.getOrDefault(area, new ArrayList<>())
                .stream()
                .filter(DeliveryRider::canAcceptOrder)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getRidersByVehicleType(String vehicleType) {
        DeliveryRider rider = ridersByVehicleType.get(vehicleType);
        if (rider != null && rider.canAcceptOrder()) {
            List<DeliveryRider> result = new ArrayList<>();
            result.add(rider);
            return result;
        }
        return new ArrayList<>();
    }

    public List<DeliveryRider> getTopRatedRiders(int limit) {
        return riders.values().stream()
                .filter(DeliveryRider::isOnline)
                .sorted((r1, r2) -> Double.compare(r2.getRating(), r1.getRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getRidersByRating(double minRating) {
        return riders.values().stream()
                .filter(rider -> rider.getRating() >= minRating)
                .filter(DeliveryRider::isOnline)
                .sorted((r1, r2) -> Double.compare(r2.getRating(), r1.getRating()))
                .collect(Collectors.toList());
    }

    // Rider Status Management
    public void goOnline(String riderId) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            rider.goOnline();
        }
    }

    public void goOffline(String riderId) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            rider.goOffline();
        }
    }

    public void updateRiderLocation(String riderId, String location) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            rider.updateLocation(location);
        }
    }

    public void setRiderAvailability(String riderId, boolean available) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            rider.updateAvailability(available);
        }
    }

    public boolean isRiderOnline(String riderId) {
        DeliveryRider rider = riders.get(riderId);
        return rider != null && rider.isOnline();
    }

    public boolean isRiderAvailable(String riderId) {
        DeliveryRider rider = riders.get(riderId);
        return rider != null && rider.canAcceptOrder();
    }

    // Order Assignment
    public DeliveryRider findBestRiderForOrder(Order order) {
        return riders.values().stream()
                .filter(rider -> rider.canDeliverTo(order.getDeliveryAddress()))
                .filter(DeliveryRider::canAcceptOrder)
                .max(Comparator.comparing(DeliveryRider::getRating))
                .orElse(null);
    }

    public DeliveryRider findNearestRiderForOrder(Order order) {
        // This is a simplified implementation - in real scenario, you'd use geolocation
        return riders.values().stream()
                .filter(rider -> rider.canDeliverTo(order.getDeliveryAddress()))
                .filter(DeliveryRider::canAcceptOrder)
                .min(Comparator.comparing(rider -> rider.getMinutesSinceLastActive()))
                .orElse(null);
    }

    public DeliveryRider findRiderWithLeastOrders(Order order) {
        return riders.values().stream()
                .filter(rider -> rider.canDeliverTo(order.getDeliveryAddress()))
                .filter(DeliveryRider::canAcceptOrder)
                .min(Comparator.comparing(DeliveryRider::getCurrentOrderCount))
                .orElse(null);
    }

    public boolean assignOrderToRider(String riderId, Order order) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null && rider.canAcceptOrder() && rider.canDeliverTo(order.getDeliveryAddress())) {
            return rider.assignOrder(order);
        }
        return false;
    }

    public boolean completeOrderForRider(String riderId, Order order) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            return rider.completeOrder(order);
        }
        return false;
    }

    public boolean cancelOrderForRider(String riderId, Order order) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            rider.cancelOrder(order);
            return true;
        }
        return false;
    }

    // Rating Management
    public void updateRiderRating(String riderId, double newRating) {
        DeliveryRider rider = riders.get(riderId);
        if (rider != null) {
            rider.updateRating(newRating);
        }
    }

    public double getRiderRating(String riderId) {
        DeliveryRider rider = riders.get(riderId);
        return rider != null ? rider.getRating() : 0.0;
    }

    // Rider Performance
    public List<DeliveryRider> getMostActiveRiders(int limit) {
        return riders.values().stream()
                .filter(DeliveryRider::isOnline)
                .sorted((r1, r2) -> Integer.compare(r2.getTotalDeliveries(), r1.getTotalDeliveries()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getTopEarningRiders(int limit) {
        return riders.values().stream()
                .filter(DeliveryRider::isOnline)
                .sorted((r1, r2) -> Integer.compare(r2.getTotalEarnings(), r1.getTotalEarnings()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getRecentlyActiveRiders(int minutesThreshold) {
        return riders.values().stream()
                .filter(rider -> rider.isRecentlyActive(minutesThreshold))
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getInactiveRiders(int minutesThreshold) {
        return riders.values().stream()
                .filter(rider -> !rider.isRecentlyActive(minutesThreshold))
                .collect(Collectors.toList());
    }

    // Statistics and Analytics
    public Map<String, Object> getRiderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRiders", riders.size());
        stats.put("onlineRiders", getOnlineRiders().size());
        stats.put("offlineRiders", getOfflineRiders().size());
        stats.put("availableRiders", getAvailableRiders().size());
        
        // Calculate average rating
        double avgRating = riders.values().stream()
                .mapToDouble(DeliveryRider::getRating)
                .average()
                .orElse(0.0);
        stats.put("averageRating", avgRating);
        
        // Calculate total deliveries
        int totalDeliveries = riders.values().stream()
                .mapToInt(DeliveryRider::getTotalDeliveries)
                .sum();
        stats.put("totalDeliveries", totalDeliveries);
        
        // Calculate total earnings
        int totalEarnings = riders.values().stream()
                .mapToInt(DeliveryRider::getTotalEarnings)
                .sum();
        stats.put("totalEarnings", totalEarnings);
        
        // Vehicle type distribution
        Map<String, Integer> vehicleStats = new HashMap<>();
        for (DeliveryRider rider : riders.values()) {
            String vehicleType = rider.getVehicleType();
            vehicleStats.put(vehicleType, vehicleStats.getOrDefault(vehicleType, 0) + 1);
        }
        stats.put("vehicleTypeDistribution", vehicleStats);
        
        return stats;
    }

    public Map<String, Integer> getVehicleTypeStatistics() {
        Map<String, Integer> vehicleStats = new HashMap<>();
        for (DeliveryRider rider : riders.values()) {
            String vehicleType = rider.getVehicleType();
            vehicleStats.put(vehicleType, vehicleStats.getOrDefault(vehicleType, 0) + 1);
        }
        return vehicleStats;
    }

    public Map<String, Integer> getAreaStatistics() {
        Map<String, Integer> areaStats = new HashMap<>();
        for (DeliveryRider rider : riders.values()) {
            for (String area : rider.getDeliveryAreas()) {
                areaStats.put(area, areaStats.getOrDefault(area, 0) + 1);
            }
        }
        return areaStats;
    }

    public List<DeliveryRider> getRidersWithMostOrders(int limit) {
        return riders.values().stream()
                .filter(DeliveryRider::isOnline)
                .sorted((r1, r2) -> Integer.compare(r2.getCurrentOrderCount(), r1.getCurrentOrderCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<DeliveryRider> getRidersWithLeastOrders(int limit) {
        return riders.values().stream()
                .filter(DeliveryRider::isOnline)
                .sorted(Comparator.comparing(DeliveryRider::getCurrentOrderCount))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Utility Methods
    private void updateAreaIndex(DeliveryRider rider) {
        for (String area : rider.getDeliveryAreas()) {
            ridersByArea.computeIfAbsent(area, k -> new ArrayList<>()).add(rider);
        }
    }

    private void removeFromAreaIndex(DeliveryRider rider) {
        for (String area : rider.getDeliveryAreas()) {
            List<DeliveryRider> areaRiders = ridersByArea.get(area);
            if (areaRiders != null) {
                areaRiders.remove(rider);
                if (areaRiders.isEmpty()) {
                    ridersByArea.remove(area);
                }
            }
        }
    }

    private void updateVehicleTypeIndex(DeliveryRider rider) {
        ridersByVehicleType.put(rider.getVehicleType(), rider);
    }

    private void removeFromVehicleTypeIndex(DeliveryRider rider) {
        ridersByVehicleType.remove(rider.getVehicleType());
    }

    public Set<String> getAllVehicleTypes() {
        return ridersByVehicleType.keySet();
    }

    public Set<String> getAllDeliveryAreas() {
        return ridersByArea.keySet();
    }

    public boolean riderExists(String riderId) {
        return riders.containsKey(riderId);
    }

    public int getTotalRiders() {
        return riders.size();
    }

    public int getOnlineRidersCount() {
        return getOnlineRiders().size();
    }

    public int getAvailableRidersCount() {
        return getAvailableRiders().size();
    }

    public DeliveryRider findRiderByOrder(Order order) {
        return riders.values().stream()
                .filter(rider -> rider.getAssignedOrders().contains(order))
                .findFirst()
                .orElse(null);
    }

    public List<Order> getAllAssignedOrders() {
        return riders.values().stream()
                .flatMap(rider -> rider.getAssignedOrders().stream())
                .collect(Collectors.toList());
    }

    public int getTotalAssignedOrders() {
        return riders.values().stream()
                .mapToInt(DeliveryRider::getCurrentOrderCount)
                .sum();
    }
}
