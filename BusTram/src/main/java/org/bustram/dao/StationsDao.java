package org.bustram.dao;

import org.bustram.dao.jdbc.DataBase;
import org.bustram.models.Path;
import org.bustram.models.Station;
import org.bustram.models.StationValidation;
import org.bustram.models.Ticket;

import java.util.List;

public interface StationsDao {
    // Other DBs setters
    void setJdbcDB(DataBase jdbcDB);

    Station add(Station station);
    Station get(Station station, boolean lazy);
    List<StationValidation> getValidations(Station station, boolean lazy);
    List<Station> getAll(boolean lazy);
    List<Station> getAll(Ticket ticket, boolean lazy);
    Station update(Station station);
    boolean delete(Station station, boolean soft);
    boolean deletePath(Path path);
    boolean deleteValidations(Ticket ticket);
}
