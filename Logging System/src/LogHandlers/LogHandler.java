package LogHandlers;

import Appenders.LogAppender;
import Constants.LogLevel;
import Services.LogMessage;

public abstract class LogHandler {
    public static final int TRACE = 10;
    public static final int DEBUG = 20;
    public static final int INFO = 30;
    public static final int WARN = 40;
    public static final int ERROR = 50;

    protected int level;
    protected LogHandler nextLogger;
    protected LogAppender appender; // the appender where we need to append the logs

    // Constructor to initialize with appender
    public LogHandler(int level, LogAppender appender) {
        this.level = level;
        this.appender = appender;
    }

    // Set the next logger in the chain
    public void setNextLogger(LogHandler nextLogger) {
        this.nextLogger = nextLogger;
    }

    // Corrected to use Constants.LogLevel instead of int for consistency
    public void logMessage(int level, String message) {
        if (this.level >= level) {
            // Convert int level to Constants.LogLevel enum
            LogLevel logLevel = intToLogLevel(level);
            LogMessage logMsg = new LogMessage(logLevel, message);
            // Use the appender to log
            if (appender != null)
                appender.append(logMsg);
            write(message);
        }
        else if (nextLogger != null)
            nextLogger.logMessage(level, message);
    }


    // Helper method to convert int level to Constants.LogLevel enum
    private LogLevel intToLogLevel(int level) {
        return switch (level) {
            case TRACE -> LogLevel.TRACE;
            case DEBUG -> LogLevel.DEBUG;
            case WARN -> LogLevel.WARN;
            case ERROR -> LogLevel.ERROR;
            default -> LogLevel.INFO;
        };
    }

    // Each concrete logger will implement its own writing mechanism
    abstract protected void write(String message);
}
