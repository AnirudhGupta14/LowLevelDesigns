package observer;

import java.time.LocalDateTime;

public class Notification {
    private final String notificationId;
    private final String entityId;
    private final String entityType;
    private final String eventType;
    private final Object entity;
    private final Object data;
    private final LocalDateTime timestamp;

    public Notification(String notificationId, String entityId, String entityType, 
                       String eventType, Object entity, Object data, LocalDateTime timestamp) {
        this.notificationId = notificationId;
        this.entityId = entityId;
        this.entityType = entityType;
        this.eventType = eventType;
        this.entity = entity;
        this.data = data;
        this.timestamp = timestamp;
    }

    // Getters
    public String getNotificationId() { return notificationId; }
    public String getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }
    public String getEventType() { return eventType; }
    public Object getEntity() { return entity; }
    public Object getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", entityType='" + entityType + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
