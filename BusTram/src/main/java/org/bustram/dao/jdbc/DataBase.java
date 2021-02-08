package org.bustram.dao.jdbc;

import org.bustram.dao.SubscriptionsDaoJDBC;
import org.bustram.dao.UsersDaoJDBC;
import org.bustram.helpers.DateHelper;
import org.bustram.models.Subscription;
import org.bustram.models.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * La classe Database a un accès direct à la base de données MySQL
 * Elle permet d'effectuer tout type d'opération CRUD sur les tables de la base de données
 */
public class DataBase {

    /**
     * Il faut déclarer une référence vers un objet qui implémente l'interface DataSource qui représente la source de données JDBC
     * dans notre cas on a optè pour MySQLDataSource qui permet d'accéder au base de données MySQL
     */
    private DataSource dataSource;
    /**
     * Il faut aussi déclarer une référence vers la connection
     */
    private Connection connection;

    /**
     * Le constructeur de la classe DataBase
     */
    public DataBase(DataSource DataSource) {
        setDataSource(DataSource);
    }

    /**
     * Setter du dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
    }

    /**
     * Cette méthode exécute une requête sql de type SELECT
     *
     * @param query : Une chaine de caractères contenant la requête SQL
     * @return : Il renvoie une représentation imbriquée du résultat sous form de liste bidimensionnelle
     */
    private List<List<String>> executeSelect(String query) {
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();

            List<List<String>> data = new Vector<List<String>>();
            while (rs.next()) {
                List<String> row = new Vector<String>();
                for (int i = 0; i < columnCount; i++) {
                    row.add(rs.getString(i + 1));
                }
                data.add(row);
            }
            statement.close();
            rs.close();
            return data;
        } catch (Exception e) {
            System.out.println("Erreur sur executeSelect() " + e.getMessage());
            return null;
        }
    }

    /**
     * Cette méthode exécute une requête sql de type UPDATE/INSERT
     *
     * @param query : Une chaine de caractères contenant la requête SQL
     * @return : Il renvoie l'ID de ligne modifiée/inserée sur la base de données
     */
    private int executeUpdate(String query) {
        int affectedRecordKey = -1;
        try {
            Statement statement = connection.createStatement();
            affectedRecordKey = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                affectedRecordKey = generatedKeys.getInt(1);
            }

            statement.close();
        } catch (Exception e) {
            System.out.println("Erreur sur executeUpdate() " + e.getMessage());
        }
        return affectedRecordKey;
    }

    /**
     * Cette méthode exécute une requête sql de type SELECT sur la table passée en paramètre
     *
     * @param tableName : La table en question
     * @return : Il renvoie une représentation imbriquée du résultat sous form de liste bidimensionnelle
     */
    public List<List<String>> select(String tableName) {
        String query = "SELECT * FROM " + tableName;
        return executeSelect(query);
    }

    /**
     * Cette méthode exécute une requête sql de type SELECT sur la table passée en paramètre avec condition sur un champs bien déterminé
     *
     * @param tableName : La table en question
     * @param key       : Le champs voulu
     * @param value     : la valeur à tester
     * @return : Il renvoie une représentation imbriquée du résultat sous form de liste bidimensionnelle
     */
    public List<List<String>> select(String tableName, String key, Object value) {
        String query = "SELECT * FROM " + tableName + " WHERE " + key + " = '" + value + "'";
        return executeSelect(query);
    }

    /**
     * Cette méthode exécute une requête sql de type SELECT sur la table passée en paramètre avec condition sur un champs bien déterminé
     *
     * @param tableName : La table en question
     * @param key       : Le champs voulu
     * @param value     : la valeur à tester
     * @return : Il renvoie la première ligne trouvée sous forme d'une liste de valeurs
     */
    public List<String> selectOne(String tableName, String key, Object value) {
        String query = "SELECT * FROM " + tableName + " WHERE " + key + " = '" + value + "'";
        return executeSelect(query).get(0);
    }

    /**
     * Cette méthode exécute une requête sql de type SELECT sur la table passée en paramètre avec condition sur des champs bien déterminés
     *
     * @param tableName     : La table en question
     * @param keyValuePairs : Le Map<Key, Value> des champs voulus
     * @return : Il renvoie une représentation du premier résultat sous forme de liste des valeurs
     */
    public List<String> selectOne(String tableName, Map<String, String> keyValuePairs) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
        for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
            query.append(entry.getKey()).append(" = '").append(entry.getValue()).append("' AND ");
        }
        query.replace(query.lastIndexOf(" AND "), query.lastIndexOf(" AND ") + 5, ";");
        return executeSelect(query.toString()).get(0);
    }

    /**
     * Cette méthode insère un nouvel enregistrement du paramètre 'record' sur la base de données
     *
     * @param record    : Cet objet peut être une instance de la classe Subscription (Insersion d'un abonnement) ou bien User (Insersion d'un nouvel utilisateur)
     * @param tableName : La table en question
     * @return : Il renvoie le objet complet avec l'ID récupéré après l'insersion sur la base de données
     */
    public Object insert(Object record, String tableName) {
        if (UsersDaoJDBC.TABLE_NAME.equals(tableName)) {
            User user = (User) record;
            // Construire la requête
            String query = "INSERT INTO " + tableName + "(name, password) VALUES('" + user.getName() + "', '" + user.getPassword() + "');";
            // Obtener la clé générée
            int generatedKey = executeUpdate(query);
            user.setId(generatedKey);
            // Retourner l'objet complet
            return user;
        } else if (SubscriptionsDaoJDBC.TABLE_NAME.equals(tableName)) {
            // Construire la requête
            Subscription subscription = (Subscription) record;
            String query = "INSERT INTO " + tableName + "(start_date, end_date, type, ticket_id) VALUES(" +
                    "'" + DateHelper.getDate(subscription.getStartDate()) + "'" +
                    ", '" + DateHelper.getDate(subscription.getEndDate()) + "'" +
                    ", '" + subscription.getType() + "'" +
                    ", '" + subscription.getTicket().getId() + "'" +
                    ");";
            // Obtener la clé générée
            int generatedKey = executeUpdate(query);
            subscription.setId(generatedKey);
            // Retourner l'objet complet
            return subscription;
        } else {
            System.out.println("Unrecognized table name.");
            return null;
        }
    }

    /**
     * Cette méthode met à jour l'enregistrement 'record' passé paramètre
     *
     * @param record    : Cet objet peut être une instance de la classe Subscription (Insersion d'un abonnement) ou bien User (Insersion d'un nouvel utilisateur)
     * @param tableName : La table en question
     * @return : Il renvoie la dernière version de l'objet en cours de modification
     */
    public Object update(Object record, String tableName) {
        if (UsersDaoJDBC.TABLE_NAME.equals(tableName)) {
            User user = (User) record;
            String query = "UPDATE " + tableName + " SET name='" + user.getName() + "', password='" + user.getPassword() + "' WHERE " + UsersDaoJDBC.PRIMARY_KEY + "=" + user.getId() + ";";
            executeUpdate(query);
            return user;
        } else if (SubscriptionsDaoJDBC.TABLE_NAME.equals(tableName)) {
            Subscription subscription = (Subscription) record;
            String query = "UPDATE " + tableName + " SET start_date='" + DateHelper.getDate(subscription.getStartDate()) + "'" +
                    ", end_date='" + DateHelper.getDate(subscription.getEndDate()) + "'" +
                    ", type='" + subscription.getType() + "'" +
                    ", ticket_id='" + subscription.getTicket().getId() + "'" +
                    " WHERE " + SubscriptionsDaoJDBC.PRIMARY_KEY + "=" + subscription.getId() + ";";
            executeUpdate(query);
            return subscription;
        } else {
            System.out.println("Unrecognized table name.");
            return null;
        }
    }

    /**
     * Cette méthode permet de supprimer un enregistrement d'une table sur la base de données
     * @param record : L'objet à supprimer. (Subscription ou bien User)
     * @param tableName : La table en question
     * @return : Il renvoie une valeur booléenne indiquant si la suppréssion a été réalisée evec succés
     */
    public boolean delete(Object record, String tableName) {
        if (UsersDaoJDBC.TABLE_NAME.equals(tableName)) {
            User user = (User) record;
            String query = "DELETE FROM " + tableName + " WHERE " + UsersDaoJDBC.PRIMARY_KEY + "=" + user.getId() + ";";
            executeUpdate(query);
            return true;
        } else if (SubscriptionsDaoJDBC.TABLE_NAME.equals(tableName)) {
            Subscription subscription = (Subscription) record;
            String query = "DELETE FROM " + tableName + " WHERE " + SubscriptionsDaoJDBC.PRIMARY_KEY + "=" + subscription.getId() + ";";
            executeUpdate(query);
            return true;
        } else {
            System.out.println("Unrecognized table name.");
            return false;
        }
    }
}
