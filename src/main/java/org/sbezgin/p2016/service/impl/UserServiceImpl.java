package org.sbezgin.p2016.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dao.UserDAO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.security.UserRoles;
import org.sbezgin.p2016.service.UserService;
import org.sbezgin.p2016.service.transformer.BeanTransformer;
import org.sbezgin.p2016.service.transformer.BeanTransformerHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class UserServiceImpl implements UserService, UserDetailsService {

    private UserDAO userDAO;
    private BeanTransformerHolder beanTransformerHolder;

    @Override
    public UserDTO getCurrentUser() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        if (principal != null && StringUtils.isNotEmpty(principal.getUsername())) {
            UserDTO userDTO = getUserByEmail(principal.getUsername());
            if (userDTO == null) {
                throw new P2016Exception("Cannot find user by email: " + principal.getUsername());
            }
            return userDTO;
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = getUserByEmail(username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return UserRoles.ROLE_USER.toString();
            }
        });

        return new org.springframework.security.core.userdetails.User(
                username,
                userDTO.getPassword(),
                authorities//TODO java8
        );
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
