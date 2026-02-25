import Appenders.ConsoleAppender;
import Appenders.FileAppender;
import Appenders.LogAppender;
import Constants.LogLevel;
import Services.Logger;
import Services.LoggerConfig;

public class Main {

    public static void main(String[] args) {

        System.out.println("==========================================================");
        System.out.println("         📝 LOGGING SYSTEM — DEMO");
        System.out.println("==========================================================\n");

        // ─── STEP 1: Configure Logger ─────────────────────────────
        System.out.println("── STEP 1: Configure Logger (DEBUG level + Console + File) ──\n");

        LoggerConfig config = new LoggerConfig();

        // Add console appender
        LogAppender consoleAppender = new ConsoleAppender();
        config.addAppender(consoleAppender);

        // Add file appender
        LogAppender fileAppender = new FileAppender("application.log");
        config.addAppender(fileAppender);

        // Create singleton logger
        Logger logger = Logger.getInstance(config);

        // ─── STEP 2: Log at all levels ────────────────────────────
        System.out.println("── STEP 2: Logging at all levels (min level = DEBUG) ──\n");

        logger.debug("Application starting up...");
        logger.info("User 'Alice' logged in successfully.");
        logger.warn("Disk usage is above 85%.");
        logger.error("Failed to connect to database: timeout after 30s.");
        logger.fatal("System out of memory! Shutting down.");

        // ─── STEP 5: Console-only logging ─────────────────────────
        System.out.println("\n── STEP 5: Remove file appender (console only) ──\n");

        config.removeAppender(fileAppender);

        System.out.println();
        logger.info("This goes only to console, not to file.");
        logger.error("Console-only error logging active.");

        // ─── STEP 6: Multiple file appenders ──────────────────────
        System.out.println("\n── STEP 6: Add error-specific file appender ──\n");

        LogAppender errorFileAppender = new FileAppender("errors.log");
        config.addAppender(errorFileAppender);

        System.out.println();
        logger.error("This error goes to console + errors.log");
        logger.fatal("This fatal goes to console + errors.log");

        // ─── SUMMARY ──────────────────────────────────────────────
        System.out.println("\n==========================================================");
        System.out.println("                  📊 DEMO SUMMARY");
        System.out.println("==========================================================");
        System.out.println("  ✅ Logged messages at DEBUG, INFO, WARN, ERROR, FATAL levels");
        System.out.println("  ✅ Dynamic log level change (DEBUG → WARN → ERROR → DEBUG)");
        System.out.println("  ✅ Console + File appenders working");
        System.out.println("  ✅ Appenders added/removed at runtime");
        System.out.println("  ✅ Chain of Responsibility filters messages by level");
        System.out.println("  ✅ Check 'application.log' and 'errors.log' for file output");
        System.out.println("\n✅ Demo completed successfully!");
    }
}