package org.sbezgin.p2016.service.file;

import org.sbezgin.p2016.common.CommonConstants;
import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileContentDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.Permission;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.sbezgin.p2016.db.entity.file.TextFile;
import org.sbezgin.p2016.service.UserServiceImpl;
import org.sbezgin.p2016.service.transformer.BeanTransformer;
import org.sbezgin.p2016.service.transformer.BeanTransformerHolder;
import org.sbezgin.p2016.service.transformer.AbstractFileTransformer;
import org.sbezgin.p2016.service.transformer.TextFileTransformerImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class FileServiceImpl implements FileService {
    private FileDAO fileDAO;
    private UserServiceImpl userService;
    private BeanTransformerHolder beanTransformerHolder;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AbstractFileDTO getFileByID(long fileID) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile file = fileDAO.getFileByID(currentUser.getId(), fileID);
        if (file != null) {
            BeanTransformer transformer = beanTransformerHolder.getTransformer(file.getClass().getCanonicalName());
            return (AbstractFileDTO) transformer.transformEntityToDTO(file);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FolderDTO getFolder(long folderID) {
        UserDTO currentUser = userService.getCurrentUser();
        Folder folder = fileDAO.getFolder(currentUser.getId(), folderID);
        if (folder != null) {
            BeanTransformer transformer = beanTransformerHolder.getTransformer(folder.getClass().getCanonicalName());
            return (FolderDTO) transformer.transformEntityToDTO(folder);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AbstractFileDTO> getFilesByName(String folderPath, String fileName) {
        UserDTO currentUser = userService.getCurrentUser();
        List<AbstractFile> files = fileDAO.getFilesByName(currentUser.getId(), folderPath, fileName);
        if (files != null && files.size() > 0) {

            List<AbstractFileDTO> fileDTOs = new ArrayList<>(files.size());
            for (AbstractFile file : files) {
                BeanTransformer beanTransformer = getTransformer(file);
                fileDTOs.add((AbstractFileDTO) beanTransformer.transformEntityToDTO(file));
            }

            return fileDTOs;
        }
        return new ArrayList<>();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveFile(AbstractFileDTO file) {
        saveFile(file, null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveFile(AbstractFileDTO file, Date lastUpdate) {
        UserDTO currentUser = userService.getCurrentUser();
        Long id = file.getId();
        Long userID = currentUser.getId();

        String fileName = file.getName();
        if (fileName.contains("/") || (fileName.contains("\\\\"))) {
            throw new FileOperationException("File name cannot contain slash");
        }

        if (id == null) {
            BeanTransformer beanTransformer = getTransformer(file);
            AbstractFile fileEntity = (AbstractFile) beanTransformer.transformDTOToEntity(file);

            fileEntity.setClassName(fileEntity.getClass().getCanonicalName());
            fileEntity.setOwnerID(userID);

            Long parentId = fileEntity.getParentId();

            if (parentId == null) { //root
                fileEntity.setPath("/");
                fileEntity.setIdPath("/");
            } else {
                AbstractFile parent = getFile(parentId, userID);
                if (parent == null) {
                    throw new P2016Exception("Cannot get parent folder by ID " + parentId + " and user ID " + userID);
                }
                String newPathId;
                String newPath;
                if (parent.getParentId() == null) {
                    newPathId = "/"  + CommonConstants.ROOT_FOLDER_ID;
                    newPath =  "/"  + CommonConstants.ROOT_FOLDER_NAME;
                } else {
                    newPathId = parent.getIdPath() + "/" + parentId;
                    newPath = parent.getPath() + "/" + parent.getName();
                }

                fileEntity.setIdPath(newPathId);
                fileEntity.setPath(newPath);
            }

            Date date = new Date();
            fileEntity.setCreateDate(date);
            fileEntity.setUpdateDate(date);
            fileDAO.saveOrUpdateFile(userID, fileEntity);
        } else {
            AbstractFile savedFile = fileDAO.getFileByID(userID, file.getId());

            if (savedFile == null) {
                throw new FileNotFoundException("Cannot get file " + file.getName() + " by id " + file.getId());
            }

            compareUpdateDate(file, lastUpdate, savedFile);

            if (!savedFile.getOwnerID().equals(currentUser.getId())) {
                Permission permission = fileDAO.getUserFilePermission(savedFile.getId(), userID);
                if (permission == null || !permission.getWrite()) {
                    throw new FileAccessDeniedException("User " + userID + " don't have access to file " + savedFile.getId());
                }
            }

            BeanTransformer transformer = getTransformer(savedFile);
            transformer.copyFieldsToEntity(file, savedFile);

            savedFile.setUpdateDate(new Date());
            fileDAO.saveOrUpdateFile(userID, savedFile);
        }
    }

    private void compareUpdateDate(AbstractFileDTO file, Date lastUpdate, AbstractFile savedFile) {
        Date checkDate;
        if (lastUpdate != null) {
            checkDate = lastUpdate;
        } else {
            checkDate = file.getUpdateDate();
        }

        if (checkDate != null && !savedFile.getUpdateDate().equals(checkDate)) {
            throw new FileChangedException("File has been changed ID " + savedFile.getId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void savePermission(AbstractFileDTO fileDTO, PermissionDTO perm) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile savedFile = fileDAO.getFileByID(currentUser.getId(), fileDTO.getId());

        if (savedFile == null) {
            throw new FileNotFoundException("File not found id " + fileDTO.getId());
        }

        if (!isUserOwner(savedFile)) {
            throw new FileAccessDeniedException("Access denied for file " + savedFile.getId());
        }

        Permission permissionForUser = getPermissionForUser(savedFile.getPermissions(), perm.getUserID());

        AbstractFileTransformer fileTransformer = (AbstractFileTransformer) getTransformer(savedFile);

        if (permissionForUser == null) {
            perm.setFileDTO(fileDTO);
            fileDTO.getPermissions().add(perm);
            saveFile(fileDTO);
        } else {
            fileTransformer.copyPermissionDTOToEntity(perm, permissionForUser, savedFile);
            savedFile.getPermissions().add(permissionForUser);
            fileDAO.saveOrUpdateFile(currentUser.getId(), savedFile);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removePermission(AbstractFileDTO fileDTO, UserDTO userDTO) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile file = fileDAO.getFileByID(currentUser.getId(), fileDTO.getId());
        if (file != null) {
            if (isUserOwner(file)) {
                fileDAO.removePermission(fileDTO.getId(), userDTO.getId());
                return;
            } else {
                throw new FileAccessDeniedException("User is not owner ");
            }
        }
        throw new FileNotFoundException("Cannot find a file: " + fileDTO.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeFolderPermissionRecursively(FolderDTO folderDTO, UserDTO userDTO) {
        removePermission(folderDTO, userDTO);
        List<AbstractFile> children = fileDAO.getUnsecuredAllChildren(folderDTO.getId());

        for (AbstractFile child : children) {
            checkIfUserOwner(child);
        }

        for (AbstractFile child : children) {
            fileDAO.removePermission(child.getId(), userDTO.getId());
        }
    }

    private void checkIfUserOwner(AbstractFile child) {
        UserDTO currentUser = userService.getCurrentUser();
        if (!child.getOwnerID().equals(currentUser.getId())) {
            throw new FileAccessDeniedException("User " + currentUser.getId() +  " is owner of file " + child.getId());
        }
    }


    private boolean isUserOwner(AbstractFile file) {
        UserDTO currentUser = userService.getCurrentUser();
        return file.getOwnerID().equals(currentUser.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setFolderPermissionRecursively(FolderDTO folderDTO, PermissionDTO permDTO) {
        UserDTO currentUser = userService.getCurrentUser();
        savePermission(folderDTO, permDTO);
        List<AbstractFile> children = fileDAO.getUnsecuredAllChildren(folderDTO.getId());

        for (AbstractFile child : children) {
            checkIfUserOwner(child);
        }

        for (AbstractFile child : children) {

            AbstractFileTransformer fileTransformer = (AbstractFileTransformer) getTransformer(child);

            List<Permission> permissions = child.getPermissions();
            if (permissions == null) {
                permissions = new ArrayList<>();
            }

            Permission permission = getPermissionForUser(permissions, permDTO.getUserID());
            if (permission == null) {
                permission = new Permission();
            }

            fileTransformer.copyPermissionDTOToEntity(permDTO, permission, child);
            permissions.add(permission);

            fileDAO.saveOrUpdateFile(currentUser.getId(), child);
        }
    }

    private Permission getPermissionForUser(List<Permission> permissions, Long userId) {
        for (Permission permission : permissions) {
            if (permission.getUserID().equals(userId)) {
                return permission;
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void renameFile(long fileID, String newName) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile file = fileDAO.getFileByID(currentUser.getId(), fileID);
        if (file == null) {
            throw new FileNotFoundException("Cannot find file: " + fileID);
        }
        file.setName(newName);
        fileDAO.saveOrUpdateFile(currentUser.getId(), file);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFile(long fileID, boolean recursively) {
        UserDTO currentUser = userService.getCurrentUser();
        boolean result;
        AbstractFile file = fileDAO.getFileByID(currentUser.getId(), fileID);

        if (file == null) {
            throw new FileNotFoundException("Cannot find file: " + fileID);
        }

        if (recursively) {
            List<AbstractFile> children = fileDAO.getUnsecuredAllChildren(fileID);
            children.sort((o1, o2) -> {
                        if (o1.getIdPath().split("/").length < o2.getIdPath().split("/").length) {
                            return 1;
                        } else if (o1.getIdPath().split("/").length > o2.getIdPath().split("/").length) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
            );

            for (AbstractFile child : children) {
                checkDeletePermission(currentUser.getId(), child);
            }

            for (AbstractFile child : children) {
                boolean childResult = fileDAO.deleteFile(currentUser.getId(), child.getId());
                if (!childResult) {
                    throw new FileNotFoundException("Cannot delete file with id: " + child.getId());
                }
            }

            checkDeletePermission(currentUser.getId(), file);
            result = fileDAO.deleteFile(currentUser.getId(), fileID);
            if (!result) {
                throw new FileNotFoundException("Cannot delete file with id: " + fileID);
            }
        } else {
            List<AbstractFile> children = fileDAO.getAllChildren(currentUser.getId(), fileID);
            if (children.size() != 0) {
                throw new FolderIsNotEmpty("Folder is not empty");
            }

            checkDeletePermission(currentUser.getId(), file);

            result = fileDAO.deleteFile(currentUser.getId(), fileID);
            if (!result) {
                throw new FileNotFoundException("Cannot delete file with id: " + fileID);
            }
        }
    }

    private void checkDeletePermission(Long userID, AbstractFile file) {
        if (!file.getOwnerID().equals(userID)) {
            Permission permission = fileDAO.getUserFilePermission(file.getId(), userID);
            if (permission == null || permission.getDel() == null || !permission.getDel()) {
                throw new FileAccessDeniedException("User " + userID + " don't have access to file " + file.getId());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FolderDTO getRootFolder() {
        AbstractFile rootFolder = fileDAO.getRootFolder();

        if (rootFolder == null) {
            throw new FileNotFoundException("Root folder is missing ");
        }

        BeanTransformer beanTransformer = getTransformer(rootFolder);
        return (FolderDTO) beanTransformer.transformEntityToDTO(rootFolder);
    }

    private BeanTransformer getTransformer(Object obj) {
        return beanTransformerHolder.getTransformer(obj.getClass().getCanonicalName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AbstractFileDTO> getChildren(long folderID, int start, int end) {
        UserDTO currentUser = userService.getCurrentUser();

        AbstractFile fileByID = getFile(folderID, currentUser.getId());

        if (fileByID != null) {
            List<AbstractFile> children = fileDAO.getChildren(currentUser.getId(), folderID, start, end);
            List<AbstractFileDTO> result = new ArrayList<>(children.size());
            result.addAll(
                    children.stream().map(
                            child -> {
                                BeanTransformer beanTransformer = getTransformer(child);
                                return (AbstractFileDTO) beanTransformer.transformEntityToDTO(child);
                            }
                    ).collect(Collectors.toList())
            );

            return result;
        }

        throw new FileNotFoundException("Cannot find a file: " + folderID);
    }

    @Override
    public List<AbstractFileDTO> getFilesByType(String javaType) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TextFileDTO getFullTextFile(long fileID) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile textFile = fileDAO.getFileByID(currentUser.getId(), fileID);

        if (textFile == null) {
            throw new FileNotFoundException("File not found id " + fileID);
        }

        if (!isUserOwner(textFile)) {
            Permission perm = getPermissionForUser(textFile.getPermissions(), currentUser.getId());
            if (perm == null || perm.getRead() == null || !perm.getRead()) {
                throw new FileAccessDeniedException("Access denied for file " + fileID);
            }
        }

        TextFileTransformerImpl transformer = (TextFileTransformerImpl) beanTransformerHolder.getTransformer(textFile.getClassName());
        return transformer.transformEntityToDTO((TextFile) textFile, true);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PermissionDTO getUserFilePermission(AbstractFileDTO fileDTO, Long userID) {
        Permission permission = fileDAO.getUserFilePermission(fileDTO.getId(), userID);
        if (permission != null) {
            BeanTransformer beanTransformer = getTransformer(permission);
            return (PermissionDTO) beanTransformer.transformEntityToDTO(permission);
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PermissionDTO getCurrentUserFilePermission(AbstractFileDTO fileDTO) {
        UserDTO currentUser = userService.getCurrentUser();
        return getUserFilePermission(fileDTO, currentUser.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveTextFileContent(long fileID, String fileContent) {
        TextFileDTO fullTextFile = getFullTextFile(fileID);
        TextFileContentDTO content = fullTextFile.getFileContent();
        if (content == null) {
            content = new TextFileContentDTO();
        }
        content.setData(fileContent);
        saveFile(fullTextFile);
    }

    private AbstractFile getFile(Long fileID, Long userID) {
        if (fileID == CommonConstants.ROOT_FOLDER_ID) {
            return fileDAO.getRootFolder();
        } else {
            return fileDAO.getFileByID(userID, fileID);
        }
    }

    public FileDAO getFileDAO() {
        return fileDAO;
    }

    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }

    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    public BeanTransformerHolder getBeanTransformerHolder() {
        return beanTransformerHolder;
    }

    public void setBeanTransformerHolder(BeanTransformerHolder beanTransformerHolder) {
        this.beanTransformerHolder = beanTransformerHolder;
    }
}
