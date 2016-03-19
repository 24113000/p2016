package org.sbezgin.p2016.db.entity;

import org.sbezgin.p2016.db.entity.file.AbstractFile;

import javax.persistence.*;


@javax.persistence.TableGenerator(
        name="PERM_GEN",
        table="Permission_GENERATOR_TABLE",
        pkColumnName = "key_gen",
        valueColumnName = "hi",
        pkColumnValue="PERM_GEN",
        allocationSize=20
)


@Entity
@Table(name = "Permission")
public class Permission {
    private Long id;
    private AbstractFile abstractFile;
    private Boolean read;
    private Boolean write;
    private Boolean delete;
    private Long userID;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="PERM_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "read", nullable = true)
    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    @Column(name = "write", nullable = true)
    public Boolean getWrite() {
        return write;
    }


    public void setWrite(Boolean write) {
        this.write = write;
    }

    @Column(name = "delete", nullable = true)
    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    @Column(name = "user_id", nullable = false)
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", nullable = false)
    public AbstractFile getAbstractFile() {
        return abstractFile;
    }

    public void setAbstractFile(AbstractFile abstractFile) {
        this.abstractFile = abstractFile;
    }
}
