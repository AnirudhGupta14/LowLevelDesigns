package LogHandlers;

import Constants.LogLevel;

/**
 * Handles FATAL level logs.
 */
public class FatalLogger extends LogHandler {
    public FatalLogger() {
        super(LogLevel.FATAL);
    }
}
