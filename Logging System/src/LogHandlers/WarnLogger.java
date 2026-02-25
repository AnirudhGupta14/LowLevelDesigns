package LogHandlers;

import Constants.LogLevel;

/**
 * Handles WARN level logs and above.
 */
public class WarnLogger extends LogHandler {
    public WarnLogger() {
        super(LogLevel.WARN);
    }
}
