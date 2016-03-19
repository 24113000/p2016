package org.sbezgin.p2016.service.impl;

import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dao.UserDAO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.service.transformer.BeanTransformer;
import org.sbezgin.p2016.service.transformer.BeanTransformerHolder;
import org.sbezgin.p2016.service.UserService;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private BeanTransformerHolder beanTransformerHolder;

    @Override
    public UserDTO getCurrentUser() {
        UserDTO user = new UserDTO();
        user.setId(1L);
        return user;
    }

    @Override
    public UserDTO getUser(Long id) {
        User user = userDAO.getUser(id);
        if (user != null) {
            return convertUserToDTO(user);
        }
        return null;
    }

    private UserDTO convertUserToDTO(User user) {
        BeanTransformer transformer = beanTransformerHolder.getTransformer(user.getClass().getCanonicalName());
        return (UserDTO) transformer.transformEntityToDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            return convertUserToDTO(user);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        User user = userDAO.getUser(id);
        if (user != null) {
            userDAO.delete(user);
            return;
        }
        throw new P2016Exception("Cannot delete user. User not found id: " + id);
    }

    @Override
    public void save(UserDTO userDTO) {
        BeanTransformer transformer = beanTransformerHolder.getTransformer(userDTO.getClass().getCanonicalName());
        if (userDTO.getId() == null) {
            userDAO.save((User)transformer.transformDTOToEntity(userDTO));
        } else {
            User user = userDAO.getUser(userDTO.getId());
            transformer.copyFieldsToEntity(userDTO, user);
            userDAO.save(user);
        }
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public BeanTransformerHolder getBeanTransformerHolder() {
        return beanTransformerHolder;
    }

    public void setBeanTransformerHolder(BeanTransformerHolder beanTransformerHolder) {
        this.beanTransformerHolder = beanTransformerHolder;
    }
}
