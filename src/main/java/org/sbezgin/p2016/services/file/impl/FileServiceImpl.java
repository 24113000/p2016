package org.sbezgin.p2016.services.file.impl;

import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.PermissionDTO;
import org.sbezgin.p2016.db.dto.file.AbstractFileDTO;
import org.sbezgin.p2016.db.dto.file.FolderDTO;
import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.services.BeanTransformer;
import org.sbezgin.p2016.services.file.FileService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class FileServiceImpl implements FileService {
    private FileDAO fileDAO;
    private BeanTransformer beanTransformer;

    @Override
    public AbstractFileDTO getFileByID(long fileID) {
        return null;
    }

    @Override
    public FolderDTO getFolder(long folderID) {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveFile(AbstractFileDTO file) {
        Long id = file.getId();
        if (id == null) {
            AbstractFile fileEntity = (AbstractFile) beanTransformer.transformDTOToEntity(file);
            User user = new User();
            user.setId(1);
            fileDAO.saveOrUpdateFile(user, fileEntity);
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
    public List<AbstractFileDTO> getRootFiles() {
        return null;
    }

    @Override
    public List<AbstractFileDTO> getChildren(long folderID) {
        return null;
    }

    @Override
    public List<AbstractFileDTO> getFilesByType(String javaType) {
        return null;
    }


    public FileDAO getFileDAO() {
        return fileDAO;
    }

    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    public BeanTransformer getBeanTransformer() {
        return beanTransformer;
    }

    public void setBeanTransformer(BeanTransformer beanTransformer) {
        this.beanTransformer = beanTransformer;
    }
}
