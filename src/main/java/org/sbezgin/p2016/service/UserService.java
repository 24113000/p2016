package org.sbezgin.p2016.service;

import org.sbezgin.p2016.db.dto.UserDTO;

public interface UserService {
    UserDTO getCurrentUser();

    UserDTO getUser(Long id);

    UserDTO getUserByEmail(String email);

    void delete(Long id);

    void save(UserDTO userDTO);
}
