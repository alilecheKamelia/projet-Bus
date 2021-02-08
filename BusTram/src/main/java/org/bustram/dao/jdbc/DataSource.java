package org.bustram.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * La classe permettant de reprèsenter une connexion JDBC
 */
public class DataSource {

    /**
     * Pour se connecter à une source de données, il nous faut 4 données
     */
    private String url;
    private String driver; // Le QualifiedName du classe : driver qui joue le rôle du point d'entrée pour la BD
    private String userName;
    private String password;

    /**
     * Constructeur de la classe
     */
    public DataSource(String url, String driver, String userName, String password) {
        super();
        this.url = url;
        this.driver = driver;
        this.userName = userName;
        this.password = password;
    }

    /**
     * La méthode permettant de récupérer une instance de type Connection utilisée pour exécuter les requêtes sur la base de données
     */
    public Connection getConnection() {
        try {
            // 1. Chargement du Driver :
            Class.forName(driver);

            // 2. Connexion :
            Connection db = DriverManager.getConnection(url, userName, password);
            System.out.println("Connexion bien établie !");
            return db;

        } catch (Exception e) {
            System.out.println("Erreur sur getConnection >> " + e.getMessage());
            return null;
        }
    }
}
