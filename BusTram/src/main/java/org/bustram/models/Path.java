package org.bustram.models;

import java.util.List;
import java.util.Vector;

public class Path {
    private String id;
    private String nickname;

    private List<Station> stations;

    public Path() {
        stations = new Vector<Station>();
    }

    public Path(String id) {
        this.id = id;
        stations = new Vector<Station>();
    }

    public Path(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
        stations = new Vector<Station>();
    }

    public Path(String id, String nickname, List<Station> stations) {
        this.id = id;
        this.nickname = nickname;
        this.stations = stations;
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

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void assign(Station station) {
        if (!stations.contains(station)) stations.add(station);
    }

    @Override
    public String toString() {
        return "Path{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", stations=" + stations +
                '}';
    }
}
