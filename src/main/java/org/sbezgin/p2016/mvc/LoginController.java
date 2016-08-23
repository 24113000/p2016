package org.sbezgin.p2016.mvc;

import org.apache.commons.collections.CollectionUtils;
import org.sbezgin.p2016.security.UserRoles;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class LoginController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User principal = getUser();
        boolean isUserAuth = isUserAuthenticated(principal);

        if (isUserAuth) {
            return new ModelAndView("redirect:/main.html");
        }

        return new ModelAndView("login");
    }

    private boolean isUserAuthenticated(User user) {
        if (user != null) {
            Collection<GrantedAuthority> authorities = user.getAuthorities();
            if (CollectionUtils.isNotEmpty(authorities)) {
                for (GrantedAuthority authority : authorities) {
                    if (UserRoles.ROLE_USER.toString().equals(authority.getAuthority())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getClass().equals(UsernamePasswordAuthenticationToken.class)) {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
            return (User) auth.getPrincipal();
        }
        return null;
    }
}
