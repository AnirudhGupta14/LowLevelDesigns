package LogHandlers;

import Constants.LogLevel;

/**
 * Handles DEBUG level logs.
 * In a chain, this is typically the first handler (lowest severity).
 */
public class DebugLogger extends LogHandler {
    public DebugLogger() {
        super(LogLevel.DEBUG);
    }
}
