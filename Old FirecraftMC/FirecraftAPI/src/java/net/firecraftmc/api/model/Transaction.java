package net.firecraftmc.api.model;

import net.firecraftmc.api.enums.TransactionType;

import java.util.Objects;
import java.util.UUID;

public class Transaction implements Comparable<Transaction> {
    private final UUID player;
    private final TransactionType type;
    private final double amount;
    private final long date;
    private UUID admin;
    private UUID target;
    private String ecoTicketId = "";

    public Transaction(UUID player, TransactionType type, double amount, long date) {
        this.player = player;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public UUID getPlayer() {
        return player;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public long getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                date == that.date &&
                Objects.equals(player, that.player) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, type, amount, date);
    }

    public int compareTo(Transaction o) {
        if (o.date > date) return 1;
        if (o.date == date) return 0;
        if (o.date < date) return -1;
        return 0;
    }

    public UUID getAdmin() {
        return admin;
    }

    public void setAdmin(UUID admin) {
        this.admin = admin;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public String getEcoTicketId() {
        return ecoTicketId;
    }

    public void setEcoTicketId(String ecoTicketId) {
        this.ecoTicketId = ecoTicketId;
    }
}