package org.sbezgin.p2016.mvc;

import org.sbezgin.p2016.common.CommonConstants;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.service.file.FileService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class P2016MainController extends AbstractController {

    private FileService fileService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        ModelAndView mv = new ModelAndView();
        FolderDTO simpleFolder = new FolderDTO();
        simpleFolder.setName(CommonConstants.ROOT_FOLDER_NAME);
        simpleFolder.setParentId(null);
        simpleFolder.setIdPath("/");
        simpleFolder.setPath("/");

        fileService.saveFile(simpleFolder);

        mv.setViewName("hello");
        return mv;
    }

    public FileService getFileService() {
        return fileService;
    }

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
}
