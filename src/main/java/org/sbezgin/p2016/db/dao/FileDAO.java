package org.sbezgin.p2016.db.dao;


import org.sbezgin.p2016.db.entity.Permission;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;

import java.util.List;

public interface FileDAO {
    AbstractFile getFileByID(Long userID, long fileID);

    List<AbstractFile> getFilesByName(Long userID, String folderPath, String fileName);

    Folder getFolder(Long userID, long folderID);

    void saveOrUpdateFile(Long userID, AbstractFile file);

    int deleteFile(Long userID, long fileID);

    List<AbstractFile> getRootFiles(Long ownerID);

    List<AbstractFile> getChildren(Long userID, long folderID, int start, int end);

    List<AbstractFile> getFilesByIDs(Long userID, List<Long> idList);

    List<AbstractFile> getAllChildren(Long currentUserId, long fileID);

    Permission getUserFilePermission(Long fileID, Long useID);

    void removePermission(Long fileID, Long userId);

    List<AbstractFile> getUnsecuredAllChildren(long fileID);
}
