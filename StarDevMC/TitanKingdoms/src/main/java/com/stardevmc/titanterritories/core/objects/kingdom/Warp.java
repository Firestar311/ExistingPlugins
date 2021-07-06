package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Warp implements IElement, ConfigurationSerializable {
    private String name;
    private List<String> allowedRanks = new ArrayList<>();
    private Location location;
    private List<Visit> visitHistory = new ArrayList<>();
    private UUID creator;
    
    public Warp(String name, Location location, UUID creator) {
        this.name = name;
        this.location = location;
        this.creator = creator;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", this.name);
        serialized.put("location", this.location);
        serialized.put("ranks", allowedRanks);
        serialized.put("visitAmount", visitHistory.size() + "");
        for (int i = 0; i < visitHistory.size(); i++) {
            serialized.put("visit" + i, visitHistory.get(i));
        }
        serialized.put("creator", this.creator.toString());
        return serialized;
    }
    
    public static Warp deserialize(Map<String, Object> serialized) {
        String name = (String) serialized.get("name");
        Location location = (Location) serialized.get("location");
        UUID creator = UUID.fromString((String) serialized.get("creator"));
        List<String> rankString = (List<String>) serialized.get("ranks");
        List<Visit> visits = new ArrayList<>();
        int visitAmount = Integer.parseInt((String) serialized.get("visitAmount"));
        for (int i = 0; i < visitAmount; i++) {
            visits.add((Visit) serialized.get("visit" + i));
        }
        Warp warp = new Warp(name, location, creator);
        warp.allowedRanks = rankString;
        warp.visitHistory = visits;
        return warp;
    }
    
    public void addVisit(Visit visit) {
        this.visitHistory.add(visit);
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getAllowedRanks() {
        return allowedRanks;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public List<Visit> getVisitHistory() {
        return visitHistory;
    }
    
    public void addAllowedRank(Rank rank) {
        this.allowedRanks.add(rank.getName());
    }
    
    public void removeAllowedRank(Rank rank) {
        this.allowedRanks.remove(rank.getName());
    }
    
    public boolean isAllowedRank(Rank rank) {
        return this.allowedRanks.contains(rank.getName());
    }
    
    public UUID getCreator() {
        return creator;
    }
    
    public String formatLine(String... args) {
        DecimalFormat format = ((DecimalFormat) NumberFormat.getInstance());
        format.applyPattern("#######0.00");
        String locationString = "(" + format.format(location.getX()) + ", " + format.format(location.getY()) + ", " + format.format(location.getZ()) + ")";
        return "&b" + name + " -> " + locationString;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
}