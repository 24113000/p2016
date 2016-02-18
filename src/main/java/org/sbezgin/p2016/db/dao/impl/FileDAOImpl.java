package org.sbezgin.p2016.db.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.entity.User;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveOrUpdateFile(User user, AbstractFile file) {
        Session session = getSession();
        file.setClassName(file.getClass().getCanonicalName());
        file.setOwnerID(user.getId());
        session.save(file);
    }

    @Override
    public void saveOrUpdateFiles(User user, List<AbstractFile> files) {

    }

    @Override
    public void deleteFile(User user, long fileID, boolean recursively) {

    }

    @Override
    public List<AbstractFile> getRootFiles(User user) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.parentId is null ");
        query.setParameter("ownerId", user.getId());
        List list = query.list();
        return list;
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
