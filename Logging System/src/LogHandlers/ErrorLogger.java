package LogHandlers;

import Constants.LogLevel;

/**
 * Handles ERROR level logs and above.
 * Typically the last handler in the chain (highest severity).
 */
public class ErrorLogger extends LogHandler {
    public ErrorLogger() {
        super(LogLevel.ERROR);
    }
}
