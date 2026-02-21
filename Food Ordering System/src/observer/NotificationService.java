package observer;

import models.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationService implements Observer {
    private final Map<String, List<Notification>> notifications;
    private final Map<String, List<Observer>> entityObservers;
    private final List<Observer> globalObservers;

    public NotificationService() {
        this.notifications = new ConcurrentHashMap<>();
        this.entityObservers = new ConcurrentHashMap<>();
        this.globalObservers = new ArrayList<>();
    }

    // Observer Registration
    public void registerObserver(String entityId, Observer observer) {
        entityObservers.computeIfAbsent(entityId, k -> new ArrayList<>()).add(observer);
    }

    public void unregisterObserver(String entityId, Observer observer) {
        List<Observer> observers = entityObservers.get(entityId);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public void registerGlobalObserver(Observer observer) {
        globalObservers.add(observer);
    }

    public void unregisterGlobalObserver(Observer observer) {
        globalObservers.remove(observer);
    }

    // Observer Interface Implementation
    @Override
    public void update(User user, String eventType, Object data) {
        String entityId = user.getUserId();
        createNotification(entityId, "USER", eventType, user, data);
        notifyEntityObservers(entityId, user, eventType, data);
        notifyGlobalObservers(user, eventType, data);
    }

    @Override
    public void update(Restaurant restaurant, String eventType, Object data) {
        String entityId = restaurant.getRestaurantId();
        createNotification(entityId, "RESTAURANT", eventType, restaurant, data);
        notifyEntityObservers(entityId, restaurant, eventType, data);
        notifyGlobalObservers(restaurant, eventType, data);
    }

    @Override
    public void update(DeliveryRider rider, String eventType, Object data) {
        String entityId = rider.getRiderId();
        createNotification(entityId, "RIDER", eventType, rider, data);
        notifyEntityObservers(entityId, rider, eventType, data);
        notifyGlobalObservers(rider, eventType, data);
    }

    @Override
    public void update(Order order, String eventType, Object data) {
        String entityId = order.getOrderId();
        createNotification(entityId, "ORDER", eventType, order, data);
        notifyEntityObservers(entityId, order, eventType, data);
        notifyGlobalObservers(order, eventType, data);
    }

    @Override
    public void update(Food food, String eventType, Object data) {
        String entityId = food.getFoodId();
        createNotification(entityId, "FOOD", eventType, food, data);
        notifyEntityObservers(entityId, food, eventType, data);
        notifyGlobalObservers(food, eventType, data);
    }

    // Notification Management
    private void createNotification(String entityId, String entityType, String eventType, Object entity, Object data) {
        Notification notification = new Notification(
            UUID.randomUUID().toString(),
            entityId,
            entityType,
            eventType,
            entity,
            data,
            LocalDateTime.now()
        );

        notifications.computeIfAbsent(entityId, k -> new ArrayList<>()).add(notification);
    }

    public List<Notification> getNotifications(String entityId) {
        return notifications.getOrDefault(entityId, new ArrayList<>());
    }

    public List<Notification> getNotificationsByType(String entityType) {
        return notifications.values().stream()
                .flatMap(List::stream)
                .filter(notification -> notification.getEntityType().equals(entityType))
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Notification> getNotificationsByEventType(String eventType) {
        return notifications.values().stream()
                .flatMap(List::stream)
                .filter(notification -> notification.getEventType().equals(eventType))
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Notification> getRecentNotifications(String entityId, int limit) {
        return notifications.getOrDefault(entityId, new ArrayList<>())
                .stream()
                .sorted((n1, n2) -> n2.getTimestamp().compareTo(n1.getTimestamp()))
                .limit(limit)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void clearNotifications(String entityId) {
        notifications.remove(entityId);
    }

    public void clearAllNotifications() {
        notifications.clear();
    }

    // Statistics
    public Map<String, Object> getNotificationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNotifications", notifications.values().stream().mapToInt(List::size).sum());
        stats.put("totalEntities", notifications.size());
        
        // Count by entity type
        Map<String, Integer> entityTypeCount = new HashMap<>();
        for (List<Notification> entityNotifications : notifications.values()) {
            for (Notification notification : entityNotifications) {
                String entityType = notification.getEntityType();
                entityTypeCount.put(entityType, entityTypeCount.getOrDefault(entityType, 0) + 1);
            }
        }
        stats.put("entityTypeDistribution", entityTypeCount);
        
        // Count by event type
        Map<String, Integer> eventTypeCount = new HashMap<>();
        for (List<Notification> entityNotifications : notifications.values()) {
            for (Notification notification : entityNotifications) {
                String eventType = notification.getEventType();
                eventTypeCount.put(eventType, eventTypeCount.getOrDefault(eventType, 0) + 1);
            }
        }
        stats.put("eventTypeDistribution", eventTypeCount);
        
        return stats;
    }

    // Private helper methods
    private void notifyEntityObservers(String entityId, Object entity, String eventType, Object data) {
        List<Observer> observers = entityObservers.get(entityId);
        if (observers != null) {
            for (Observer observer : new ArrayList<>(observers)) {
                try {
                    if (entity instanceof User) {
                        observer.update((User) entity, eventType, data);
                    } else if (entity instanceof Restaurant) {
                        observer.update((Restaurant) entity, eventType, data);
                    } else if (entity instanceof DeliveryRider) {
                        observer.update((DeliveryRider) entity, eventType, data);
                    } else if (entity instanceof Order) {
                        observer.update((Order) entity, eventType, data);
                    } else if (entity instanceof Food) {
                        observer.update((Food) entity, eventType, data);
                    }
                } catch (Exception e) {
                    // Log error but continue with other observers
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }

    private void notifyGlobalObservers(Object entity, String eventType, Object data) {
        for (Observer observer : new ArrayList<>(globalObservers)) {
            try {
                if (entity instanceof User) {
                    observer.update((User) entity, eventType, data);
                } else if (entity instanceof Restaurant) {
                    observer.update((Restaurant) entity, eventType, data);
                } else if (entity instanceof DeliveryRider) {
                    observer.update((DeliveryRider) entity, eventType, data);
                } else if (entity instanceof Order) {
                    observer.update((Order) entity, eventType, data);
                } else if (entity instanceof Food) {
                    observer.update((Food) entity, eventType, data);
                }
            } catch (Exception e) {
                // Log error but continue with other observers
                System.err.println("Error notifying global observer: " + e.getMessage());
            }
        }
    }

    // Event Types Constants
    public static class EventTypes {
        // User Events
        public static final String USER_CREATED = "USER_CREATED";
        public static final String USER_UPDATED = "USER_UPDATED";
        public static final String USER_DELETED = "USER_DELETED";
        
        // Restaurant Events
        public static final String RESTAURANT_CREATED = "RESTAURANT_CREATED";
        public static final String RESTAURANT_UPDATED = "RESTAURANT_UPDATED";
        public static final String RESTAURANT_OPENED = "RESTAURANT_OPENED";
        public static final String RESTAURANT_CLOSED = "RESTAURANT_CLOSED";
        public static final String RESTAURANT_RATING_UPDATED = "RESTAURANT_RATING_UPDATED";
        
        // Rider Events
        public static final String RIDER_CREATED = "RIDER_CREATED";
        public static final String RIDER_UPDATED = "RIDER_UPDATED";
        public static final String RIDER_ONLINE = "RIDER_ONLINE";
        public static final String RIDER_OFFLINE = "RIDER_OFFLINE";
        public static final String RIDER_LOCATION_UPDATED = "RIDER_LOCATION_UPDATED";
        public static final String RIDER_ORDER_ASSIGNED = "RIDER_ORDER_ASSIGNED";
        public static final String RIDER_ORDER_COMPLETED = "RIDER_ORDER_COMPLETED";
        public static final String RIDER_RATING_UPDATED = "RIDER_RATING_UPDATED";
        
        // Order Events
        public static final String ORDER_CREATED = "ORDER_CREATED";
        public static final String ORDER_UPDATED = "ORDER_UPDATED";
        public static final String ORDER_CONFIRMED = "ORDER_CONFIRMED";
        public static final String ORDER_CANCELLED = "ORDER_CANCELLED";
        public static final String ORDER_COMPLETED = "ORDER_COMPLETED";
        public static final String ORDER_ITEM_ADDED = "ORDER_ITEM_ADDED";
        public static final String ORDER_ITEM_REMOVED = "ORDER_ITEM_REMOVED";
        
        // Food Events
        public static final String FOOD_CREATED = "FOOD_CREATED";
        public static final String FOOD_UPDATED = "FOOD_UPDATED";
        public static final String FOOD_AVAILABILITY_CHANGED = "FOOD_AVAILABILITY_CHANGED";
        public static final String FOOD_QUANTITY_UPDATED = "FOOD_QUANTITY_UPDATED";
        public static final String FOOD_PRICE_UPDATED = "FOOD_PRICE_UPDATED";
    }
}

