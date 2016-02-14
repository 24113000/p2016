package org.sbezgin.p2016.services.file.impl;

import org.sbezgin.p2016.db.dto.Permission;
import org.sbezgin.p2016.db.dto.file.AbstractFile;
import org.sbezgin.p2016.db.dto.file.SimpleFolder;
import org.sbezgin.p2016.services.file.FileService;

import java.util.List;

public class FileServiceImpl implements FileService {

    @Override
    public AbstractFile getFileByID(long fileID) {
        return null;
    }

    @Override
    public SimpleFolder getFolder(long folderID) {
        return null;
    }

    @Override
    public void saveFile(AbstractFile file) {

    }

    @Override
    public void setPermission(long fileD, Permission perm) {

    }

    @Override
    public void renameFile(long fileID, String newName) {

    }

    @Override
    public void deleteFile(long fileID, boolean recursively) {

    }

    @Override
    public List<AbstractFile> getRootFiles() {
        return null;
    }

    @Override
    public List<AbstractFile> getChildren(long folderID) {
        return null;
    }

    @Override
    public List<AbstractFile> getFilesByType(String javaType) {
        return null;
    }

}
