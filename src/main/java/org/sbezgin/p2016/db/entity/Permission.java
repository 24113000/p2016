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
    private Boolean del;
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

    @Column(name = "del", nullable = true)
    public Boolean getDel() {
        return del;
    }

    public void setDel(Boolean del) {
        this.del = del;
    }

    @Column(name = "user_id", nullable = false)
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    public AbstractFile getAbstractFile() {
        return abstractFile;
    }

    public void setAbstractFile(AbstractFile abstractFile) {
        this.abstractFile = abstractFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        if (abstractFile != null ? !abstractFile.equals(that.abstractFile) : that.abstractFile != null) return false;
        if (del != null ? !del.equals(that.del) : that.del != null) return false;
        if (read != null ? !read.equals(that.read) : that.read != null) return false;
        if (userID != null ? !userID.equals(that.userID) : that.userID != null) return false;
        if (write != null ? !write.equals(that.write) : that.write != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = abstractFile != null ? abstractFile.hashCode() : 0;
        result = 31 * result + (read != null ? read.hashCode() : 0);
        result = 31 * result + (write != null ? write.hashCode() : 0);
        result = 31 * result + (del != null ? del.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        return result;
    }
}
