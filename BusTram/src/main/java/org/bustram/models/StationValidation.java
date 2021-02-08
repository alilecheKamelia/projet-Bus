package org.bustram.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StationValidation {
    private Ticket ticket;
    private Date validationDate;

    public StationValidation() {
    }

    public StationValidation(Ticket ticket, Date validationDate) {
        this.ticket = ticket;
        this.validationDate = validationDate;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Date getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Date validationDate) {
        this.validationDate = validationDate;
    }

    @Override
    public String toString() {
        return "StationValidation{" +
                "ticket=" + ticket +
                ", validationDate=" + validationDate +
                '}';
    }
}
