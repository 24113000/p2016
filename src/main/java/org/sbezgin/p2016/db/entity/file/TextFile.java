package org.sbezgin.p2016.db.entity.file;

import org.sbezgin.p2016.common.FileType;

import javax.persistence.*;

@Entity
@Table(name = "text_file")
@PrimaryKeyJoinColumn(name="id")
public class TextFile extends AbstractFile {
    private FileType type;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }
}
