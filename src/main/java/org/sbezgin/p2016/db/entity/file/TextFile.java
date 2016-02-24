package org.sbezgin.p2016.db.entity.file;

import org.sbezgin.p2016.common.FileType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "text_file")
@PrimaryKeyJoinColumn(name="id")
public class TextFile extends AbstractFile {
    private FileType type;

    @Column(name = "type", nullable = false)
    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }
}
