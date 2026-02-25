package Appenders;

import Constants.LogLevel;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Appender that writes log messages to a file.
 * Creates the file if it doesn't exist, appends if it does.
 */
public class FileAppender implements LogAppender {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String filePath;

    public FileAppender(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void append(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String formatted = "[" + timestamp + "] [" + level + "] " + message;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(formatted);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + filePath + " — " + e.getMessage());
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
