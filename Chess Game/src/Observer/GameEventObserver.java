package Observer;

/**
 * Observer Pattern — interface for components that want to listen to game
 * events.
 */
public interface GameEventObserver {
    void onEvent(GameEvent event);
}
