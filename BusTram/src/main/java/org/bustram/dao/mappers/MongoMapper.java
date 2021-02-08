package org.bustram.dao.mappers;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bustram.dao.UsersDao;
import org.bustram.dao.UsersDaoJDBC;
import org.bustram.helpers.DateHelper;
import org.bustram.models.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Cette classe permet de réaliser Une conversion depuis des objets Model vers des Document BSON et vice-versa
 */
public class MongoMapper {
    /**
     * Obtenir un document Bson à partir d'un objet modèle
     *
     * @param ticket:  Objet en question
     * @param ignoreID : Une valeur booléenne indiquant s'il faut ignorer l'ID de l'objet à convertir ou pas
     */
    public static Document getDocument(Ticket ticket, boolean ignoreID) {
        Document ticketDocument = ignoreID ? new Document() : new Document("_id", new ObjectId(ticket.getId()));
        ticketDocument.append("user_id", ticket.getOwner().getId());
        ticketDocument.append("status", ticket.getStatus());
        ticketDocument.append("allowed_uses", ticket.getAllowedUses());
        ticketDocument.append("consumable", ticket.isConsumable());
        ticketDocument.append("price", ticket.getPrice());

        List<Document> ticketValidationsDocuments = new Vector<Document>();
        for (TicketValidation ticketValidation : ticket.getTicketValidations()) {
            ticketValidationsDocuments.add(getDocument(ticketValidation));
        }
        ticketDocument.append("ticket_validations", ticketValidationsDocuments);

        return ticketDocument;
    }

    /**
     * Obtenir un document Bson à partir d'un objet modèle
     *
     * @param station: Objet en question
     * @param ignoreID : Une valeur booléenne indiquant s'il faut ignorer l'ID de l'objet à convertir ou pas
     */
    public static Document getDocument(Station station, boolean ignoreID) {
        Document stationDocument = ignoreID ? new Document() : new Document("_id", new ObjectId(station.getId()));
        stationDocument.append("nickname", station.getNickname());

        List<Document> stationValidationsDocuments = new Vector<Document>();
        for (StationValidation stationValidation : station.getStationValidations()) {
            stationValidationsDocuments.add(getDocument(stationValidation));
        }
        stationDocument.append("station_validations", stationValidationsDocuments);

        List<String> pathIDs = new Vector<String>();
        for (Path path : station.getPaths()) {
            pathIDs.add(path.getId());
        }
        stationDocument.append("paths", pathIDs);

        return stationDocument;
    }

    /**
     * Obtenir un document Bson à partir d'un objet modèle
     *
     * @param path:    Objet en question
     * @param ignoreID : Une valeur booléenne indiquant s'il faut ignorer l'ID de l'objet à convertir ou pas
     */
    public static Document getDocument(Path path, boolean ignoreID) {
        Document pathDocument = ignoreID ? new Document() : new Document("_id", new ObjectId(path.getId()));
        pathDocument.append("nickname", path.getNickname());

        List<String> stationIDs = new Vector<String>();
        for (Station station : path.getStations()) {
            stationIDs.add(station.getId());
        }
        pathDocument.append("stations", stationIDs);

        return pathDocument;
    }

    /**
     * Obtenir un document Bson à partir d'un objet modèle
     *
     * @param ticketValidation: Objet en question
     */
    public static Document getDocument(TicketValidation ticketValidation) {
        Document ticketValidationDocument = new Document();
        ticketValidationDocument.append("station_id", ticketValidation.getStation().getId());
        ticketValidationDocument.append("validation_date", DateHelper.getDate(ticketValidation.getValidationDate()));
        return ticketValidationDocument;
    }

    /**
     * Obtenir un document Bson à partir d'un objet modèle
     *
     * @param stationValidation: Objet en question
     */
    public static Document getDocument(StationValidation stationValidation) {
        Document ticketValidationDocument = new Document();
        ticketValidationDocument.append("ticket_id", stationValidation.getTicket().getId());
        ticketValidationDocument.append("validation_date", DateHelper.getDate(stationValidation.getValidationDate()));
        return ticketValidationDocument;
    }

    /**
     * Obtenir un objet modèle à partir d'un document Bson
     *
     * @param document : Document en question
     * @return : L'objet Modèle
     */
    public static Ticket getTicket(Document document) {
        Ticket ticket = new Ticket();
        ticket.setId(document.get("_id").toString());
        ticket.setOwner(new User(document.getInteger("user_id")));
        ticket.setStatus(document.getInteger("status"));
        ticket.setAllowedUses(document.getInteger("allowed_uses"));
        ticket.setConsumable(document.getBoolean("consumable"));
        ticket.setPrice(document.getDouble("price"));

        List<TicketValidation> ticketValidations = new Vector<TicketValidation>();
        for (Document d : ((List<Document>) document.get("ticket_validations"))) {
            ticketValidations.add(getTicketValidation(d));
        }
        ticket.setTicketValidations(ticketValidations);

        return ticket;
    }

    /**
     * Obtenir un objet modèle à partir d'un document Bson
     *
     * @param document : Document en question
     * @return : L'objet Modèle
     */
    public static TicketValidation getTicketValidation(Document document) {
        TicketValidation ticketValidation = new TicketValidation();
        ticketValidation.setStation(new Station(document.getString("station_id")));
        ticketValidation.setValidationDate(DateHelper.getDate(document.getString("validation_date")));
        return ticketValidation;
    }

    /**
     * Obtenir un objet modèle à partir d'un document Bson
     *
     * @param document : Document en question
     * @return : L'objet Modèle
     */
    public static Station getStation(Document document) {
        Station station = new Station();
        station.setId(document.get("_id").toString());
        station.setNickname(document.getString("nickname"));

        List<StationValidation> stationValidations = new Vector<StationValidation>();
        for (Document d : ((List<Document>) document.get("station_validations"))) {
            stationValidations.add(getStationValidation(d));
        }
        station.setStationValidations(stationValidations);

        List<Path> paths = new Vector<Path>();
        for (String pathID : ((List<String>) document.get("paths"))) {
            paths.add(new Path(pathID));
        }
        station.setPaths(paths);

        return station;
    }

    /**
     * Obtenir un objet modèle à partir d'un document Bson
     *
     * @param document : Document en question
     * @return : L'objet Modèle
     */
    public static StationValidation getStationValidation(Document document) {
        StationValidation stationValidation = new StationValidation();
        stationValidation.setTicket(new Ticket(document.getString("ticket_id")));
        stationValidation.setValidationDate(DateHelper.getDate(document.getString("validation_date")));
        return stationValidation;
    }

    /**
     * Obtenir un objet modèle à partir d'un document Bson
     *
     * @param document : Document en question
     * @return : L'objet Modèle
     */
    public static Path getPath(Document document) {
        Path path = new Path();
        path.setId(document.get("_id").toString());
        path.setNickname(document.getString("nickname"));

        List<Station> stations = new Vector<Station>();
        for (String stationID : (List<String>) document.get("stations")) {
            stations.add(new Station(stationID));
        }
        path.setStations(stations);

        return path;
    }
}
