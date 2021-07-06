package net.firecraftmc.api.punishments;

import net.firecraftmc.api.paginator.Paginatable;

import java.util.Map.Entry;
import java.util.TreeMap;

public class Rule implements Paginatable {
    private final int id;
    private final String name;
    private final String description;
    private final TreeMap<Integer, RulePunishment> punishments = new TreeMap<>();
    
    public Rule(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void addPunishment(Punishment.Type type, long length) {
        int number = punishments.size() + 1;
        this.punishments.put(number, new RulePunishment(number, type, length));
    }
    
    public RulePunishment getPunishment(int n) {
        return punishments.get(n);
    }
    
    public int getMaxOffenses() {
        return punishments.size();
    }
    
    public String formatLine() {
        String line = name + " (ID: " + id + "): §7" + description + "\n";
        StringBuilder sb = new StringBuilder();
        for (Entry<Integer, RulePunishment> entry : punishments.entrySet()) {
            sb.append("\n§8§l - ").append("§6Offense #").append(entry.getKey()).append(" ").append(entry.getValue());
        }
        
        return line + sb.toString();
    }
}