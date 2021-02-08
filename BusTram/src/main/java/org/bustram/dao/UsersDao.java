package org.bustram.dao;

import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.User;

import java.util.List;

public interface UsersDao {
    // Other DBs setters
    void setMongoDB(MongoDB mongoDB);

    User add(User user);
    User get(User user);
    User check(User user);
    List<User> getAll();
    User update(User user);
    boolean delete(User user, boolean soft);
}
