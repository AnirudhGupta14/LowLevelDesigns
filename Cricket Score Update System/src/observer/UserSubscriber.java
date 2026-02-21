package observer;

import entities.MatchUpdateEvent;
import lombok.Getter;
import lombok.Setter;

// Concrete Observer implementation for users/viewers
@Getter
@Setter
public class UserSubscriber implements MatchObserver {
    private final String userId;
    private final String userName;
    private final boolean receiveAllUpdates;

    public UserSubscriber(String userId, String userName, boolean receiveAllUpdates) {
        this.userId = userId;
        this.userName = userName;
        this.receiveAllUpdates = receiveAllUpdates;
    }

    @Override
    public void onMatchUpdate(MatchUpdateEvent event) {
        // Filter updates based on user preferences
        if (!receiveAllUpdates && event.getUpdateType().equals("SUBSCRIPTION")) {
            return; // Skip subscription notifications
        }

        // Simulate push notification or websocket update
        System.out.println("[NOTIFICATION TO " + userName + "] " + event.toString());
    }
}

