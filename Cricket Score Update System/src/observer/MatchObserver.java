package observer;

import entities.MatchUpdateEvent;

// Observer interface for real-time updates
public interface MatchObserver {
    void onMatchUpdate(MatchUpdateEvent event);
}

