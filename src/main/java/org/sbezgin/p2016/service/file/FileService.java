package org.sbezgin.p2016.service.file;

import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.Permission;

import java.util.List;

public interface FileService {
    AbstractFileDTO getFileByID(long fileID);

    FolderDTO getFolder(long folderID);

    List<AbstractFileDTO> getFilesByName(String folderPath, String fileName);

    void saveFile(AbstractFileDTO file);

    void setPermission(AbstractFileDTO fileDTO, PermissionDTO perm);

    void removePermission(AbstractFileDTO fileDTO, UserDTO userDTO);

    void setFolderPermissionRecursively(FolderDTO folderDTO, PermissionDTO perm);

    void removeFolderPermissionRecursively(FolderDTO folderDTO, UserDTO userDTO);

    void renameFile(long fileID, String newName);

    void deleteFile(long fileID, boolean recursively);

    FolderDTO getRootFolder();

    List<AbstractFileDTO> getChildren(long folderID, int start, int end);

    List<AbstractFileDTO> getFilesByType(String javaType);

    TextFileDTO getFullTextFile(long fileID);

    PermissionDTO getUserFilePermission(AbstractFileDTO fileDTO, UserDTO userDTO);

    PermissionDTO getCurrentUserFilePermission(AbstractFileDTO fileDTO);
}
