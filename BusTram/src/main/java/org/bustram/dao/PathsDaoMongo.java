package org.bustram.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bustram.dao.jdbc.DataBase;
import org.bustram.dao.mappers.MongoMapper;
import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.Path;
import org.bustram.models.Station;

import java.util.List;
import java.util.Vector;

/**
 * La classe permettant de gérer les Trajets sur MongoDB
 */
public class PathsDaoMongo implements PathsDao {

    /**
     * Le Nom de la collection des trajets
     */
    public static final String COLLECTION_NAME = "paths";

    /**
     * L'instance permettant d'accéder à la base de données MongoDB
     */
    private MongoDB mongoDB;

    /**
     * L'instance permettant d'accéder à la base de données MySQL
     */
    private DataBase jdbcDB;

    /**
     * La collection des trajets
     */
    private MongoCollection<Document> pathsCollection;

    /**
     * Constructeur de la classe
     */
    public PathsDaoMongo(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
        this.pathsCollection = this.mongoDB.getCollection(COLLECTION_NAME);
    }

    /**
     * Setter de l'instance d'accés à la base de données MySQL
     */
    public void setJdbcDB(DataBase jdbcDB) {
        this.jdbcDB = jdbcDB;
    }

    /**
     * La méthode pour ajouter un nouveau trajet sur la base de données
     *
     * @param path : Le trajet en question
     * @return : La méthode renvoie l'objet du trajet complet avec l'ID de ce dernier
     */
    public Path add(Path path) {
        Document pathDocument = MongoMapper.getDocument(path, true);
        this.pathsCollection.insertOne(pathDocument);
        return MongoMapper.getPath(pathDocument);
    }

    /**
     * La méthode pour récupérer un objet d'un trajet depuis la collection des trajets
     *
     * @param path : L'objet du trajet en question. le seul attribut requis est l'ID.
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé, y compris les Stations et StationValidations
     * @return : La méthode renvoie l'objet du trajet avec les details
     */
    public Path get(Path path, boolean lazy) {
        Path found;
        Document pathDocument = new Document("_id", new ObjectId(path.getId()));
        pathDocument = this.pathsCollection.find(pathDocument).first();

        // Map the path from Document to Model
        found = MongoMapper.getPath(pathDocument);

        if (!lazy) {
            found.setStations(getStations(found, true));
        }
        return found;
    }

    /**
     * La méthode pour récupérer la liste des Stations liée à un trajet bien déterminé
     *
     * @param path : L'objet complet du trajet en question.
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé, y compris les StationValidations
     * @return : La méthode renvoie une liste des stations avec les details
     */
    public List<Station> getStations(Path path, boolean lazy) {
        StationsDao stationsDao = new StationsDaoMongo(mongoDB);
        stationsDao.setJdbcDB(jdbcDB);

        List<Station> stations = new Vector<Station>();
        for (Station station : path.getStations()) {
            stations.add(stationsDao.get(station, lazy));
        }
        return stations;
    }

    /**
     * La méthode pour récupérer tout les trajet depuis depuis la base données
     *
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé
     * @return : La méthode renvoie la liste des objets des trajets avec les details
     */
    public List<Path> getAll(boolean lazy) {
        List<Path> paths = new Vector<Path>();
        FindIterable<Document> iterable = this.pathsCollection.find();
        for (Document document : iterable) {
            Path path = MongoMapper.getPath(document);
            if (!lazy) {
                path = get(path, false);
            }
            paths.add(path);
        }
        return paths;
    }

    /**
     * La méthode pour mettre à jour un certain trajet
     *
     * @param path : le trajet en question
     * @return : La dernière version du trajet
     */
    public Path update(Path path) {
        Document documentToUpdate = new Document("_id", new ObjectId(path.getId()));
        Document updatedDocument = MongoMapper.getDocument(path, false);
        this.pathsCollection.updateOne(documentToUpdate, new Document("$set", updatedDocument));
        return path;
    }

    /**
     * La méthode pour supprimer un certain trajet
     *
     * @param path : Le trajet en question
     * @param soft : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés au trajet en question, y compris les stations.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(Path path, boolean soft) {
        Document pathDocument = MongoMapper.getDocument(path, false);
        if (!soft) {
            StationsDao stationsDao = new StationsDaoMongo(mongoDB);
            stationsDao.setJdbcDB(jdbcDB);
            return this.pathsCollection.findOneAndDelete(pathDocument) != null && stationsDao.deletePath(path);
        } else {
            return this.pathsCollection.findOneAndDelete(pathDocument) != null;
        }
    }

    /**
     * La méthode pour supprimer une station depuis la liste des station d'un trajet bien determiné
     *
     * @param station : La station en question
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean deleteStation(Station station) {
        Document stationDocument = new Document();
        Document stationCriteria = new Document("stations", station.getId());
        Document stationQuery = new Document("$pull", stationCriteria);
        return this.pathsCollection.updateMany(stationDocument, stationQuery).wasAcknowledged();
    }
}
