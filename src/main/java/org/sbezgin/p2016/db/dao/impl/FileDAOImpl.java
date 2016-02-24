package org.sbezgin.p2016.db.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class FileDAOImpl implements FileDAO {

    private SessionFactory sessionFactory;

    @Override
    public AbstractFile getFileByID(int userID, long fileID) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.id = :fileId  ");
        query.setParameter("ownerId", userID);
        query.setParameter("fileId", fileID);
        return (AbstractFile) query.uniqueResult();
    }

    @Override
    public Folder getFolder(int userID, long folderID) {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveOrUpdateFile(int userID, AbstractFile file) {
        Session session = getSession();
        session.save(file);
    }

    @Override
    public void saveOrUpdateFiles(int userID, List<AbstractFile> files) {

    }

    @Override
    public void deleteFile(int userID, long fileID, boolean recursively) {

    }

    @Override
    public List<AbstractFile> getRootFiles(int ownerID) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.parentId is null ");
        query.setParameter("ownerId", ownerID);
        return query.list();
    }

    @Override
    public List<AbstractFile> getChildren(int userID, long folderID, int start, int end) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.parentId = :folderID ");
        query.setParameter("ownerId", userID);
        query.setParameter("folderID", folderID);
        return query.list();
    }

    @Override
    public List<AbstractFile> getFilesByType(int userID, String javaType) {
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
