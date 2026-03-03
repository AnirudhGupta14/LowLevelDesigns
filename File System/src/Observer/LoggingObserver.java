package Observer;

import entities.FileSystemItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingObserver implements FileChangeObserver {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void onFileCreated(FileSystemItem item) {
        System.out.printf("  🔔 [%s] CREATED → %s%n", now(), item.getPath());
    }

    @Override
    public void onFileDeleted(FileSystemItem item) {
        System.out.printf("  🔔 [%s] DELETED → %s%n", now(), item.getPath());
    }

    @Override
    public void onFileModified(FileSystemItem item) {
        System.out.printf("  🔔 [%s] MODIFIED → %s%n", now(), item.getPath());
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
