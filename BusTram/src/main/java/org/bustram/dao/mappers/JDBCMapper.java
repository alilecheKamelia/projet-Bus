package org.bustram.dao.mappers;

import org.bustram.helpers.DateHelper;
import org.bustram.models.Subscription;
import org.bustram.models.Ticket;
import org.bustram.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Cette classe permet de récuperer des Model d'après des lignes de données brutes renvoiée par la base données MySQL
 */
public class JDBCMapper {

    /**
     * Récupération d'un objet User
     *
     * @param row : Le ligne représentant les informations brutes de l'utilisateur en question
     */
    public static User getUser(List<String> row) {
        User user = new User();
        user.setId(getInt(row.get(0)));
        user.setName(row.get(1));
        user.setPassword(row.get(2));
        return user;
    }

    /**
     * Récupération d'un objet Subscription
     *
     * @param row : Le ligne représentant les informations brutes de l''abonnement en question
     */
    public static Subscription getSubscription(List<String> row) {
        Subscription subscription = new Subscription();
        subscription.setId(getInt(row.get(0)));
        subscription.setStartDate(DateHelper.getDate(row.get(1)));
        subscription.setEndDate(DateHelper.getDate(row.get(2)));
        subscription.setType(getInt(row.get(3)));
        subscription.setTicket(new Ticket(row.get(4)));
        return subscription;
    }

    /**
     * Récupération d'une liste d'utilisateurs
     *
     * @param rows : Une reprèsentation sous forme d'une liste bidimentionnelle des données des utilisateurs
     */
    public static List<User> getUsers(List<List<String>> rows) {
        List<User> users = new Vector<User>();
        for (List<String> row : rows) {
            users.add(getUser(row));
        }
        return users;
    }

    /**
     * Récupération d'une liste d'abonnements
     *
     * @param rows : Une reprèsentation sous forme d'une liste bidimentionnelle des données des abonnements
     */
    public static List<Subscription> getSubscriptions(List<List<String>> rows) {
        List<Subscription> subscriptions = new Vector<Subscription>();
        for (List<String> row : rows) {
            subscriptions.add(getSubscription(row));
        }
        return subscriptions;
    }

    /**
     * Une méthode pour convertir une chaîne de caractères vers un nombre INT
     */
    private static int getInt(String value) {
        return Integer.parseInt(value);
    }
}
