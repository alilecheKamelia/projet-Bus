package org.bustram.dao;

import org.bustram.dao.jdbc.DataBase;
import org.bustram.models.Station;
import org.bustram.models.Ticket;
import org.bustram.models.TicketValidation;
import org.bustram.models.User;

import java.util.List;

public interface TicketsDao {
    // Other DBs setters
    void setJdbcDB(DataBase jdbcDB);

    Ticket add(Ticket ticket);
    Ticket get(Ticket ticket, boolean lazy);
    List<TicketValidation> getValidations(Ticket ticket, boolean lazy);
    List<Ticket> getAll(boolean lazy);
    List<Ticket> getAll(User user, boolean lazy);
    Ticket update(Ticket ticket);
    boolean delete(Ticket ticket, boolean soft);
    boolean delete(User user, boolean soft);
    boolean delete(List<Ticket> tickets, boolean soft);
    boolean deleteValidations(Station station);
}
