package Observer;

import entities.FileSystemItem;

public interface FileChangeObserver {
    void onFileCreated(FileSystemItem item);

    void onFileDeleted(FileSystemItem item);

    void onFileModified(FileSystemItem item);
}
