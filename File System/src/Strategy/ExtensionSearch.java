package Strategy;

import entities.Directory;
import entities.File;
import entities.FileSystemItem;

import java.util.ArrayList;
import java.util.List;

public class ExtensionSearch implements SearchStrategy {

    @Override
    public List<FileSystemItem> search(Directory root, String criteria) {
        List<FileSystemItem> results = new ArrayList<>();
        searchRecursive(root, criteria.toLowerCase(), results);
        return results;
    }

    private void searchRecursive(Directory dir, String extension, List<FileSystemItem> results) {
        for (FileSystemItem child : dir.getChildren()) {
            if (!child.isDirectory()) {
                File file = (File) child;
                if (file.getExtension().toLowerCase().equals(extension)) {
                    results.add(file);
                }
            } else {
                searchRecursive((Directory) child, extension, results);
            }
        }
    }
}
