package org.sbezgin.p2016.db.entity.file;

import org.sbezgin.p2016.common.FileType;

public abstract class AbstractFile {
    private long id;
    private String name;
    private String path;
    private String idPath;
    private String className;
    private FileType fileType;

    public boolean isFolder() {
        return FileType.FOLDER == fileType;
    }
}
