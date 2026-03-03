package Services;

import Observer.FileChangeObserver;
import Observer.FileChangeSubject;
import Strategy.SearchStrategy;
import entities.Directory;
import entities.File;
import entities.FileSystemItem;
import entities.FileType;

import java.util.ArrayList;
import java.util.List;

public class FileSystemService implements FileChangeSubject {

    private static FileSystemService instance;

    private final Directory root;
    private final List<FileChangeObserver> observers;

    private FileSystemService() {
        this.root = new Directory("root");
        this.observers = new ArrayList<>();
    }

    public static synchronized FileSystemService getInstance() {
        if (instance == null) {
            instance = new FileSystemService();
        }
        return instance;
    }

    // ─── Observer Management ──────────────────────────────────

    @Override
    public void addObserver(FileChangeObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(FileChangeObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyCreated(FileSystemItem item) {
        for (FileChangeObserver observer : observers) {
            observer.onFileCreated(item);
        }
    }

    @Override
    public void notifyDeleted(FileSystemItem item) {
        for (FileChangeObserver observer : observers) {
            observer.onFileDeleted(item);
        }
    }

    @Override
    public void notifyModified(FileSystemItem item) {
        for (FileChangeObserver observer : observers) {
            observer.onFileModified(item);
        }
    }

    // ─── Directory Operations ─────────────────────────────────

    public void createDirectory(String path) {
        String[] parts = path.split("/");
        Directory current = root;

        for (String part : parts) {
            if (part.isEmpty())
                continue;

            FileSystemItem existing = current.getChild(part);
            if (existing != null && existing.isDirectory()) {
                current = (Directory) existing;
            } else if (existing != null) {
                System.out.printf("  ❌ \"%s\" exists as a file, cannot create directory%n", part);
                return;
            } else {
                Directory newDir = new Directory(part);
                current.addChild(newDir);
                current = newDir;
                notifyCreated(newDir);
            }
        }
    }

    // ─── File Operations ──────────────────────────────────────

    public File createFile(String dirPath, String name, String extension, FileType type) {
        Directory dir = navigateToDirectory(dirPath);
        if (dir == null) {
            System.out.printf("  ❌ Directory \"%s\" not found%n", dirPath);
            return null;
        }

        // Check duplicate
        if (dir.getChild(name) != null) {
            System.out.printf("  ❌ \"%s\" already exists in \"%s\"%n", name, dir.getName());
            return null;
        }

        File file = new File(name, extension, type);
        dir.addChild(file);
        notifyCreated(file);
        return file;
    }

    public void writeToFile(String filePath, String content) {
        FileSystemItem item = navigateToItem(filePath);
        if (item == null) {
            System.out.printf("  ❌ File \"%s\" not found%n", filePath);
            return;
        }
        if (item.isDirectory()) {
            System.out.printf("  ❌ \"%s\" is a directory, cannot write%n", filePath);
            return;
        }

        File file = (File) item;
        file.setContent(content);
        notifyModified(file);
    }

    // ─── Delete ───────────────────────────────────────────────

    public void delete(String path) {
        FileSystemItem item = navigateToItem(path);
        if (item == null) {
            System.out.printf("  ❌ \"%s\" not found%n", path);
            return;
        }

        Directory parent = item.getParent();
        if (parent == null) {
            System.out.println("  ❌ Cannot delete root directory");
            return;
        }

        parent.removeChild(item.getName());
        notifyDeleted(item);
        System.out.printf("  🗑️  Deleted: %s%n", item.getPath());
    }

    // ─── Move ─────────────────────────────────────────────────

    public void move(String sourcePath, String destDirPath) {
        FileSystemItem item = navigateToItem(sourcePath);
        if (item == null) {
            System.out.printf("  ❌ Source \"%s\" not found%n", sourcePath);
            return;
        }

        Directory destDir = navigateToDirectory(destDirPath);
        if (destDir == null) {
            System.out.printf("  ❌ Destination \"%s\" not found%n", destDirPath);
            return;
        }

        Directory parent = item.getParent();
        if (parent == null) {
            System.out.println("  ❌ Cannot move root directory");
            return;
        }

        // Check duplicate in destination
        if (destDir.getChild(item.getName()) != null) {
            System.out.printf("  ❌ \"%s\" already exists in \"%s\"%n", item.getName(), destDir.getName());
            return;
        }

        parent.removeChild(item.getName());
        destDir.addChild(item);
        System.out.printf("  📦 Moved: %s → %s%n", sourcePath, item.getPath());
    }

    // ─── Search ───────────────────────────────────────────────

    public List<FileSystemItem> search(SearchStrategy strategy, String criteria) {
        return strategy.search(root, criteria);
    }

    // ─── Display ──────────────────────────────────────────────

    public void displayTree() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    📁 FILE SYSTEM TREE                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");
        root.display("  ");
        System.out.printf("%n  Total size: %d bytes%n%n", root.getSize());
    }

    // ─── Navigation Helpers ───────────────────────────────────

    public Directory getRoot() {
        return root;
    }

    private Directory navigateToDirectory(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return root;
        }

        String[] parts = path.split("/");
        Directory current = root;

        for (String part : parts) {
            if (part.isEmpty())
                continue;

            FileSystemItem child = current.getChild(part);
            if (child == null || !child.isDirectory()) {
                return null;
            }
            current = (Directory) child;
        }
        return current;
    }

    private FileSystemItem navigateToItem(String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }

        String[] parts = path.split("/");
        Directory current = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty())
                continue;

            FileSystemItem child = current.getChild(part);
            if (child == null) {
                return null;
            }

            if (i == parts.length - 1) {
                return child;
            }

            if (!child.isDirectory()) {
                return null;
            }
            current = (Directory) child;
        }
        return current;
    }
}
