package org.bustram.business;

import org.bustram.dao.*;
import org.bustram.helpers.DateHelper;
import org.bustram.models.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Cette classe permet d'accéder à tous les services de l'application
 */
public class FacadeDefault implements Facade {
    
    /**
     * Pour que la classe Facade fonctionne correctement, il faut fournir
     * toutes les références vers les classes DAO afin que cette dernière
     * puisse gérer les dépendances entres les enregistrement de notre Bases de de données
     */
    private UsersDao usersDao;
    private SubscriptionsDao subscriptionsDao;
    private TicketsDao ticketsDao;
    private StationsDao stationsDao;
    private PathsDao pathsDao;

    /**
     * Le constructeur de la classe
     */
    public FacadeDefault(UsersDao usersDao, SubscriptionsDao subscriptionsDao, TicketsDao ticketsDao, StationsDao stationsDao, PathsDao pathsDao) {
        this.usersDao = usersDao;
        this.subscriptionsDao = subscriptionsDao;
        this.ticketsDao = ticketsDao;
        this.stationsDao = stationsDao;
        this.pathsDao = pathsDao;
    }

    /**
     * La méthode register(...) appelle la méthode add(...) de la couche DAO afin d'insérer un nouvel utilisateur dans la base de données.
     * L'objet renvoyé par la couche DAO a l'ID inséré.
     */
    public User register(User user) {
        return usersDao.add(user);
    }

    /**
     * La méthode unregister(...) permet de supprimer un utilisateur de la base de données. Il utilise la méthode delete(...) de la couche DAO.
     *
     * @param soft : Ce paramètre indique s'il faut effectuer une opération de suppression de l'utilisateur ainsi que toutes ses données existantes y compris son titres et ses validations
     */
    public boolean unregister(User user, boolean soft) {
        // Obtenir l'objet utilisateur complet
        user = usersDao.get(user);
        if (soft) {
            return usersDao.delete(user, true);
        } else {
            // Supprimer l'utilisateur ainsi que ses tickets (ce qui supprime en retour toutes les validations de station de toutes les stations associées)
            return usersDao.delete(user, false) && ticketsDao.delete(user, false);
        }
    }

    /**
     * La méthode connect(...) effectue simplement une vérification par rapport à la base de données de l'objet utilisateur à l'aide de la couche DAO
     * la méthode renvoie un objet utilisateur complet
     *
     * @param user : Ce paramètre devrait avoir à la fois le nom et le champ de mot de passe renseignés avec des valeurs valides
     */
    public User connect(User user) {
        return usersDao.check(user);
    }

    /**
     * la méthode disconnects(...) doit être implémentée du côté client de l'application car elle doit détruire la session ainsi que gérer les cookies
     */
    public boolean disconnect(User user) {
        System.out.println("DISCONNECT HAS TO BE IMPLEMENTED ON THE CLIENT SIDE");
        return false;
    }

    /**
     * La méthode subscribe(...) utilise la couche DAO afin d'ajouter un nouvel abonnement à la base de données.
     *
     * @param subscription : Ce paramètre contient toutes les données nécessaires à l'exception des identifiants
     */
    public Subscription subscribe(Subscription subscription) {
        // Le ticket attibuer au abonnement doit être inséré en premier pour acquérir son l'identifiant
        // Puis attribuez l'objet de ticket complet à l'abonnement
        subscription.setTicket(ticketsDao.add(subscription.getTicket()));
        // Enfin nous appelons la couche DAO qui insère le nouvel abonnement
        return subscriptionsDao.add(subscription);
    }

    /**
     * Cette méthode utilise la couche DAO pour insérer nouveau ticket dans la base de données
     *
     * @param ticket : Ce paramètre contient toutes les données nécessaires à l'exception de l'identifiant
     */
    public Ticket buy(Ticket ticket) {
        return ticketsDao.add(ticket);
    }

    /**
     * Cette méthode permet d'ajouter un certain nombre de ticket sur la base de données
     *
     * @param tickets : La liste des tickets qui doivent être persistées
     */
    public List<Ticket> buy(List<Ticket> tickets) {
        List<Ticket> fullTickets = new Vector<Ticket>();
        for (Ticket ticket : tickets) {
            fullTickets.add(buy(ticket));
        }
        return fullTickets;
    }

    /**
     * la méthode validate(...) permet de valider un certain ticket dans une terminal appartenant à une station spécifique
     *
     * @param ticket  : Un objet complet avec son identifiant qui référence le ticket en cours de validation
     * @param station : Un objet complet avec son identifiant qui fait référence à la station dans laquelle le ticket est en cours de validation
     */
    public boolean validate(Ticket ticket, Station station) {
        if (ticket.getStatus() == Ticket.ACTIVE) {
            // Ajouter l'objet TicketValidation à la liste des validations du ticket
            TicketValidation ticketValidation = new TicketValidation();
            ticketValidation.setStation(station);
            ticketValidation.setValidationDate(new Date());
            ticket.addTicketValidation(ticketValidation);

            // Ajouter l'objet StationValidation à la liste des validations de la station
            StationValidation stationValidation = new StationValidation();
            stationValidation.setTicket(ticket);
            stationValidation.setValidationDate(new Date());
            station.addStationValidation(stationValidation);

            // Si le ticket est consommable, décrémentez la valeur de allowedUses
            if (ticket.isConsumable()) {
                ticket.setAllowedUses(ticket.getAllowedUses() - 1);
            }

            // Mettre à jour le statut du ticket
            ticket.setStatus(getStatus(ticket));

            // Mettre à jour la station ainsi que le ticket dans la DB
            ticketsDao.update(ticket);
            stationsDao.update(station);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Méthode pour obtenir le statut du ticket
     *
     * @param ticket : l'objet du ticket en cours de validation
     */
    private int getStatus(Ticket ticket) {
        int status = Ticket.EXPIRED;
        if (ticket.isConsumable()) {
            // Le ticket est un ticket normal
            if (ticket.getAllowedUses() > 0) {
                status = Ticket.ACTIVE;
            }
        } else {
            // Le ticket est un ticket d'abonnement
            Subscription subscription = subscriptionsDao.get(ticket, true);
            // Nous appelons la classe DateHelper afin de vérifier si le ticket est expiré
            if (DateHelper.during(subscription.getStartDate(), subscription.getEndDate(), new Date())) {
                status = Ticket.ACTIVE;
            }
        }
        return status;
    }
}
