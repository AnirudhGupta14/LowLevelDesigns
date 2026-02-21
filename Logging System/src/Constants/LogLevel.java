package Constants;

// Constants.LogLevel.java
public enum LogLevel {
    TRACE(10), DEBUG(20), INFO(30), WARN(40), ERROR(50);

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }

    // Getter method to retrieve the numeric value of a log level
    public int getValue() {
        return value;
    }

    // Method to compare log levels based on severity
    public boolean isGreaterOrEqual(LogLevel other) {
        return this.value >= other.value;
    }
}
