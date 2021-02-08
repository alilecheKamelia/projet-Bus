package org.bustram.dao;

import org.bustram.dao.jdbc.DataBase;
import org.bustram.dao.mappers.JDBCMapper;
import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * La classe permettant de gérer les utilisateurs sur MySQL
 */
public class UsersDaoJDBC implements UsersDao {

    /**
     * La variable indiquant le nom de la table des utilisateurs sur MySQL
     */
    public static final String TABLE_NAME = "Users";

    /**
     * La clé primaire de la table des utilisateurs
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
    public UsersDaoJDBC(DataBase db) {
        this.db = db;
    }

    /**
     * Setter de l'instance d'accés à la base de données MongoDB
     */
    public void setMongoDB(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
    }

    /**
     * La méthode pour ajouter un nouveau utilisateur sur la base de données
     *
     * @param user : L'utilisateur en question
     * @return : La méthode renvoie l'objet complet du nouvel utilisateur avec l'ID du nouvel enregistrement
     */
    public User add(User user) {
        return (User) db.insert(user, TABLE_NAME);
    }

    /**
     * La méthode pour récupérer un objet complet d'un certain utilisateur
     *
     * @param user : L'objet de l'utilisateur en question. le seul attribut requis est l'ID.
     * @return : La méthode renvoie l'objet de l'utilisateur avec les details.
     */
    public User get(User user) {
        return JDBCMapper.getUser(db.selectOne(TABLE_NAME, PRIMARY_KEY, user.getId()));
    }

    /**
     * La méthode pour verifier l'existance d'un objet d'un certain utilisateur d'après son Nom et Mot de passe
     *
     * @param user : L'objet de l'utilisateur en question. les seuls attributs obligatoires sont le Nom et le Mot de passe.
     * @return : La méthode renvoie l'objet de l'utilisateur avec les details.
     */
    public User check(User user) {
        Map<String, String> keyValuePairs = new HashMap<String, String>();
        keyValuePairs.put("Name", user.getName());
        keyValuePairs.put("Password", user.getPassword());
        return JDBCMapper.getUser(db.selectOne(TABLE_NAME, keyValuePairs));
    }

    /**
     * La méthode pour récupérer la liste de tout les utilisateurs
     *
     * @return : La liste des utilisateurs
     */
    public List<User> getAll() {
        return JDBCMapper.getUsers(db.select(TABLE_NAME));
    }

    /**
     * La méthode pour mettre à jour l'objet d'un certain utilisateur
     *
     * @param user : L'utilisateur en question
     * @return : La méthode renvoie un objet contnant les dernières informations de l'utilisateur
     */
    public User update(User user) {
        return (User) db.update(user, TABLE_NAME);
    }

    /**
     * La méthode pour supprimer un certain utilisateur.
     *
     * @param user : L'utilisateur en question
     * @param soft : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés au utilisateur en question.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(User user, boolean soft) {
        if (!soft) {
            // Delete the tickets
            TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
            ticketsDao.setJdbcDB(db);
            return ticketsDao.delete(user, false) && db.delete(user, TABLE_NAME);
        } else {
            return db.delete(user, TABLE_NAME);
        }
    }
}
