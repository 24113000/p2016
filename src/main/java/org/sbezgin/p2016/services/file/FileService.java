package org.sbezgin.p2016.services.file;

import org.sbezgin.p2016.db.dto.file.SimpleFolder;
import org.sbezgin.p2016.db.dto.Permission;
import org.sbezgin.p2016.db.dto.file.AbstractFile;

import java.util.List;

public interface FileService {
    AbstractFile getFileByID(long fileID);

    SimpleFolder getFolder(long folderID);

    void saveFile(AbstractFile file);

    void setPermission(long fileD, Permission perm);

    void renameFile(long fileID, String newName);

    void deleteFile(long fileID, boolean recursively);

    List<AbstractFile> getRootFiles();

    List<AbstractFile> getChildren(long folderID);

    List<AbstractFile> getFilesByType(String javaType);
}
