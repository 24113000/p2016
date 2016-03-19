package org.sbezgin.p2016.db.dto;

import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;

public class PermissionDTO {
    private Long id;
    private AbstractFileDTO fileDTO;
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

    public AbstractFileDTO getFileDTO() {
        return fileDTO;
    }

    public void setFileDTO(AbstractFileDTO fileDTO) {
        this.fileDTO = fileDTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionDTO that = (PermissionDTO) o;

        if (delete != null ? !delete.equals(that.delete) : that.delete != null) return false;
        if (fileDTO != null ? !fileDTO.equals(that.fileDTO) : that.fileDTO != null) return false;
        if (read != null ? !read.equals(that.read) : that.read != null) return false;
        if (userID != null ? !userID.equals(that.userID) : that.userID != null) return false;
        if (write != null ? !write.equals(that.write) : that.write != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 + (fileDTO != null ? fileDTO.hashCode() : 0);
        result = 31 * result + (read != null ? read.hashCode() : 0);
        result = 31 * result + (write != null ? write.hashCode() : 0);
        result = 31 * result + (delete != null ? delete.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        return result;
    }
}
