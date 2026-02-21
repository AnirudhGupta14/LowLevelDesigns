package Appenders;

import Services.LogMessage;

// Prints log messages to the console
public class ConsoleAppender implements LogAppender {
    // Appends a log message to the console
    @Override
    public void append(LogMessage logMessage) {
        System.out.println(logMessage); // Print log to console
    }
}