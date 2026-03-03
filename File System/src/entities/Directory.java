package entities;

import java.util.ArrayList;
import java.util.List;

public class Directory extends FileSystemItem {

    private final List<FileSystemItem> children;

    public Directory(String name) {
        super(name);
        this.children = new ArrayList<>();
    }

    // ─── Child Management ─────────────────────────────────────

    public void addChild(FileSystemItem item) {
        // Check for duplicate names
        for (FileSystemItem child : children) {
            if (child.getName().equals(item.getName())) {
                System.out.printf("  ❌ Item \"%s\" already exists in directory \"%s\"%n",
                        item.getName(), getName());
                return;
            }
        }
        item.setParent(this);
        children.add(item);
    }

    public void removeChild(String name) {
        children.removeIf(child -> child.getName().equals(name));
    }

    public FileSystemItem getChild(String name) {
        for (FileSystemItem child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public List<FileSystemItem> getChildren() {
        return new ArrayList<>(children);
    }

    public int getChildCount() {
        return children.size();
    }

    // ─── Composite Methods ────────────────────────────────────

    @Override
    public long getSize() {
        long total = 0;
        for (FileSystemItem child : children) {
            total += child.getSize();
        }
        return total;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public void display(String indent) {
        System.out.printf("%s📁 %s/  (%d items, %d bytes)%n",
                indent, getName(), children.size(), getSize());
        for (int i = 0; i < children.size(); i++) {
            boolean isLast = (i == children.size() - 1);
            String connector = isLast ? "└── " : "├── ";
            String childIndent = indent + (isLast ? "    " : "│   ");
            FileSystemItem child = children.get(i);
            if (child.isDirectory()) {
                System.out.printf("%s%s", indent, connector);
                child.display(childIndent);
            } else {
                System.out.printf("%s%s", indent, connector);
                child.display("");
            }
        }
    }

    @Override
    public String toString() {
        return String.format("📁 %s/ (%d items)", getName(), children.size());
    }
}
