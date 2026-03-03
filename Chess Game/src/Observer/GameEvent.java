package Observer;

/**
 * Immutable event object passed to all registered GameEventObservers.
 */
public class GameEvent {
    private final GameEventType type;
    private final String message;

    public GameEvent(GameEventType type, String message) {
        this.type = type;
        this.message = message;
    }

    public GameEventType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + message;
    }
}
