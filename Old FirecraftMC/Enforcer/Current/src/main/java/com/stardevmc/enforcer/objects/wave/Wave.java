package com.stardevmc.enforcer.objects.wave;

import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.wave.WaveEntry.Status;
import com.starmediadev.lib.pagination.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;
import java.util.Map.Entry;

public class Wave implements ConfigurationSerializable, IElement {
    
    protected Actor creator, activator, assignee;
    protected String id;
    protected Paginator<WaveEntry> paginator;
    protected SortedMap<Integer, WaveEntry> punishments = new TreeMap<>();
    protected State state = State.CREATED;
    protected Type type;
    
    public Wave(String id, Actor creator, Type type) {
        this.id = id;
        this.creator = creator;
        this.type = type;
    }
    
    @Override
    public String formatLine(String... args) {
        return "&a" + this.state.name() + " " + this.type.name() + " " + this.creator.getName() + ": " + this.punishments.size();
    }
    
    public static Wave deserialize(Map<String, Object> serialized) {
        String id = (String) serialized.get("id");
        Actor creator = (Actor) serialized.get("creator");
        Type type = Type.valueOf((String) serialized.get("type"));
        State state = State.valueOf((String) serialized.get("state"));
        Actor assignee = null, activator = null;
        if (serialized.containsKey("assignee")) {
            assignee = (Actor) serialized.get("assignee");
        }
        
        if (serialized.containsKey("activator")) {
            activator = (Actor) serialized.get("activator");
        }
        
        SortedMap<Integer, WaveEntry> punishments = new TreeMap<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().startsWith("entry")) {
                WaveEntry waveEntry = (WaveEntry) entry.getValue();
                punishments.put(waveEntry.getIndex(), waveEntry);
            }
        }
        
        Wave wave = new Wave(id, creator, type);
        wave.punishments = punishments;
        wave.state = state;
        wave.assignee = assignee;
        wave.activator = activator;
        return wave;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id);
        serialized.put("creator", creator);
        serialized.put("type", this.type.name());
        serialized.put("state", this.state.name());
        if (activator != null) { serialized.put("activator", this.activator); }
        if (assignee != null) { serialized.put("assignee", this.assignee); }
        for (Entry<Integer, WaveEntry> entry : punishments.entrySet()) {
            serialized.put("entry" + entry.getKey(), entry.getValue());
        }
        return serialized;
    }
    
    public void addPunishment(Punishment punishment) {
        int nextIndex;
        if (this.punishments.isEmpty()) {
            nextIndex = 0;
        } else {
            nextIndex = punishments.lastKey();
        }
        
        this.punishments.put(nextIndex, new WaveEntry(nextIndex, id, punishment));
        
        if (this.paginator != null) {
            generatePaginator();
        }
    }
    
    protected void generatePaginator() {
        this.paginator = PaginatorFactory.generatePaginator(7, punishments.values(), new HashMap<>() {{
            put(DefaultVariables.COMMAND, "wave " + id + " list ");
            put(DefaultVariables.TYPE, "Wave " + id + " Punishments");
        }});
    }
    
    public ActivationStats activate(Actor activator) {
        this.state = State.ACTIVATED;
        this.activator = activator;
        
        int approved = 0, rejected = 0, undecided = 0;
        
        for (WaveEntry value : this.punishments.values()) {
            if (value.getStatus().equals(Status.APPROVED)) {
                value.getPunishment().setDate(System.currentTimeMillis());
                value.getPunishment().executePunishment(ExecuteOptions.NO_MESSAGE);
                approved++;
            } else if (value.getStatus().equals(Status.REJECTED)) {
                rejected++;
            } else if (value.getStatus().equals(Status.UNDECIDED)) {
                undecided++;
            }
        }
        
        return new ActivationStats(approved, rejected, undecided);
    }
    
    public enum Type {
        MANUAL, AUTOMATIC
    }
    
    public enum State {
        CREATED, ACTIVATED, ASSIGNED, UNDER_REVIEW
    }
    
    public Paginator<WaveEntry> getPaginator() {
        if (paginator == null) {
            generatePaginator();
        }
        return paginator;
    }
    
    public WaveEntry getWaveEntry(int index) {
        return this.punishments.get(index);
    }
    
    public void setAssignee(Actor assignee) {
        this.assignee = assignee;
        if (assignee != null) {
            this.state = State.ASSIGNED;
        } else {
            this.state = State.CREATED;
        }
    }
    
    public Actor getCreator() {
        return creator;
    }
    
    public Actor getActivator() {
        return activator;
    }
    
    public Actor getAssignee() {
        return assignee;
    }
    
    public String getId() {
        return id;
    }
    
    public State getState() {
        return state;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setState(State state) {
        this.state = state;
    }
}