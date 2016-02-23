package org.sbezgin.p2016.services.impl;

import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.services.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public UserDTO getCurrentUser() {
        UserDTO user = new UserDTO();
        user.setId(1);
        return user;
    }
}
