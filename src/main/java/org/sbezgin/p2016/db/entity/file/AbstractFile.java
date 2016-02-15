package org.sbezgin.p2016.db.entity.file;

import javax.persistence.*;

@javax.persistence.TableGenerator(
        name="FILE_GEN",
        table="AbstractFile_GENERATOR_TABLE",
        pkColumnName = "key_gen",
        valueColumnName = "hi",
        pkColumnValue="FILE_GEN",
        allocationSize=20
)

@Entity
@Table(name = "file")
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractFile {
    private long id;
    private String name;
    private String path;
    private String idPath;
    private long parentId;
    private String className;
    private int ownerID;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="FILE_GEN")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "path", nullable = false)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Column(name = "id_path", nullable = false)
    public String getIdPath() {
        return idPath;
    }

    public void setIdPath(String idPath) {
        this.idPath = idPath;
    }

    @Column(name = "parent_id", nullable = false)
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Column(name = "class", nullable = false)
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Column(name = "owner_id", nullable = false)
    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractFile that = (AbstractFile) o;

        if (id != that.id) return false;
        if (ownerID != that.ownerID) return false;
        if (parentId != that.parentId) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (idPath != null ? !idPath.equals(that.idPath) : that.idPath != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (idPath != null ? idPath.hashCode() : 0);
        result = 31 * result + (int) (parentId ^ (parentId >>> 32));
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + ownerID;
        return result;
    }
}
