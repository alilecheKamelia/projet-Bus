package org.bustram.models;

import org.bustram.helpers.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Subscription {
    public static final int MONTHLY = 0;
    public static final int ANNUALLY = 1;

    private int id;
    private Date startDate;
    private Date endDate;
    private int type;

    private Ticket ticket;

    public Subscription() {
    }

    public Subscription(int id) {
        this.id = id;
    }

    public Subscription(int id, Date startDate, Date endDate, int type) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
    }

    public Subscription(int id, Date startDate, Date endDate, int type, Ticket ticket) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.ticket = ticket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", start_date=" + DateHelper.getDate(startDate) +
                ", end_date=" + DateHelper.getDate(endDate) +
                ", type=" + type +
                ", ticket=" + ticket +
                '}';
    }
}
