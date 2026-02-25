package Services;

import Constants.LogLevel;
import LogHandlers.*;

/**
 * Singleton Logger — central logging facade.
 *
 * Builds the Chain of Responsibility from log handlers and delegates
 * log messages through the chain. Filters by minimum log level before
 * passing to the chain.
 *
 * Thread-safe: all log methods are synchronized.
 */
public class Logger {

    private static Logger instance;
    private final LoggerConfig config;
    private LogHandler chainHead;

    private Logger(LoggerConfig config) {
        this.config = config;
        buildChain();
    }

    public static synchronized Logger getInstance(LoggerConfig config) {
        if (instance == null) {
            instance = new Logger(config);
        }
        return instance;
    }

    /**
     * Builds the handler chain: DEBUG → INFO → WARN → ERROR → FATAL.
     * Each handler in the chain gets the same set of appenders from config.
     */
    private void buildChain() {
        DebugLogger debugLogger = new DebugLogger();
        InfoLogger infoLogger = new InfoLogger();
        WarnLogger warnLogger = new WarnLogger();
        ErrorLogger errorLogger = new ErrorLogger();
        FatalLogger fatalLogger = new FatalLogger();

        // Set appenders on each handler
        debugLogger.setAppenders(config.getAppenders());
        infoLogger.setAppenders(config.getAppenders());
        warnLogger.setAppenders(config.getAppenders());
        errorLogger.setAppenders(config.getAppenders());
        fatalLogger.setAppenders(config.getAppenders());

        // Build chain: only the first handler that matches the min level is the head
        chainHead = getHandlerForLevel(debugLogger, infoLogger, warnLogger, errorLogger, fatalLogger);
    }

    /**
     * Returns the starting handler for the configured minimum level.
     */
    private LogHandler getHandlerForLevel(DebugLogger debug, InfoLogger info,
            WarnLogger warn, ErrorLogger error,
            FatalLogger fatal) {
        // Link the full chain
        debug.setNextHandler(info);
        info.setNextHandler(warn);
        warn.setNextHandler(error);
        error.setNextHandler(fatal);

        return debug;
    }

    // ─── Convenience logging methods ──────────────────────────────

    public synchronized void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public synchronized void info(String message) {
        log(LogLevel.INFO, message);
    }

    public synchronized void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public synchronized void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public synchronized void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

    /**
     * Core log method — filters by min level, then delegates to chain.
     */
    public synchronized void log(LogLevel level, String message) {
        if (chainHead != null) {
            chainHead.handleLog(level, message);
        }
    }

    public LoggerConfig getConfig() {
        return config;
    }
}
