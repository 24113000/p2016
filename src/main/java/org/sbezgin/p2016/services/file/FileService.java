package org.sbezgin.p2016.services.file;

import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;

import java.util.List;

public interface FileService {
    AbstractFileDTO getFileByID(long fileID);

    FolderDTO getFolder(long folderID);

    List<AbstractFileDTO> getFilesByName(String folderPath, String fileName);

    void saveFile(AbstractFileDTO file);

    void setPermission(long fileD, PermissionDTO perm);

    void renameFile(long fileID, String newName);

    void deleteFile(long fileID, boolean recursively);

    FolderDTO getRootFolder();

    List<AbstractFileDTO> getChildren(long folderID, int start, int end);

    List<AbstractFileDTO> getFilesByType(String javaType);

    TextFileDTO getFullTextFile(long fileID);
}
