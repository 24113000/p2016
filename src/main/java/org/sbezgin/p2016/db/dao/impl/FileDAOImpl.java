package org.sbezgin.p2016.db.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sbezgin.p2016.db.dao.FileDAO;
import org.sbezgin.p2016.db.dto.UserDTO;
import org.sbezgin.p2016.db.entity.Permission;
import org.sbezgin.p2016.db.entity.file.AbstractFile;
import org.sbezgin.p2016.db.entity.file.Folder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    private SessionFactory sessionFactory;

    @Override
    public AbstractFile getFileByID(Long userID, long fileID) {
        Session session = getSession();
        Query query = session.createQuery(
                " select file " +
                " from AbstractFile as file " +
                " left join file.permissions as perm " +
                " where (file.ownerID = :ownerId or (perm.userID = :userId and perm.read = true)) " +
                " and file.id = :fileId  ");
        query.setParameter("ownerId", userID);
        query.setParameter("userId", userID);
        query.setParameter("fileId", fileID);
        return (AbstractFile) query.uniqueResult();
    }

    @Override
    public List<AbstractFile> getFilesByName(Long userID, String folderPath, String fileName) {
        Session session = getSession();
        Query query = session.createQuery(
                " select file " +
                " from AbstractFile as file " +
                " left join file.permissions as perm " +
                " where (file.ownerID = :ownerId or (perm.userID = :userId and perm.read = true)) and file.name = :fileName and file.path = :folderPath ");
        query.setParameter("ownerId", userID);
        query.setParameter("userId", userID);
        query.setParameter("fileName", fileName);
        query.setParameter("folderPath", folderPath);
        return query.list();
    }

    @Override
    public Folder getFolder(Long userID, long folderID) {
        AbstractFile fileByID = getFileByID(userID, folderID);
        if (fileByID != null && fileByID.getClassName().equals(Folder.class.getCanonicalName())) {
            return (Folder) fileByID;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveOrUpdateFile(Long userID, AbstractFile file) {
        Session session = getSession();
        session.save(file);
    }

    @Override
    public int deleteFile(Long userID, long fileID) {
        Session session = getSession();
        Query query = session.createQuery(
                " delete from AbstractFile as file " +
                " where file.id = :fileId and (file.ownerID = :ownerId or ( exists ( " +
                "       from Permission as perm "+
                "       where perm.userID = :userId and perm.del = true and perm.abstractFile.id = :fileId " +
                " )))");
        query.setParameter("ownerId", userID);
        query.setParameter("userId", userID);
        query.setParameter("fileId", fileID);
        return query.executeUpdate();
    }

    @Override
    public List<AbstractFile> getRootFiles(Long ownerID) {
        Session session = getSession();
        Query query = session.createQuery("from AbstractFile as file where file.parentId is null ");
        return query.list();
    }

    @Override
    public List<AbstractFile> getChildren(Long userID, long folderID, int start, int end) {
        Session session = getSession();
        Query query = session.createQuery(
                " select file " +
                " from AbstractFile as file " +
                " left join file.permissions as perm " +
                " where (file.ownerID = :ownerId or (perm.userID = :userId and perm.read = true)) and file.parentId = :folderID "
        );
        query.setParameter("ownerId", userID);
        query.setParameter("userId", userID);
        query.setParameter("folderID", folderID);
        return query.list();
    }

    @Override
    public List<AbstractFile> getFilesByIDs(Long userID, List<Long> idList) {
        Session session = getSession();
        Query query = session.createQuery(
                " select file " +
                " from AbstractFile as file " +
                " left join file.permissions as perm " +
                " where (file.ownerID = :ownerId or (perm.userID = :userId and perm.read = true)) and file.id in :fileIds "
        );
        query.setParameter("ownerId", userID);
        query.setParameter("userId", userID);
        query.setParameterList("fileIds", idList);
        return query.list();
    }

    @Override
    public List<AbstractFile> getAllChildren(Long userId, long fileID) {
        Session session = getSession();
        Query query = session.createQuery(
                " select file " +
                " from AbstractFile as file " +
                " left join file.permissions as perm " +
                " where (file.ownerID = :ownerId or (perm.userID = :userId and perm.read = true)) and (file.idPath like :likeexp or file.idPath like :likeexp2) ");
        query.setParameter("ownerId", userId);
        query.setParameter("userId", userId);
        query.setParameter("likeexp", "%/" + fileID + "/%");
        query.setParameter("likeexp2", "%/" + fileID);
        return query.list();
    }

    @Override
    public Permission getUserFilePermission(Long fileID, Long useID) {
        Session session = getSession();
        Query query = session.createQuery(" from Permission as perm where perm.abstractFile.id = :fileID and userID = :userID ");
        query.setParameter("userID", useID);
        query.setParameter("fileID", fileID);

        return (Permission) query.uniqueResult();
    }

    @Override
    public void removePermission(Long fileID, Long userId) {
        Session session = getSession();
        AbstractFile file = session.get(AbstractFile.class, fileID);
        if (file != null) {
            List<Permission> permissions = file.getPermissions();
            Iterator<Permission> iterator = permissions.iterator();
            while (iterator.hasNext()) {
                Permission next = iterator.next();
                if (next.getUserID().equals(userId)) {
                    iterator.remove();
                }
            }
            session.save(file);
        } else {
            //TODO warn file not found !!!
        }
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
