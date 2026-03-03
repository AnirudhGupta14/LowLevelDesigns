package entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class FileSystemItem {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final String name;
    private final LocalDateTime createdAt;
    private Directory parent;

    public FileSystemItem(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.parent = null;
    }

    // ─── Getters ──────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Directory getParent() {
        return parent;
    }

    // ─── Setters ──────────────────────────────────────────────

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    // ─── Path ─────────────────────────────────────────────────

    public String getPath() {
        if (parent == null) {
            return "/" + name;
        }
        String parentPath = parent.getPath();
        return parentPath.equals("/") ? "/" + name : parentPath + "/" + name;
    }

    // ─── Abstract ─────────────────────────────────────────────

    public abstract long getSize();

    public abstract boolean isDirectory();

    public abstract void display(String indent);

    // ─── Display ──────────────────────────────────────────────

    public String getFormattedDate() {
        return createdAt.format(FMT);
    }

    @Override
    public String toString() {
        return getPath();
    }
}
