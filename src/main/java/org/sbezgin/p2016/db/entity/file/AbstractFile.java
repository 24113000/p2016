package org.sbezgin.p2016.db.entity.file;

import org.sbezgin.p2016.db.entity.Permission;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
@Inheritance(strategy= InheritanceType.JOINED)
public abstract class AbstractFile {
    private Long id;
    private String name;
    private String path;
    private String idPath;
    private Long parentId;
    private String className;
    private Long ownerID;
    private FileContent fileContent;
    private List<Permission> permissions;
    private Date createDate;
    private Date updateDate;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="FILE_GEN")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Column(name = "parent_id", nullable = true)
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
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
    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id", nullable = true)
    public FileContent getFileContent() {
        return fileContent;
    }

    public void setFileContent(FileContent fileContent) {
        this.fileContent = fileContent;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file_id")
    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Column(name = "create_date", nullable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "update_date", nullable = false)
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractFile that = (AbstractFile) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (idPath != null ? !idPath.equals(that.idPath) : that.idPath != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (ownerID != null ? !ownerID.equals(that.ownerID) : that.ownerID != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (idPath != null ? idPath.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (ownerID != null ? ownerID.hashCode() : 0);
        return result;
    }
}
