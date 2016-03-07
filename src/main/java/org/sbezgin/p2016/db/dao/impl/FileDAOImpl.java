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
    public List<AbstractFile> getFileByIDs(int userID, List<Long> fileIDs) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.id = :fileId  ");
        return query.list();
    }

    @Override
    public List<AbstractFile> getFilesByName(int userID, String folderPath, String fileName) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.name = :fileName and file.path = :folderPath  ");
        query.setParameter("ownerId", userID);
        query.setParameter("fileName", fileName);
        query.setParameter("folderPath", folderPath);
        return query.list();
    }

    @Override
    public Folder getFolder(int userID, long folderID) {
        AbstractFile fileByID = getFileByID(userID, folderID);
        if (fileByID != null && fileByID.getClassName().equals(Folder.class.getCanonicalName())) {
            return (Folder) fileByID;
        }
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
    public int deleteFile(int userID, long fileID) {
        Session session = getSession();
        Query query = session.createQuery("delete from AbstractFile as file where file.ownerID = :ownerId and file.id = :fileId ");
        query.setParameter("ownerId", userID);
        query.setParameter("fileId", fileID);
        return query.executeUpdate();
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
    public List<AbstractFile> getFilesByIDs(int userID, List<Long> idList) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and file.id in :fileIds");
        query.setParameter("ownerId", userID);
        query.setParameterList("fileIds", idList);
        return query.list();
    }

    @Override
    public List<AbstractFile> getAllChildren(int userId, long fileID) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.ownerID = :ownerId and (file.idPath like :likeexp or file.idPath like :likeexp2) ");
        query.setParameter("ownerId", userId);
        query.setParameter("likeexp", "%/" + fileID + "/%");
        query.setParameter("likeexp2", "%/" + fileID);
        return query.list();
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
