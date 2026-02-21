package observer;

import models.User;
import java.util.HashMap;
import java.util.Map;

public class ObservableUser extends ObservableEntity {
    private final User user;

    public ObservableUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void updateUser(String name, String email, String phoneNumber) {
        // Note: User class has final fields, so we can't actually update them
        // This is just for demonstration of the observer pattern
        
        // Notify observers
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", name);
        updateData.put("email", email);
        updateData.put("phoneNumber", phoneNumber);
        
        notifyObservers(NotificationService.EventTypes.USER_UPDATED, updateData);
    }

    @Override
    protected void notifyObserver(Observer observer, String eventType, Object data) {
        observer.update(user, eventType, data);
    }
}
