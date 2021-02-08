package org.bustram.app;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bustram.business.Facade;
import org.bustram.business.FacadeDefault;
import org.bustram.dao.*;
import org.bustram.dao.jdbc.DataBase;
import org.bustram.dao.jdbc.DataSource;
import org.bustram.dao.jdbc.mysql.MySQLDataSource;
import org.bustram.dao.mongo.MongoDB;
import org.bustram.helpers.DateHelper;
import org.bustram.models.*;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class App {
    private DataBase db;
    private MongoDB mongoDB;

    public App() {
        initDatabases();
      // initSampleData();
          exp07();
    }

    void initDatabases() {
        DataSource ds = new MySQLDataSource("localhost", "BusTram", "root", "password");
        db = new DataBase(ds);
        mongoDB = new MongoDB("user", "password", "localhost", "27017", "BusTram");
    }

    void initSampleData() {
        UsersDao usersDao = new UsersDaoJDBC(db);
        SubscriptionsDao subscriptionsDao = new SubscriptionsDaoJDBC(db);
        TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
        StationsDao stationsDao = new StationsDaoMongo(mongoDB);
        PathsDao pathsDao = new PathsDaoMongo(mongoDB);

        usersDao.setMongoDB(mongoDB);
        subscriptionsDao.setMongoDB(mongoDB);
        ticketsDao.setJdbcDB(db);
        stationsDao.setJdbcDB(db);
        pathsDao.setJdbcDB(db);

        List<User> users = new Vector<User>();
        List<Station> stations = new Vector<Station>();
        List<Path> paths = new Vector<Path>();

        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setPassword("password");
            users.add(usersDao.add(user));
        }

        for (int i = 0; i < 6; i++) {
            Station station = new Station();
            station.setNickname("Station " + i);
            stations.add(stationsDao.add(station));
        }

        for (int i = 0; i < 2; i++) {
            Path path = new Path();
            path.setNickname("Path " + i);
            paths.add(pathsDao.add(path));
        }

        for (int i = 0; i < 3; i++) {
            Path path = paths.get(0);
            Station station = stations.get(i);

            path.assign(station);
            station.assign(path);

            pathsDao.update(path);
            stationsDao.update(station);
        }

        for (int i = 3; i < 6; i++) {
            Path path = paths.get(1);
            Station station = stations.get(i);

            path.assign(station);
            station.assign(path);

            pathsDao.update(path);
            stationsDao.update(station);
        }
    }

    private void exp07() {
        // Facade testing
        // 1 - Wiring
        UsersDao usersDao = new UsersDaoJDBC(db);
        SubscriptionsDao subscriptionsDao = new SubscriptionsDaoJDBC(db);
        TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
        StationsDao stationsDao = new StationsDaoMongo(mongoDB);
        PathsDao pathsDao = new PathsDaoMongo(mongoDB);

        usersDao.setMongoDB(mongoDB);
        subscriptionsDao.setMongoDB(mongoDB);
        ticketsDao.setJdbcDB(db);
        stationsDao.setJdbcDB(db);
        pathsDao.setJdbcDB(db);

        // 2 - Initializing the Facade
        Facade facade = new FacadeDefault(usersDao, subscriptionsDao, ticketsDao, stationsDao, pathsDao);

        // 3 - S'inscrire à la plate-forme
//        User user = new User();
//        user.setName("fatima ezzahraa");
//        user.setPassword("password");
//        user = facade.register(user);
//        System.out.println("New user : " + user);

        // 4 - Se désinscrire
//        User user = new User(90);
//        facade.unregister(user, true);

        // 5 - Se connecter à son espace personnel
//        User user = new User();
//        user.setName("User 1");
//        user.setPassword("password");
//        user = facade.connect(user);
//        System.out.println(user);

        // 6 - Se déconnecter
//        System.out.println("This needs to be implemented on the Client application (Destroy the session and the cookies...)");

//         7 - Souscrire à un abonnement mensuel ou annuel
//        User user = new User(94);
//        user = usersDao.get(user);
//
//        Ticket ticket = new Ticket();
//        ticket.setOwner(user);
//        ticket.setStatus(Ticket.ACTIVE);
//        //-1 ticket non consomable
//        ticket.setAllowedUses(-1);
//        ticket.setConsumable(false);
//
//        Subscription subscription = new Subscription();
//        subscription.setStartDate(new Date());
//        subscription.setEndDate(DateHelper.addMonths(subscription.getStartDate(), 12));
//        subscription.setType(Subscription.ANNUALLY);
//        subscription.setTicket(ticket);
//
//        facade.subscribe(subscription);

//        // 8 - Commander un certain nombre de titres (vendables à l'unité ou par 10)
//        User user = new User(94);
//        user = usersDao.get(user);
//
//        Ticket ticket = new Ticket();
//        ticket.setOwner(user);
//        ticket.setStatus(Ticket.ACTIVE);
//        ticket.setAllowedUses(1);
//        ticket.setConsumable(true);
//
//        facade.buy(ticket);

        // 9 - Valider un titre de transport sur un terminal dédié
//        Ticket ticket = new Ticket("5ffecb7b83962d46a8c1d518");
//        //lazy : (un ticket appartient a un user) si true ==>> les données d'une maniere feniante (les id des stations
//        //lazy : true ==> les details
//        ticket = ticketsDao.get(ticket, false);
//
//        Station station = new Station("5ffdf41a0a35865ae715273d");
//        station = stationsDao.get(station, false);
//
//        System.out.println(facade.validate(ticket, station) ? "Ticket is valid" : "Ticket is not valid");
    }

    public static void main(String[] args) {
        new App();
    }
}
