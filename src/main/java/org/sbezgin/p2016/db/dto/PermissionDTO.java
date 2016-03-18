package org.sbezgin.p2016.db.dto;

public class PermissionDTO {
    private Long id;
    private Long fileID;
    private Boolean read;
    private Boolean write;
    private Boolean delete;
    private Long userID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileID() {
        return fileID;
    }

    public void setFileID(Long fileID) {
        this.fileID = fileID;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getWrite() {
        return write;
    }


    public void setWrite(Boolean write) {
        this.write = write;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }


    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionDTO that = (PermissionDTO) o;

        if (delete != null ? !delete.equals(that.delete) : that.delete != null) return false;
        if (fileID != null ? !fileID.equals(that.fileID) : that.fileID != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (read != null ? !read.equals(that.read) : that.read != null) return false;
        if (userID != null ? !userID.equals(that.userID) : that.userID != null) return false;
        if (write != null ? !write.equals(that.write) : that.write != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fileID != null ? fileID.hashCode() : 0);
        result = 31 * result + (read != null ? read.hashCode() : 0);
        result = 31 * result + (write != null ? write.hashCode() : 0);
        result = 31 * result + (delete != null ? delete.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        return result;
    }
}
