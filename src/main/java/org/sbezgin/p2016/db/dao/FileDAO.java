package org.sbezgin.p2016.db.dao;


import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.SimpleFolder;

import java.util.List;

public interface FileDAO {
    AbstractFile getFileByID(User user, long fileID);

    SimpleFolder getFolder(User user, long folderID);

    void saveOrUpdateFile(User user, AbstractFile file);

    void saveOrUpdateFiles(User user, List<AbstractFile> files);

    void deleteFile(User user, long fileID, boolean recursively);

    List<AbstractFile> getRootFiles(User user);

    List<AbstractFile> getChildren(User user, long folderID);

    List<AbstractFile> getFilesByType(User user, String javaType);
}
