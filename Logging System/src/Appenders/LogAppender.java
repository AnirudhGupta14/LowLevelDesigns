package Appenders;

import Services.LogMessage;

public interface LogAppender {
    void append(LogMessage logMessage);
}
