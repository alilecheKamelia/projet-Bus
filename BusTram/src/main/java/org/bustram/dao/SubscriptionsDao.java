package org.bustram.dao;

import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.Subscription;
import org.bustram.models.Ticket;

import java.util.List;

public interface SubscriptionsDao {
    // Other DBs setters
    void setMongoDB(MongoDB mongoDB);

    Subscription add(Subscription subscription);
    Subscription get(Subscription subscription, boolean lazy);
    Subscription get(Ticket ticket, boolean lazy);
    List<Subscription> getAll(boolean lazy);
    Subscription update(Subscription subscription);
    boolean delete(Subscription subscription, boolean soft);
}
