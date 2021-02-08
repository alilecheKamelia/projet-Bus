package org.bustram.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketValidation {
    private Station station;
    private Date validationDate;

    public TicketValidation() {
    }

    public TicketValidation(Station station, Date validationDate) {
        this.station = station;
        this.validationDate = validationDate;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Date getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Date validationDate) {
        this.validationDate = validationDate;
    }

    @Override
    public String toString() {
        return "TicketValidation{" +
                "station=" + station +
                ", validationDate=" + validationDate +
                '}';
    }
}
