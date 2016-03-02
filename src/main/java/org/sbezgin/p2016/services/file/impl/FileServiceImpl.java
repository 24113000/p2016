package org.sbezgin.p2016.services.file.impl;

import org.sbezgin.p2016.common.P2016Exception;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.dto.file.TextFileDTO;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.TextFile;
import org.sbezgin.p2016.services.BeanTransformer;
import org.sbezgin.p2016.services.BeanTransformerHolder;
import org.sbezgin.p2016.services.file.FileNotFoundException;
import org.sbezgin.p2016.services.file.FileOperationException;
import org.sbezgin.p2016.services.file.FileService;
import org.sbezgin.p2016.services.impl.TextFileTransformerImpl;
import org.sbezgin.p2016.services.impl.UserServiceImpl;
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
        return null;
    }

    @Override
    public AbstractFileDTO getFileByName(String folderPath, String fileName) {
        UserDTO currentUser = userService.getCurrentUser();
        AbstractFile file = fileDAO.getFileByName(currentUser.getId(), folderPath, fileName);
        if (file != null) {
            BeanTransformer beanTransformer = getTransformer(file);
            return (AbstractFileDTO) beanTransformer.transformEntityToDTO(file);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveFile(AbstractFileDTO file) {
        UserDTO currentUser = userService.getCurrentUser();
        Long id = file.getId();
        int userID = currentUser.getId();

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

    }

    @Override
    public void deleteFile(long fileID, boolean recursively) {

    }

    @Override
    public FolderDTO getRootFolder() {
        UserDTO currentUser = userService.getCurrentUser();
        List<AbstractFile> rootFiles = fileDAO.getRootFiles(currentUser.getId());

        if (rootFiles.size() == 0) {
            throw new P2016Exception("Root folder is missing ");
        }

        if (rootFiles.size() > 1) {
            throw new P2016Exception("Root folder should be one ");
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
