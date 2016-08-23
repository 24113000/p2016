package org.sbezgin.p2016.mvc;

import org.sbezgin.p2016.service.UserService;
import org.sbezgin.p2016.service.file.FileService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainPageController extends AbstractController {

    private FileService fileService;
    private UserService userService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        System.out.println("TEST TEST TEST TEST " + userService.getCurrentUser().getEmail());

        ModelAndView mv = new ModelAndView();
        mv.setViewName("main");
        return mv;
    }

    public FileService getFileService() {
        return fileService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
