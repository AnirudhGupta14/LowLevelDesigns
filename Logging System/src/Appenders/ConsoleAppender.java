package Appenders;

import Constants.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appender that writes log messages to the console (stdout/stderr).
 * ERROR and FATAL go to stderr, everything else to stdout.
 */
public class ConsoleAppender implements LogAppender {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void append(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String formatted = "[" + timestamp + "] [" + level + "] " + message;

        if (level == LogLevel.ERROR || level == LogLevel.FATAL) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }
    }
}
