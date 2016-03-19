package org.sbezgin.p2016.service.file.impl;

import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.sbezgin.p2016.db.entity.file.TextFile;
import org.sbezgin.p2016.service.transformer.BeanTransformer;
import org.sbezgin.p2016.service.transformer.BeanTransformerHolder;
import org.sbezgin.p2016.service.file.FileNotFoundException;
import org.sbezgin.p2016.service.file.FileOperationException;
import org.sbezgin.p2016.service.file.FileService;
import org.sbezgin.p2016.service.file.FolderIsNotEmpty;
import org.sbezgin.p2016.service.transformer.impl.TextFileTransformerImpl;
import org.sbezgin.p2016.service.impl.UserServiceImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class FileServiceImpl implements FileService {
    private FileDAO fileDAO;
    private UserServiceImpl userService;
    private BeanTransformerHolder beanTransformerHolder;

    @Override
    public AbstractFileDTO getFileByID(long fileID) {
        return null;
    }

    @Override
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

            fileDAO.saveOrUpdateFile(userID, fileEntity);
        } else {
            AbstractFile savedFile = fileDAO.getFileByID(userID, file.getId());
            if (savedFile == null) {
                throw new FileNotFoundException("Cannot get file " + file.getName() + " by id " + file.getId());
            }

            BeanTransformer transformer = getTransformer(savedFile);
            transformer.copyFieldsToEntity(file, savedFile);

            fileDAO.saveOrUpdateFile(userID, savedFile);
        }
    }

    @Override
    public void setPermission(long fileD, PermissionDTO perm) {

    }

    @Override
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
    public void deleteFile(long fileID, boolean recursively) {
        UserDTO currentUser = userService.getCurrentUser();
        int result;
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
                int childResult = fileDAO.deleteFile(currentUser.getId(), child.getId());
                if (childResult != 1) {
                    throw new FileNotFoundException("Cannot delete file with id: " + child.getId());
                }
            }

            result = fileDAO.deleteFile(currentUser.getId(), fileID);
            if (result != 1) {
                throw new FileNotFoundException("Cannot delete file with id: " + fileID);
            }
        } else {
            List<AbstractFile> children = fileDAO.getAllChildren(currentUser.getId(), fileID);
            if (children.size() != 0) {
                throw new FolderIsNotEmpty("Folder is not empty");
            }

            result = fileDAO.deleteFile(currentUser.getId(), fileID);
            if (result != 1) {
                throw new FileNotFoundException("Cannot delete file with id: " + fileID);
            }
        }
    }

    @Override
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
    public TextFileDTO getFullTextFile(long fileID) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile textFile = fileDAO.getFileByID(currentUser.getId(), fileID);
        TextFileTransformerImpl transformer = (TextFileTransformerImpl) beanTransformerHolder.getTransformer(textFile.getClassName());
        return transformer.transformEntityToDTO((TextFile) textFile, true);
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
