package Observer;

import entities.FileSystemItem;

public interface FileChangeSubject {
    void addObserver(FileChangeObserver observer);

    void removeObserver(FileChangeObserver observer);

    void notifyCreated(FileSystemItem item);

    void notifyDeleted(FileSystemItem item);

    void notifyModified(FileSystemItem item);
}
