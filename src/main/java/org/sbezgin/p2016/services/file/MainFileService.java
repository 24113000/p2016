package org.sbezgin.p2016.services.file;

import org.sbezgin.p2016.db.dto.file.Folder;
import org.sbezgin.p2016.db.dto.Permission;
import org.sbezgin.p2016.db.dto.file.AbstractFile;

import java.util.List;

public interface MainFileService {
    AbstractFile getFileByID(long resID);

    Folder getFolder(long folderID);

    void saveFile(AbstractFile res);

    void setPermission(long resID, Permission perm);

    void renameFile(long resID, String newName);

    void deleteFile(long resID, boolean recursively);

    List<AbstractFile> getRootFiles();

    List<AbstractFile> getChildren(long folderID);

    List<AbstractFile> getFilesByType(String javaType);
}
