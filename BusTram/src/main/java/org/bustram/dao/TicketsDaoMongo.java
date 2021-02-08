package org.bustram.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bustram.dao.jdbc.DataBase;
import org.bustram.dao.mappers.MongoMapper;
import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.*;

import java.util.List;
import java.util.Vector;

/**
 * La classe permettant de gérer les Tickets sur MongoDB
 */
public class TicketsDaoMongo implements TicketsDao {

    /**
     * Le Nom de la collection des tickets
     */
    public static final String COLLECTION_NAME = "tickets";

    /**
     * L'instance permettant d'accéder à la base de données MongoDB
     */
    private MongoDB mongoDB;

    /**
     * L'instance permettant d'accéder à la base de données MySQL
     */
    private DataBase jdbcDB;

    /**
     * La collection des tickets
     */
    private MongoCollection<Document> ticketsCollection;

    /**
     * Constructeur de la classe
     */
    public TicketsDaoMongo(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
        this.ticketsCollection = this.mongoDB.getCollection(COLLECTION_NAME);
    }

    /**
     * Setter de l'instance d'accés à la base de données MySQL
     */
    public void setJdbcDB(DataBase jdbcDB) {
        this.jdbcDB = jdbcDB;
    }

    /**
     * La méthode pour ajouter un nouveau ticket sur la base de données
     *
     * @param ticket : Le ticket en question
     * @return : La méthode renvoie l'objet du ticket complet avec l'ID de cette dernière
     */
    public Ticket add(Ticket ticket) {
        Document ticketDocument = MongoMapper.getDocument(ticket, true);
        this.ticketsCollection.insertOne(ticketDocument);
        return MongoMapper.getTicket(ticketDocument);
    }

    /**
     * La méthode pour récupérer un objet d'un ticket depuis la collection des tickets
     *
     * @param ticket : L'objet du ticket en question. le seul attribut requis est l'ID.
     * @param lazy   : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie l'objet du ticket avec les details.
     */
    public Ticket get(Ticket ticket, boolean lazy) {

        // First we get the lazy load of the requested ticket from MongoDB
        Document ticketDocument = new Document("_id", new ObjectId(ticket.getId()));
        ticketDocument = this.ticketsCollection.find(ticketDocument).first();

        // Map the ticket from Document to Model
        Ticket found = MongoMapper.getTicket(ticketDocument);

        // If the request is load everything at once
        if (!lazy) {
            UsersDao usersDao = new UsersDaoJDBC(jdbcDB);
            found.setOwner(usersDao.get(found.getOwner()));

            // Ticket Validations
            // Watch out for an infinite loop that may occur if you try to load a Ticket's TicketValidations with !lazy loading
            // Once you call stationsDao.get(station), the station object has it's own ticketValidations which by turn try to get
            // the details of the attached ticket which in this case is the ticket that we started with, thus making an infinite "getDetails" loop
            // Solution is to lazy load the validations and once you need a certain validation details then you can call the needed DAO
            found.setTicketValidations(getValidations(found, true));
        }

        return found;
    }

    /**
     * La méthode pour récupérer les validations du ticket en question
     *
     * @param ticket : Le ticket en question
     * @param lazy   : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des validation du ticket.
     */
    public List<TicketValidation> getValidations(Ticket ticket, boolean lazy) {
        StationsDao stationsDao = new StationsDaoMongo(mongoDB);
        stationsDao.setJdbcDB(jdbcDB);

        List<TicketValidation> ticketValidations = ticket.getTicketValidations();
        for (TicketValidation ticketValidation : ticketValidations) {
            ticketValidation.setStation(stationsDao.get(ticketValidation.getStation(), lazy));
        }
        return ticketValidations;
    }

    /**
     * La méthode pour récupérer tout les Tickets
     *
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des tickets.
     */
    public List<Ticket> getAll(boolean lazy) {
        List<Ticket> tickets = new Vector<Ticket>();
        FindIterable<Document> iterable = this.ticketsCollection.find();
        for (Document document : iterable) {
            Ticket ticket = MongoMapper.getTicket(document);
            if (!lazy) {
                ticket = get(ticket, false);
            }
            tickets.add(ticket);
        }
        return tickets;
    }

    /**
     * La méthode renvoie tout les tickets d'un certain utilisateur
     *
     * @param user : L'utilisateur en question
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des tickets.
     */
    public List<Ticket> getAll(User user, boolean lazy) {
        List<Ticket> tickets = new Vector<Ticket>();
        Document ticketCriteria = new Document("user_id", user.getId());
        FindIterable<Document> iterable = this.ticketsCollection.find(ticketCriteria);
        for (Document document : iterable) {
            Ticket ticket = MongoMapper.getTicket(document);
            if (!lazy) {
                ticket = get(ticket, false);
            }
            tickets.add(ticket);
        }
        return tickets;
    }

    /**
     * La méthode pour mettre à jour un certain ticket
     *
     * @param ticket : le ticket en question
     * @return : La dernière version du ticket
     */
    public Ticket update(Ticket ticket) {
        Document documentToUpdate = new Document("_id", new ObjectId(ticket.getId()));
        Document updatedDocument = MongoMapper.getDocument(ticket, false);
        this.ticketsCollection.updateOne(documentToUpdate, new Document("$set", updatedDocument));
        return ticket;
    }

    /**
     * La méthode pour supprimer un certain ticket
     *
     * @param ticket : Le ticket en question
     * @param soft   : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés à la station en question.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(Ticket ticket, boolean soft) {
        Document ticketDocument = MongoMapper.getDocument(ticket, false);
        if (!soft) {
            // Delete ticket's station validations that have been done in all of the stations since the beginning of time
            // Delete also the ticket's subscriptions
            StationsDao stationsDao = new StationsDaoMongo(mongoDB);
            stationsDao.setJdbcDB(jdbcDB);

            if (ticket.isConsumable()) {
                return this.ticketsCollection.findOneAndDelete(ticketDocument) != null && stationsDao.deleteValidations(ticket);
            }

            SubscriptionsDao subscriptionsDao = new SubscriptionsDaoJDBC(jdbcDB);
            subscriptionsDao.setMongoDB(mongoDB);
            Subscription subscription = subscriptionsDao.get(ticket, true);

            return this.ticketsCollection.findOneAndDelete(ticketDocument) != null && stationsDao.deleteValidations(ticket) && subscriptionsDao.delete(subscription, true);
        } else {
            return this.ticketsCollection.findOneAndDelete(ticketDocument) != null;
        }
    }

    /**
     * La méthode pour supprimer une liste des tickets
     *
     * @param tickets : Les tickets en question
     * @param soft    : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés à la station en question.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(List<Ticket> tickets, boolean soft) {
        boolean allSuccess = true;
        for (Ticket ticket : tickets) {
            if (!delete(ticket, soft)) allSuccess = false;
        }
        return allSuccess;
    }

    /**
     * La méthode pour supprimer les ticket d'un certain utilisateur
     *
     * @param user : L'utilisateur en question
     * @param soft : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés à la station en question.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(User user, boolean soft) {
        List<Ticket> userTickets = getAll(user, true);
        return delete(userTickets, soft);
    }

    /**
     * la méthode supprime toutes les occurrences de la station courante sur les validations des tickets dans toute les tickets existants
     *
     * @param station : La station en question
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean deleteValidations(Station station) {
        Document stationDocument = new Document();
        Document stationCriteria = new Document("ticket_validations", new Document("station_id", station.getId()));
        Document stationQuery = new Document("$pull", stationCriteria);
        return this.ticketsCollection.updateMany(stationDocument, stationQuery).wasAcknowledged();
    }
}
