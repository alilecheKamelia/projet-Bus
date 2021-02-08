package org.bustram.models;

import java.util.List;
import java.util.Vector;

public class Ticket {
    public static final int EXPIRED = 0;
    public static final int ACTIVE = 1;

    private String id;
    private User owner;
    private int status;
    private int allowedUses;
    private boolean consumable;
    private double price;

    private List<TicketValidation> ticketValidations;

    public Ticket() {
        ticketValidations = new Vector<TicketValidation>();
    }

    public Ticket(String id) {
        this.id = id;
        ticketValidations = new Vector<TicketValidation>();
    }

    public Ticket(String id, User owner, int status, int allowedUses, boolean consumable, double price) {
        this.id = id;
        this.owner = owner;
        this.status = status;
        this.allowedUses = allowedUses;
        this.consumable = consumable;
        ticketValidations = new Vector<TicketValidation>();
    }

    public Ticket(String id, User owner, int status, int allowedUses, boolean consumable, double price, List<TicketValidation> ticketValidations) {
        this.id = id;
        this.owner = owner;
        this.status = status;
        this.allowedUses = allowedUses;
        this.consumable = consumable;
        this.ticketValidations = ticketValidations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAllowedUses() {
        return allowedUses;
    }

    public void setAllowedUses(int allowedUses) {
        this.allowedUses = allowedUses;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public void setConsumable(boolean consumable) {
        this.consumable = consumable;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public List<TicketValidation> getTicketValidations() {
        return ticketValidations;
    }

    public void setTicketValidations(List<TicketValidation> ticketValidations) {
        this.ticketValidations = ticketValidations;
    }

    public void addTicketValidation(TicketValidation ticketValidation) {
        this.ticketValidations.add(ticketValidation);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", owner=" + owner +
                ", status=" + status +
                ", allowedUses=" + allowedUses +
                ", consumable=" + consumable +
                ", price=" + price +
                ", ticketValidations=" + ticketValidations +
                '}';
    }
}
