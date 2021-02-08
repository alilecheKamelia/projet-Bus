package org.bustram.business;

import org.bustram.models.Station;
import org.bustram.models.Subscription;
import org.bustram.models.Ticket;
import org.bustram.models.User;

import java.util.List;

/**
 * Une interface permettant de définir les principaux services de l'application
 */
public interface Facade {

    /**
     * S'inscrire à la plate-forme
     */
    User register(User user);

    /**
     * Se désinscrire
     */
    boolean unregister(User user, boolean soft);

    /**
     * Se connecter à son espace personnel
     */
    User connect(User user);

    /**
     * Se déconnecter
     */
    boolean disconnect(User user);

    /**
     * Souscrire à un abonnement mensuel ou annuel
     */
    Subscription subscribe(Subscription subscription);

    /**
     * Commander un certain nombre de titres (vendables à l'unité ou par 10)
     */
    Ticket buy(Ticket ticket);

    List<Ticket> buy(List<Ticket> tickets);

    /**
     * Valider un titre de transport sur un terminal dédié
     */
    boolean validate(Ticket ticket, Station station);
}
