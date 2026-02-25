package Appenders;

import Constants.LogLevel;

/**
 * Strategy Pattern interface — defines WHERE log messages are written.
 * Implementations decide the output destination (console, file, etc.).
 */
public interface LogAppender {
    void append(LogLevel level, String message);
}
