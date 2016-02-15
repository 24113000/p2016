package org.sbezgin.p2016.services.file.impl;

import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.Permission;
import org.sbezgin.p2016.db.dto.file.AbstractFile;
import org.sbezgin.p2016.db.dto.file.Folder;
import org.sbezgin.p2016.services.BeanTransformer;
import org.sbezgin.p2016.services.file.FileService;

import java.util.List;

public class FileServiceImpl implements FileService {
    private FileDAO fileDAO;
    private BeanTransformer beanTransformer;

    @Override
    public AbstractFile getFileByID(long fileID) {
        return null;
    }

    @Override
    public Folder getFolder(long folderID) {
        return null;
    }

    @Override
    public void saveFile(AbstractFile file) {
        fileDAO.saveOrUpdateFile();
    }

    @Override
    public void setPermission(long fileD, Permission perm) {

    }

    @Override
    public void renameFile(long fileID, String newName) {

    }

    @Override
    public void deleteFile(long fileID, boolean recursively) {

    }

    @Override
    public List<AbstractFile> getRootFiles() {
        return null;
    }

    @Override
    public List<AbstractFile> getChildren(long folderID) {
        return null;
    }

    @Override
    public List<AbstractFile> getFilesByType(String javaType) {
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
