package org.bustram.dao;

import org.bustram.dao.jdbc.DataBase;
import org.bustram.models.Path;
import org.bustram.models.Station;

import java.util.List;

public interface PathsDao {
    // Other DBs setters
    void setJdbcDB(DataBase jdbcDB);

    Path add(Path path);
    Path get(Path path, boolean lazy);
    List<Path> getAll(boolean lazy);
    Path update(Path path);
    boolean delete(Path path, boolean soft);
    boolean deleteStation(Station station);
}
