package Strategy;

import entities.Directory;
import entities.FileSystemItem;

import java.util.ArrayList;
import java.util.List;

public class NameSearch implements SearchStrategy {

    @Override
    public List<FileSystemItem> search(Directory root, String criteria) {
        List<FileSystemItem> results = new ArrayList<>();
        searchRecursive(root, criteria.toLowerCase(), results);
        return results;
    }

    private void searchRecursive(Directory dir, String name, List<FileSystemItem> results) {
        for (FileSystemItem child : dir.getChildren()) {
            if (child.getName().toLowerCase().contains(name)) {
                results.add(child);
            }
            if (child.isDirectory()) {
                searchRecursive((Directory) child, name, results);
            }
        }
    }
}
