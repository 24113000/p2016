package org.sbezgin.p2016.db.entity.file;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;


@Entity
@Table(name = "FILE_CONTENT")
public class FileContent {
    private Long id;
    private byte[] data;
    private AbstractFile file;

    @GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "file"))
    @Id
    @GeneratedValue(generator = "generator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "data", nullable = false)
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @OneToOne(mappedBy = "fileContent")
    public AbstractFile getFile() {
        return file;
    }

    public void setFile(AbstractFile file) {
        this.file = file;
    }
}
