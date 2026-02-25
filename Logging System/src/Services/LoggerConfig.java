package Services;

import Appenders.LogAppender;
import Constants.LogLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the Logger.
 * Holds the minimum log level and the list of appenders.
 * Allows dynamic reconfiguration at runtime.
 */
public class LoggerConfig {

    private final List<LogAppender> appenders;

    public LoggerConfig() {
        this.appenders = new ArrayList<>();
    }

    public void addAppender(LogAppender appender) {
        appenders.add(appender);
    }

    public void removeAppender(LogAppender appender) {
        appenders.remove(appender);
    }

    public List<LogAppender> getAppenders() {
        return new ArrayList<>(appenders);
    }
}
