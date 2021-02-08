package org.bustram.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * La classe permettant de communiquer avec une base de données MongoDB
 */
public class MongoDB {

    /**
     * Informations de connexion
     */
    public static final String MONGO_BRIDGE = "mongodb:";
    private String userName;
    private String password;
    private String host;
    private String port;
    private String source;
    private String userCredentialsCollectionDB;

    /**
     * Constructeur de la classe
     */
    public MongoDB(String userName, String password, String host, String port, String source) {
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
        this.source = source;
        this.userCredentialsCollectionDB = "admin";
    }

    /**
     * Constructeur de la classe
     * @param userCredentialsCollectionDB : La collection de la base de données contenant les informations d'identification des utilisateurs
     */
    public MongoDB(String userName, String password, String host, String port, String source, String userCredentialsCollectionDB) {
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.port = port;
        this.source = source;
        this.userCredentialsCollectionDB = userCredentialsCollectionDB;
    }

    /**
     * Method pour récupérer une référence vers une collection d'après son nom
     * @param name : Le nom de la collection
     * @return : La référence vers la collection
     */
    public MongoCollection<Document> getCollection(String name) {
        try {
            String uri = MONGO_BRIDGE + "//" + userName + ":" + password + "@" + host + ":" + port + "/" + source + "?authSource=" + userCredentialsCollectionDB;
            MongoClientURI clientURI = new MongoClientURI(uri);
            MongoClient mongoClient = new MongoClient(clientURI);
            MongoDatabase mongoDatabase = mongoClient.getDatabase(this.source);
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(name);
            return mongoCollection;
        } catch (Exception e) {
            System.out.println("Erreur sur getConnection >> " + e.getMessage());
            return null;
        }
    }
}
