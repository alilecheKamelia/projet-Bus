package org.bustram.dao;

import org.bustram.dao.jdbc.DataBase;
import org.bustram.dao.mappers.JDBCMapper;
import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.Subscription;
import org.bustram.models.Ticket;

import java.util.List;

/**
 * La classe permettant de gérer les abonnements sur MySQL
 */
public class SubscriptionsDaoJDBC implements SubscriptionsDao {

    /**
     * La variable indiquant le nom de la table des abonnements sur MySQL
     */
    public static final String TABLE_NAME = "Subscriptions";

    /**
     * La clé primaire de la table des abonnements
     */
    public static final String PRIMARY_KEY = "id";

    /**
     * L'instance permettant d'accéder à la base de données MySQL
     */
    private DataBase db;

    /**
     * L'instance permettant d'accéder à la base de données MongoDB
     */
    private MongoDB mongoDB;

    /**
     * Constructeur de la classe
     */
    public SubscriptionsDaoJDBC(DataBase db) {
        this.db = db;
    }

    /**
     * Setter de l'instance d'accés à la base de données MongoDB
     */
    public void setMongoDB(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    /**
     * La méthode pour ajouter un abonnement sur la base de données
     *
     * @param subscription : L'abonnement en question
     * @return : La méthode renvoie l'objet de l'abonnement complet avec l'ID du nouvel enregistrement
     */
    public Subscription add(Subscription subscription) {
        return (Subscription) db.insert(subscription, TABLE_NAME);
    }

    /**
     * La méthode pour récupérer un objet d'un abonnement  depuis la base de données
     *
     * @param subscription : L'objet de l'abonnement en question. le seul attribut requis est l'ID.
     * @param lazy         : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie l'objet de l'abonnement avec les details.
     */
    public Subscription get(Subscription subscription, boolean lazy) {
        subscription = JDBCMapper.getSubscription(db.selectOne(TABLE_NAME, PRIMARY_KEY, subscription.getId()));

        if (!lazy) {
            TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
            ticketsDao.setJdbcDB(db);

            Ticket ticket = ticketsDao.get(subscription.getTicket(), false);
            subscription.setTicket(ticket);
        }

        return subscription;
    }

    /**
     * La méthode pour récupérer un objet d'un abonnement associé à un ticket
     *
     * @param ticket : Le ticket en question. le seul attribut requis est l'ID.
     * @param lazy   : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie l'objet de l'abonnement avec les details.
     */
    public Subscription get(Ticket ticket, boolean lazy) {
        Subscription subscription = JDBCMapper.getSubscription(db.selectOne(TABLE_NAME, "ticket_id", ticket.getId()));
        subscription = get(subscription, lazy);
        return subscription;
    }

    /**
     * La méthode pour récupérer la liste de tout les abonnements depuis la base de données
     *
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des abonnements avec les details.
     */
    public List<Subscription> getAll(boolean lazy) {
        return JDBCMapper.getSubscriptions(db.select(TABLE_NAME));
    }

    /**
     * La méthode pour mettre à jour un objet d'un abonnement sur la base de données
     *
     * @param subscription : L'objet de l'abonnement en question.
     * @return : La méthode renvoie la dernière version de l'abonnement.
     */
    public Subscription update(Subscription subscription) {
        return (Subscription) db.update(subscription, TABLE_NAME);
    }

    /**
     * La méthode pour supprimer un certain abonnement
     *
     * @param subscription : L'abonnement en question
     * @param soft    : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés au abonnement en question.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(Subscription subscription, boolean soft) {
        if (!soft) {
            TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
            ticketsDao.setJdbcDB(db);
            // I removed the db.delete in here cus ticketsDao.delete(...) with a "NOT SOFT" call is calling the current method with a "SOFT" delete anyways
            // Thus we eventually execute the db.delete(...) located in the else block
            return ticketsDao.delete(subscription.getTicket(), false);
        } else {
            return db.delete(subscription, TABLE_NAME);
        }
    }
}
