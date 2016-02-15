package org.sbezgin.p2016.db.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;

import java.util.List;

public class FileDAOImpl implements FileDAO {

    private SessionFactory sessionFactory;

    @Override
    public AbstractFile getFileByID(User user, long fileID) {
        return null;
    }

    @Override
    public Folder getFolder(User user, long folderID) {
        return null;
    }

    @Override
    public void saveOrUpdateFile(User user, AbstractFile file) {
        Session session = getSession();
        session.saveOrUpdate(file);
    }

    @Override
    public void saveOrUpdateFiles(User user, List<AbstractFile> files) {

    }

    @Override
    public void deleteFile(User user, long fileID, boolean recursively) {

    }

    @Override
    public List<AbstractFile> getRootFiles(User user) {
        return null;
    }

    @Override
    public List<AbstractFile> getChildren(User user, long folderID) {
        return null;
    }

    @Override
    public List<AbstractFile> getFilesByType(User user, String javaType) {
        return null;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
