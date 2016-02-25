package org.sbezgin.p2016.db.dto.file;

import org.sbezgin.p2016.common.FileType;

public class TextFileDTO extends AbstractFileDTO {
    private FileType type;
    private TextFileContentDTO fileContent;
    private boolean hasFileContent = false;

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public void setFileContent(TextFileContentDTO fileContent) {
        this.fileContent = fileContent;
    }

    public TextFileContentDTO getFileContent() {
        return fileContent;
    }

    public boolean isHasFileContent() {
        return hasFileContent;
    }

    public void setHasFileContent(boolean hasFileContent) {
        this.hasFileContent = hasFileContent;
    }
}
