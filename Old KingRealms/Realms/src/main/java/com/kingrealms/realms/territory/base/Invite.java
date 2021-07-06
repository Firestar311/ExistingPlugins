package com.kingrealms.realms.territory.base;

import com.kingrealms.realms.Realms;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.Constants;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Invite")
public class Invite implements IElement, ConfigurationSerializable {
    protected UUID target, actor; //mysql
    protected long date; //mysql
    
    public Invite(UUID target, UUID actor, long date) {
        this.target = target;
        this.actor = actor;
        this.date = date;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("target", this.target.toString());
        serialized.put("actor", this.actor.toString());
        serialized.put("date", this.date + "");
        return serialized;
    }
    
    public static Invite deserialize(Map<String, Object> serialized) {
        UUID target = UUID.fromString((String) serialized.get("target"));
        UUID actor = UUID.fromString((String) serialized.get("actor"));
        long date = Long.parseLong((String) serialized.get("date"));
        return new Invite(target, actor, date);
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public UUID getActor() {
        return actor;
    }
    
    public long getDate() {
        return date;
    }
    
    @Override
    public String formatLine(String... args) {
        String actor = Realms.getInstance().getUserManager().getUser(this.actor).getLastName();
        String target = Realms.getInstance().getUserManager().getUser(this.target).getLastName();
        String date = Constants.DATE_FORMAT.format(new Date(this.date));
        return " &8- &b" + target + " &ewas invited by &d" + actor + " on &a" + date;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Invite invite = (Invite) o;
        return Objects.equals(target, invite.target);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(target);
    }
}