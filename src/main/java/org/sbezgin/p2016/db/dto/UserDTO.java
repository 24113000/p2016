package org.sbezgin.p2016.db.dto;

import java.util.Date;

public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private String resetToken;
    private Date expirationTokenDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Date getExpirationTokenDate() {
        return expirationTokenDate;
    }

    public void setExpirationTokenDate(Date expirationTokenDate) {
        this.expirationTokenDate = expirationTokenDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (email != null ? !email.equals(userDTO.email) : userDTO.email != null) return false;
        if (id != null ? !id.equals(userDTO.id) : userDTO.id != null) return false;
        if (password != null ? !password.equals(userDTO.password) : userDTO.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
