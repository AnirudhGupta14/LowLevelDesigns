package Strategy;

import entities.Directory;
import entities.File;
import entities.FileSystemItem;

import java.util.ArrayList;
import java.util.List;

public class SizeSearch implements SearchStrategy {

    @Override
    public List<FileSystemItem> search(Directory root, String criteria) {
        long threshold = Long.parseLong(criteria);
        List<FileSystemItem> results = new ArrayList<>();
        searchRecursive(root, threshold, results);
        return results;
    }

    private void searchRecursive(Directory dir, long threshold, List<FileSystemItem> results) {
        for (FileSystemItem child : dir.getChildren()) {
            if (!child.isDirectory()) {
                if (child.getSize() > threshold) {
                    results.add(child);
                }
            } else {
                searchRecursive((Directory) child, threshold, results);
            }
        }
    }
}
