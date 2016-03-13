package org.sbezgin.p2016.db.dao;

import org.sbezgin.p2016.db.entity.User;

public interface UserDAO {
    User getUser(Long id);

    User getUserByEmail(String email);

    void delete(User user);

    void save(User user);
}
