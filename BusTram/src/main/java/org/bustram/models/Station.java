package org.bustram.models;

import java.util.List;
import java.util.Vector;

public class Station {
    private String id;
    private String nickname;

    private List<StationValidation> stationValidations;
    private List<Path> paths;

    public Station() {
        stationValidations = new Vector<StationValidation>();
        paths = new Vector<Path>();
    }

    public Station(String id) {
        this.id = id;
        stationValidations = new Vector<StationValidation>();
        paths = new Vector<Path>();
    }

    public Station(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        stationValidations = new Vector<StationValidation>();
        paths = new Vector<Path>();
    }

    public Station(String id, String nickname, List<StationValidation> stationValidations, List<Path> paths) {
        this.id = id;
        this.nickname = nickname;
        this.stationValidations = stationValidations;
        this.paths = paths;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<StationValidation> getStationValidations() {
        return stationValidations;
    }

    public void setStationValidations(List<StationValidation> stationValidations) {
        this.stationValidations = stationValidations;
    }

    public void addStationValidation(StationValidation stationValidation) {
        this.stationValidations.add(stationValidation);
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public void assign(Path path) {
        if (!paths.contains(path)) paths.add(path);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", stationValidations=" + stationValidations +
                ", paths=" + paths +
                '}';
    }
}
