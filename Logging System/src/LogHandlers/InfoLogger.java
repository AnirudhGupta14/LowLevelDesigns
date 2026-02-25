package LogHandlers;

import Constants.LogLevel;

/**
 * Handles INFO level logs and above.
 */
public class InfoLogger extends LogHandler {
    public InfoLogger() {
        super(LogLevel.INFO);
    }
}
