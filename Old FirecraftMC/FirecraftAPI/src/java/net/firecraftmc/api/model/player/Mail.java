package net.firecraftmc.api.model.player;

import net.firecraftmc.api.FirecraftAPI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Mail {
    private int id;
    private final long date;
    private final UUID sender;
    private final UUID receiver;
    private final String text;
    private final boolean read;
    
    public Mail(int id, long date, UUID sender, UUID receiver, String text, boolean read) {
        this.id = id;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.read = read;
    }
    
    public Mail(long date, UUID sender, UUID receiver, String text, boolean read) {
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
        this.read = read;
    }
    
    public int getId() {
        return id;
    }
    
    public UUID getSender() {
        return sender;
    }
    
    public UUID getReceiver() {
        return receiver;
    }
    
    public String getText() {
        return text;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public long getDate() {
        return date;
    }
    
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return id + " " + new SimpleDateFormat("MM/dd/yyyy h:mm a, z").format(calendar.getTime()) + " " +  FirecraftAPI.getDatabase().getPlayerName(sender);
    }
}