package LogHandlers;

import Appenders.LogAppender;
import Constants.LogLevel;

import java.util.List;

/**
 * Chain of Responsibility — abstract log handler.
 *
 * Each handler has a log level it is responsible for.
 * If the incoming message's level matches, it processes it.
 * Then it always passes the message to the next handler in the chain.
 */
public abstract class LogHandler {

    protected LogLevel level;
    private LogHandler nextHandler;
    private List<LogAppender> appenders;

    public LogHandler(LogLevel level) {
        this.level = level;
    }

    /**
     * Set the next handler in the chain. Returns the next handler for fluent
     * chaining.
     */
    public void setNextHandler(LogHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Set the appenders this handler should write to.
     */
    public void setAppenders(List<LogAppender> appenders) {
        this.appenders = appenders;
    }

    /**
     * Process the log message.
     * If the level exactly matches this handler's level, write to appenders.
     * Then pass to the next handler in chain regardless.
     */
    public void handleLog(LogLevel logLevel, String message) {
        if (logLevel == this.level) {
            writeToAppenders(logLevel, message);
        }

        // Pass to next handler
        if (nextHandler != null) {
            nextHandler.handleLog(logLevel, message);
        }
    }

    /**
     * Write the message to all configured appenders.
     */
    private void writeToAppenders(LogLevel logLevel, String message) {
        if (appenders != null) {
            for (LogAppender appender : appenders) {
                appender.append(logLevel, message);
            }
        }
    }

    public LogLevel getLevel() {
        return level;
    }
}
