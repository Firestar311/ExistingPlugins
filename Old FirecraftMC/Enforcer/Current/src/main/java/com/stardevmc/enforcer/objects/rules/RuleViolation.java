package com.stardevmc.enforcer.objects.rules;

import com.starmediadev.lib.pagination.IElement;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class RuleViolation implements IElement, ConfigurationSerializable {
    
    private SortedMap<Integer, RulePunishment> punishments = new TreeMap<>();
    
    private int violationNumber;
    private long length;
    
    private Rule parent;
    
    public RuleViolation(Rule parent, int violationNumber) {
        this.violationNumber = violationNumber;
        this.parent = parent;
    }
    
    public RuleViolation() {}
    
    public void addPunishment(RulePunishment punishment) {
        if (punishment.getId() == -1) {
            int id = this.punishments.size();
            punishment.setId(id);
        }
        this.punishments.put(punishment.getId(), punishment);
    }
    
    public void addPunishment(int id, RulePunishment punishment) {
        if (punishment.getId() == -1) {
            punishment.setId(id);
        }
        this.punishments.put(id, punishment);
    }
    
    public void setLength(long length) {
        this.length = length;
    }
    
    public SortedMap<Integer, RulePunishment> getPunishments() {
        return new TreeMap<>(punishments);
    }
    
    public int getViolationNumber() {
        return violationNumber;
    }
    
    public void setViolationNumber(int violationNumber) {
        this.violationNumber = violationNumber;
    }
    
    public void removePunishment(int punishment) {
        this.punishments.remove(punishment);
    }
    
    public String formatLine(String... args) {
        return "&dViolation: &e" + violationNumber + " &7- &dAction(s): &e" + this.punishments.size();
    }
    
    public void clearPunishments() {
        this.punishments.clear();
    }
    
    public boolean hasPunishment(int id) {
        return this.punishments.containsKey(id);
    }
    
    public String getPermission() {
        return parent.getPermission() + ".violations." + this.violationNumber;
    }
    
    public boolean hasPermission(Player player) {
        return player.hasPermission("enforcer.rules.*") || player.hasPermission(parent.getPermission() + ".violations.*") || player.hasPermission(getPermission());
    }
    
    public long getLength() {
        return length;
    }
    
    public void setParent(Rule rule) {
        this.parent = rule;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("violationNumber", this.violationNumber + "");
        serialized.put("length", this.length + "");
        for (Entry<Integer, RulePunishment> entry : this.punishments.entrySet()) {
            serialized.put("punishment" + entry.getKey(), entry.getValue());
        }
        return serialized;
    }
    
    public static RuleViolation deserialize(Map<String, Object> serialized) {
        int offenseNumber = Integer.parseInt((String) serialized.get("violationNumber"));
        int length = Integer.parseInt((String) serialized.get("length"));
        SortedMap<Integer, RulePunishment> punishments = new TreeMap<>();
        serialized.forEach((s, o) -> {
            if (s.startsWith("punishment")) {
                RulePunishment rulePunishment = (RulePunishment) o;
                punishments.put(rulePunishment.getId(), rulePunishment);
            }
        });
        
        RuleViolation ruleViolation = new RuleViolation();
        ruleViolation.violationNumber = offenseNumber;
        ruleViolation.length = length;
        ruleViolation.punishments = punishments;
        return ruleViolation;
    }
}