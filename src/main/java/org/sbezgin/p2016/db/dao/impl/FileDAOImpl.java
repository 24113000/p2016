package org.sbezgin.p2016.db.dao.impl;

import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;

import java.util.List;

public class FileDAOImpl implements FileDAO {
    @Override
    public AbstractFile getFileByID(User user, long fileID) {
        return null;
    }

    @Override
    public Folder getFolder(User user, long folderID) {
        return null;
    }

    @Override
    public void saveOrUpdateFile(User user, AbstractFile file) {

    }

    @Override
    public void saveOrUpdateFiles(User user, List<AbstractFile> files) {

    }

    @Override
    public void deleteFile(User user, long fileID, boolean recursively) {

    }

    @Override
    public List<AbstractFile> getRootFiles(User user) {
        return null;
    }

    @Override
    public List<AbstractFile> getChildren(User user, long folderID) {
        return null;
    }

    @Override
    public List<AbstractFile> getFilesByType(User user, String javaType) {
        return null;
    }
}
