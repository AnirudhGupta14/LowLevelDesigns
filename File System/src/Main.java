import Observer.LoggingObserver;
import Services.FileSystemService;
import Strategy.ExtensionSearch;
import Strategy.NameSearch;
import Strategy.SizeSearch;
import entities.File;
import entities.FileSystemItem;
import entities.FileType;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // ─── 1. Initialize Service ────────────────────────────────────
        FileSystemService fs = FileSystemService.getInstance();

        // Register observer
        fs.addObserver(new LoggingObserver());

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   📁 FILE SYSTEM — DEMO");
        System.out.println("═══════════════════════════════════════════════\n");

        // ─── 2. Create Directory Structure ────────────────────────────
        System.out.println("── Creating Directories ────────────────────\n");

        fs.createDirectory("home");
        fs.createDirectory("home/anirudh");
        fs.createDirectory("home/anirudh/documents");
        fs.createDirectory("home/anirudh/pictures");
        fs.createDirectory("home/anirudh/projects");
        fs.createDirectory("home/anirudh/projects/java");
        fs.createDirectory("home/anirudh/projects/python");
        fs.createDirectory("var/log");

        // ─── 3. Create Files ──────────────────────────────────────────
        System.out.println("\n── Creating Files ──────────────────────────\n");

        fs.createFile("home/anirudh/documents", "resume", "pdf", FileType.DOCUMENT);
        fs.createFile("home/anirudh/documents", "notes", "txt", FileType.TEXT);
        fs.createFile("home/anirudh/documents", "budget", "xlsx", FileType.DOCUMENT);
        fs.createFile("home/anirudh/pictures", "vacation", "jpg", FileType.IMAGE);
        fs.createFile("home/anirudh/pictures", "profile", "png", FileType.IMAGE);
        fs.createFile("home/anirudh/projects/java", "Main", "java", FileType.CODE);
        fs.createFile("home/anirudh/projects/java", "Utils", "java", FileType.CODE);
        fs.createFile("home/anirudh/projects/python", "app", "py", FileType.CODE);
        fs.createFile("home/anirudh/projects/python", "requirements", "txt", FileType.TEXT);
        fs.createFile("var/log", "system", "log", FileType.TEXT);
        fs.createFile("var/log", "error", "log", FileType.TEXT);

        // ─── 4. Write Content to Files ────────────────────────────────
        System.out.println("\n── Writing Content ─────────────────────────\n");

        fs.writeToFile("home/anirudh/documents/resume",
                "Anirudh Gupta\nSoftware Engineer\n5+ years experience in Java, Python, and cloud technologies.\nSkills: Java, Spring Boot, Microservices, AWS, Docker, Kubernetes");
        fs.writeToFile("home/anirudh/documents/notes",
                "Meeting notes for project kickoff.\nAction items: Setup CI/CD pipeline, design database schema.");
        fs.writeToFile("home/anirudh/documents/budget",
                "Monthly budget spreadsheet data with detailed expense tracking across 12 months of financial records.");
        fs.writeToFile("home/anirudh/projects/java/Main",
                "public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}");
        fs.writeToFile("home/anirudh/projects/java/Utils",
                "public class Utils {\n    public static String format(String s) { return s.trim().toUpperCase(); }\n}");
        fs.writeToFile("home/anirudh/projects/python/app",
                "from flask import Flask\napp = Flask(__name__)\n\n@app.route('/')\ndef hello():\n    return 'Hello from Python!'");
        fs.writeToFile("home/anirudh/projects/python/requirements", "flask==2.3.0\nrequests==2.31.0");
        fs.writeToFile("var/log/system",
                "2026-03-02 10:00:00 INFO  System started successfully.\n2026-03-02 10:01:00 INFO  All services initialized.\n2026-03-02 10:05:00 WARN  Memory usage at 75%.");
        fs.writeToFile("var/log/error",
                "2026-03-02 10:02:00 ERROR Connection timeout to database.\n2026-03-02 10:03:00 ERROR Retry attempt 1 failed.");

        // ─── 5. Display Tree ──────────────────────────────────────────
        fs.displayTree();

        // ─── 6. Search by Name ────────────────────────────────────────
        System.out.println("── Search: By Name (\"app\") ─────────────────\n");

        List<FileSystemItem> nameResults = fs.search(new NameSearch(), "app");
        if (nameResults.isEmpty()) {
            System.out.println("  No results found.");
        } else {
            for (FileSystemItem item : nameResults) {
                System.out.printf("  ✅ Found: %s  (%d bytes)%n", item.getPath(), item.getSize());
            }
        }

        // ─── 7. Search by Extension ───────────────────────────────────
        System.out.println("\n── Search: By Extension (\"java\") ───────────\n");

        List<FileSystemItem> extResults = fs.search(new ExtensionSearch(), "java");
        if (extResults.isEmpty()) {
            System.out.println("  No results found.");
        } else {
            for (FileSystemItem item : extResults) {
                System.out.printf("  ✅ Found: %s  (%d bytes)%n", item.getPath(), item.getSize());
            }
        }

        // ─── 8. Search by Size ────────────────────────────────────────
        System.out.println("\n── Search: Files > 100 bytes ───────────────\n");

        List<FileSystemItem> sizeResults = fs.search(new SizeSearch(), "100");
        if (sizeResults.isEmpty()) {
            System.out.println("  No results found.");
        } else {
            for (FileSystemItem item : sizeResults) {
                System.out.printf("  ✅ Found: %s  (%d bytes)%n", item.getPath(), item.getSize());
            }
        }

        // ─── 9. Move a File ──────────────────────────────────────────
        System.out.println("\n── Moving File ─────────────────────────────\n");

        fs.move("home/anirudh/documents/notes", "home/anirudh/projects");

        // ─── 10. Delete a File ───────────────────────────────────────
        System.out.println("\n── Deleting Items ──────────────────────────\n");

        fs.delete("var/log/error");

        // ─── 11. Edge Cases ──────────────────────────────────────────
        System.out.println("\n── Edge Cases ──────────────────────────────\n");

        // Duplicate file
        System.out.println("  → Attempt to create duplicate file:");
        fs.createFile("home/anirudh/pictures", "vacation", "jpg", FileType.IMAGE);

        // File not found
        System.out.println("\n  → Attempt to write to non-existent file:");
        fs.writeToFile("home/anirudh/nonexistent", "test");

        // Delete non-existent
        System.out.println("\n  → Attempt to delete non-existent path:");
        fs.delete("home/anirudh/ghost");

        // ─── 12. Final State ─────────────────────────────────────────
        fs.displayTree();

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("   ✅ FILE SYSTEM DEMO COMPLETE");
        System.out.println("═══════════════════════════════════════════════");
    }
}