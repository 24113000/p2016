package org.sbezgin.p2016.rest;

import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileOperationRest {

    @Autowired
    private FileService fileService;

    public String getFile(long fileID) {
        AbstractFileDTO file = fileService.getFileByID(fileID);
        if (file != null) {

        }
        return null;
    }

    public String getFolderChildren(){
        return null;
    }

    public String getFileContent() {
        return null;
    }

    public void saveFileContent() {

    }

    public void saveFolder() {

    }

    public void deleteFile() {

    }

    public void renameFile() {

    }

}
