package org.sbezgin.p2016.services.file;

import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;

import java.util.List;

public interface FileService {
    AbstractFileDTO getFileByID(long fileID);

    FolderDTO getFolder(long folderID);

    void saveFile(AbstractFileDTO file);

    void setPermission(long fileD, PermissionDTO perm);

    void renameFile(long fileID, String newName);

    void deleteFile(long fileID, boolean recursively);

    List<AbstractFileDTO> getRootFiles();

    List<AbstractFileDTO> getChildren(long folderID);

    List<AbstractFileDTO> getFilesByType(String javaType);
}
