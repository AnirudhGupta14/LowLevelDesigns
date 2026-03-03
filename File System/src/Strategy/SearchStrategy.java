package Strategy;

import entities.Directory;
import entities.FileSystemItem;

import java.util.List;

public interface SearchStrategy {
    List<FileSystemItem> search(Directory root, String criteria);
}
