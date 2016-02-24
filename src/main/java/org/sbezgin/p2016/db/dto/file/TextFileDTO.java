package org.sbezgin.p2016.db.dto.file;

import org.sbezgin.p2016.common.FileType;

public class TextFileDTO extends AbstractFileDTO {
    private FileType type;

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }
}
