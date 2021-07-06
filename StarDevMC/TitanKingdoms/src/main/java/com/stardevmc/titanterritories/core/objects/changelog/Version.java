package com.stardevmc.titanterritories.core.objects.changelog;

import java.util.*;

public class Version implements Comparable<Version> {
    
    private String number, description, name;
    private Set<String> changes = new HashSet<>();
    private Set<UUID> acknowledged = new HashSet<>();
    
    public Version(String name, String number, String description) {
        this.name = name;
        this.number = number;
        this.description = description;
    }
    
    public void addChange(String change) {
        this.changes.add(change);
    }
    
    public void addChanges(String... changes) {
        this.changes.addAll(Arrays.asList(changes));
    }
    
    public void addAcknoledged(UUID uuid) {
        this.acknowledged.add(uuid);
    }
    
    public int compareTo(Version o) {
        return number.compareTo(o.number);
    }
    
    public String getNumber() {
        return number;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Set<String> getChanges() {
        return changes;
    }
    
    public Set<UUID> getAcknowledged() {
        return acknowledged;
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Version version = (Version) o;
        return Objects.equals(number, version.number);
    }
    
    public int hashCode() {
        return Objects.hash(number);
    }
    
    public String getName() {
        return name;
    }
}