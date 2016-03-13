package org.sbezgin.p2016.db.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sbezgin.p2016.db.dao.UserDAO;
import org.sbezgin.p2016.db.entity.User;

public class UserDAOImpl implements UserDAO {

    private SessionFactory sessionFactory;

    @Override
    public User getUser(Long id) {
        Session session = getSession();
        return session.get(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        Session session = getSession();
        Query query = session.createQuery("from User as user where user.email = :email ");
        query.setParameter("email", email);
        return (User) query.uniqueResult();
    }

    @Override
    public void delete(User user) {
        Session session = getSession();
        session.delete(user);
    }

    @Override
    public void save(User user) {
        Session session = getSession();
        session.save(user);
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
