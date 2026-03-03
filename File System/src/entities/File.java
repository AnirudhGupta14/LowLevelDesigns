package entities;

public class File extends FileSystemItem {

    private String content;
    private final String extension;
    private final FileType fileType;

    public File(String name, String extension, FileType fileType) {
        super(name);
        this.extension = extension;
        this.fileType = fileType;
        this.content = "";
    }

    // ─── Getters ──────────────────────────────────────────────

    public String getContent() {
        return content;
    }

    public String getExtension() {
        return extension;
    }

    public FileType getFileType() {
        return fileType;
    }

    // ─── Content Operations ───────────────────────────────────

    public void setContent(String content) {
        this.content = content;
    }

    public void appendContent(String text) {
        this.content += text;
    }

    // ─── Composite Methods ────────────────────────────────────

    @Override
    public long getSize() {
        return content.length();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public void display(String indent) {
        System.out.printf("%s📄 %s.%s  (%d bytes, %s)%n",
                indent, getName(), extension, getSize(), fileType);
    }

    @Override
    public String toString() {
        return String.format("📄 %s.%s (%d bytes)", getName(), extension, getSize());
    }
}
