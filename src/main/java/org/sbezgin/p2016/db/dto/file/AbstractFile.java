package org.sbezgin.p2016.db.dto.file;

public abstract class AbstractFile {
    private long id;
    private String name;
    private String path;
    private String idPath;
    private String type;

    public boolean isFolder() {
        return this.getClass().getCanonicalName().equals(SimpleFolder.class.getCanonicalName());
    }
}
