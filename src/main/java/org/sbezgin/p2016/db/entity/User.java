package org.sbezgin.p2016.db.entity;

import javax.persistence.*;
import java.util.Date;

@javax.persistence.TableGenerator(
        name="USER_GEN",
        table="USER_GENERATOR_TABLE",
        pkColumnName = "key_gen",
        valueColumnName = "hi",
        pkColumnValue="USER_GEN",
        allocationSize=20
)

@Entity
@Table(name = "User")
public class User {
    private int id;
    private String email;
    private String password;
    private String resetToken;
    private Date expirationTokenDate;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator="USER_GEN")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "email", nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "reset_token", nullable = false)
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    @Column(name = "token_expiration_date", nullable = false)
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

        User user = (User) o;

        if (id != user.id) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
