package org.bustram.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bustram.dao.jdbc.DataBase;
import org.bustram.dao.mappers.MongoMapper;
import org.bustram.dao.mongo.MongoDB;
import org.bustram.models.*;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

/**
 * La classe permettant de gérer les Stations sur MongoDB
 */
public class StationsDaoMongo implements StationsDao {

    /**
     * Le Nom de la collection des stations
     */
    public static final String COLLECTION_NAME = "stations";

    /**
     * L'instance permettant d'accéder à la base de données MongoDB
     */
    private MongoDB mongoDB;

    /**
     * L'instance permettant d'accéder à la base de données MySQL
     */
    private DataBase jdbcDB;

    /**
     * La collection des stations
     */
    private MongoCollection<Document> stationsCollection;

    /**
     * Constructeur de la classe
     */
    public StationsDaoMongo(MongoDB mongoDB) {
        this.mongoDB = mongoDB;
        this.stationsCollection = this.mongoDB.getCollection(COLLECTION_NAME);
    }

    /**
     * Setter de l'instance d'accés à la base de données MySQL
     */
    public void setJdbcDB(DataBase jdbcDB) {
        this.jdbcDB = jdbcDB;
    }

    /**
     * La méthode pour ajouter une nouvelle station sur la base de données
     *
     * @param station : La station en question
     * @return : La méthode renvoie l'objet de la station complet avec l'ID de cette dernière
     */
    public Station add(Station station) {
        Document stationDocument = MongoMapper.getDocument(station, true);
        this.stationsCollection.insertOne(stationDocument);
        station = MongoMapper.getStation(stationDocument);
        return station;
    }

    /**
     * La méthode pour récupérer un objet d'une station depuis la collection des stations
     *
     * @param station : L'objet de la station en question. le seul attribut requis est l'ID.
     * @param lazy    : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie l'objet de la station avec les details.
     */
    public Station get(Station station, boolean lazy) {
        Station found;
        Document stationDocument = new Document("_id", new ObjectId(station.getId()));
        stationDocument = this.stationsCollection.find(stationDocument).first();

        // Map the station from Document to Model
        found = MongoMapper.getStation(stationDocument);

        if (!lazy) {
            // Station Validations
            found.setStationValidations(getValidations(found, true));
            found.setPaths(getPaths(found, true));
        }

        return found;
    }

    /**
     * La méthode pour récupérer les validations de la station en question
     *
     * @param station : La station en question
     * @param lazy    : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des validation de la station.
     */
    public List<StationValidation> getValidations(Station station, boolean lazy) {
        TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
        ticketsDao.setJdbcDB(jdbcDB);

        List<StationValidation> stationValidations = station.getStationValidations();
        for (StationValidation stationValidation : stationValidations) {
            stationValidation.setTicket(ticketsDao.get(stationValidation.getTicket(), lazy));
        }
        return stationValidations;
    }

    /**
     * La méthode pour récupérer les trajets auxquel appartient la station en question
     *
     * @param station : La station en question
     * @param lazy    : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des trajets.
     */
    public List<Path> getPaths(Station station, boolean lazy) {
        PathsDao pathsDao = new PathsDaoMongo(mongoDB);
        pathsDao.setJdbcDB(jdbcDB);

        List<Path> paths = new Vector<Path>();
        for (Path path : station.getPaths()) {
            paths.add(pathsDao.get(path, true));
        }
        return paths;
    }

    /**
     * La méthode pour récupérer tout les Stations sur la collection des station
     *
     * @param lazy : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des stations.
     */
    public List<Station> getAll(boolean lazy) {
        List<Station> stations = new Vector<Station>();
        FindIterable<Document> iterable = this.stationsCollection.find();
        for (Document document : iterable) {
            Station station = MongoMapper.getStation(document);
            if (!lazy) {
                station = get(station, false);
            }
            stations.add(station);
        }
        return stations;
    }

    /**
     * La méthode retourne toutes les stations qui ont été validées à l'aide du ticket actuel
     *
     * @param ticket : Le ticket en question
     * @param lazy   : Une valeur booléenne pour indiquer s'il faut obtenir l'objet complet de chaque objet associé.
     * @return : La méthode renvoie la liste des stations.
     */
    public List<Station> getAll(Ticket ticket, boolean lazy) {
        List<Station> stations = new Vector<Station>();
        Document stationCriteria = new Document("station_validations.ticket_id", ticket.getId());
        FindIterable<Document> iterable = this.stationsCollection.find(stationCriteria);
        for (Document document : iterable) {
            Station station = MongoMapper.getStation(document);
            if (!lazy) {
                station = get(station, false);
            }
            stations.add(station);
        }
        return stations;
    }

    /**
     * La méthode pour mettre à jour une certaine station
     *
     * @param station : la station en question
     * @return : La dernière version de la station
     */
    public Station update(Station station) {
        Document documentToUpdate = new Document("_id", new ObjectId(station.getId()));
        Document updatedDocument = MongoMapper.getDocument(station, false);
        this.stationsCollection.updateOne(documentToUpdate, new Document("$set", updatedDocument));
        return station;
    }

    /**
     * La méthode pour supprimer une certaine station
     *
     * @param station : La station en question
     * @param soft    : Une valeur booléenne pour indiquer s'il faut supprimer tout les objet associés à la station en question.
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean delete(Station station, boolean soft) {
        Document stationDocument = MongoMapper.getDocument(station, false);
        if (!soft) {
            // Delete ticket validations
            TicketsDao ticketsDao = new TicketsDaoMongo(mongoDB);
            ticketsDao.setJdbcDB(jdbcDB);

            // Delete stations of paths
            PathsDao pathsDao = new PathsDaoMongo(mongoDB);
            pathsDao.setJdbcDB(jdbcDB);

            return this.stationsCollection.findOneAndDelete(stationDocument) != null && ticketsDao.deleteValidations(station) && pathsDao.deleteStation(station);
        } else {
            return this.stationsCollection.findOneAndDelete(stationDocument) != null;
        }
    }

    /**
     * La méthode pour supprimer un trajet depuis toutes les stations sur la collection des stations
     *
     * @param path : Le trajet en question
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean deletePath(Path path) {
        Document pathDocument = new Document();
        Document pathCriteria = new Document("paths", path.getId());
        Document pathQuery = new Document("$pull", pathCriteria);
        return this.stationsCollection.updateMany(pathDocument, pathQuery).wasAcknowledged();
    }

    /**
     * la méthode supprime toutes les validations du ticket courant dans toutes les stations existantes
     *
     * @param ticket : Le ticket en question
     * @return : Une valeur booléenne pour indiquer si la supression a été efféctuée avec succès
     */
    public boolean deleteValidations(Ticket ticket) {
        Document validationDocument = new Document();
        Document validationCriteria = new Document("station_validations", new Document("ticket_id", ticket.getId()));
        Document validationQuery = new Document("$pull", validationCriteria);
        return this.stationsCollection.updateMany(validationDocument, validationQuery).wasAcknowledged();
    }
}
