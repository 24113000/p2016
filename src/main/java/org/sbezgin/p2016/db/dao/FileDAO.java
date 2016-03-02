package org.sbezgin.p2016.db.dao;


import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;

import java.util.List;

public interface FileDAO {
    AbstractFile getFileByID(int userID, long fileID);

    AbstractFile getFileByName(int userID, String folderPath, String fileName);

    Folder getFolder(int userID, long folderID);

    void saveOrUpdateFile(int userID, AbstractFile file);

    void saveOrUpdateFiles(int userID, List<AbstractFile> files);

    void deleteFile(int userID, long fileID, boolean recursively);

    List<AbstractFile> getRootFiles(int ownerID);

    List<AbstractFile> getChildren(int userID, long folderID, int start, int end);

    List<AbstractFile> getFilesByType(int userID, String javaType);
}
