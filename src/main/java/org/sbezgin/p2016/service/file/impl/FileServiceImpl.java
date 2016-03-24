package org.sbezgin.p2016.service.file.impl;

import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.Permission;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.sbezgin.p2016.db.entity.file.TextFile;
import org.sbezgin.p2016.service.file.*;
import org.sbezgin.p2016.service.impl.UserServiceImpl;
import org.sbezgin.p2016.service.transformer.BeanTransformer;
import org.sbezgin.p2016.service.transformer.BeanTransformerHolder;
import org.sbezgin.p2016.service.transformer.impl.AbstractFileTransformer;
import org.sbezgin.p2016.service.transformer.impl.TextFileTransformerImpl;
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
                AbstractFile parent = fileDAO.getFileByID(userID, parentId);
                if (parent == null) {
                    throw new P2016Exception("Cannot get parent folder by ID " + parentId + " and user ID " + userID);
                }

                String newPathId;
                String newPath;
                if (parent.getParentId() == null) {
                    newPathId = parent.getIdPath() + parentId;
                    newPath = parent.getPath() + parent.getName();
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setPermission(AbstractFileDTO fileDTO, PermissionDTO perm) {
        perm.setFileDTO(fileDTO);
        fileDTO.getPermissionDTOs().add(perm);
        saveFile(fileDTO);
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
        UserDTO currentUser = userService.getCurrentUser();
        removePermission(folderDTO, userDTO);
        List<AbstractFile> children = fileDAO.getAllChildren(currentUser.getId(), folderDTO.getId());
        //TODO check if user has permission on all children
        for (AbstractFile child : children) {
            fileDAO.removePermission(child.getId(), userDTO.getId());
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
        setPermission(folderDTO, permDTO);
        List<AbstractFile> children = fileDAO.getAllChildren(currentUser.getId(), folderDTO.getId());
        //TODO check if user has permission on all children
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
        int result;
        AbstractFile file = fileDAO.getFileByID(currentUser.getId(), fileID);

        if (file == null) {
            throw new FileNotFoundException("Cannot find file: " + fileID);
        }

        if (recursively) {
            List<AbstractFile> children = fileDAO.getAllChildren(currentUser.getId(), fileID);
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

                int childResult = fileDAO.deleteFile(currentUser.getId(), child.getId());
                if (childResult != 1) {
                    throw new FileNotFoundException("Cannot delete file with id: " + child.getId());
                }

            }

            checkDeletePermission(currentUser.getId(), file);
            result = fileDAO.deleteFile(currentUser.getId(), fileID);
            if (result != 1) {
                throw new FileNotFoundException("Cannot delete file with id: " + fileID);
            }
        } else {
            List<AbstractFile> children = fileDAO.getAllChildren(currentUser.getId(), fileID);
            if (children.size() != 0) {
                throw new FolderIsNotEmpty("Folder is not empty");
            }

            checkDeletePermission(currentUser.getId(), file);

            result = fileDAO.deleteFile(currentUser.getId(), fileID);
            if (result != 1) {
                throw new FileNotFoundException("Cannot delete file with id: " + fileID);
            }
        }
    }

    private void checkDeletePermission(Long userID, AbstractFile file) {
        if (!file.getOwnerID().equals(userID)) {
            Permission permission = fileDAO.getUserFilePermission(file.getId(), userID);
            if (permission == null || !permission.getDel()) {
                throw new FileAccessDeniedException("User " + userID + " don't have access to file " + file.getId());
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FolderDTO getRootFolder() {
        UserDTO currentUser = userService.getCurrentUser();
        List<AbstractFile> rootFiles = fileDAO.getRootFiles(currentUser.getId());

        if (rootFiles.size() == 0) {
            throw new FileNotFoundException("Root folder is missing ");
        }

        if (rootFiles.size() > 1) {
            throw new FileOperationException("Root folder should be unique");
        }

        AbstractFile rootFolder = rootFiles.get(0);
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

    @Override
    public List<AbstractFileDTO> getFilesByType(String javaType) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TextFileDTO getFullTextFile(long fileID) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile textFile = fileDAO.getFileByID(currentUser.getId(), fileID);
        TextFileTransformerImpl transformer = (TextFileTransformerImpl) beanTransformerHolder.getTransformer(textFile.getClassName());
        return transformer.transformEntityToDTO((TextFile) textFile, true);
    }

    @Override
    public PermissionDTO getUserFilePermission(AbstractFileDTO fileDTO, UserDTO userDTO) {
        Permission permission = fileDAO.getUserFilePermission(fileDTO.getId(), userDTO.getId());
        if (permission != null) {
            BeanTransformer beanTransformer = getTransformer(permission);
            return (PermissionDTO) beanTransformer.transformEntityToDTO(permission);
        }
        return null;
    }

    @Override
    public PermissionDTO getCurrentUserFilePermission(AbstractFileDTO fileDTO) {
        UserDTO currentUser = userService.getCurrentUser();
        return getUserFilePermission(fileDTO, currentUser);
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
