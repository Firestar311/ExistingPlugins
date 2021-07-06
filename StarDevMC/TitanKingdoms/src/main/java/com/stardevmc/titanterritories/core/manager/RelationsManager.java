package com.stardevmc.titanterritories.core.manager;

import com.firestar311.lib.config.ConfigManager;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Relation;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.kingdom.Relationship;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class RelationsManager {
    
    private ConfigManager configManager;
    
    private List<Relationship> relationships = new ArrayList<>();
    
    public RelationsManager() {
        this.configManager = new ConfigManager(TitanTerritories.getInstance(), "relations");
        this.configManager.setup();
    }
    
    public void addRelationship(Relationship relationship) {
        this.relationships.add(relationship);
    }
    
    public void removeRelationship(Relationship relationship) {
        this.relationships.remove(relationship);
    }
    
    public void setRelationship(Kingdom kingdom1, Kingdom kingdom2, Relation relation) {
        Relationship relationship = getRelationship(kingdom1, kingdom2);
        if (relationship != null) {
            relationship.setRelation(relation);
        } else {
            this.relationships.add(new Relationship(kingdom1, kingdom2, relation));
        }
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        for (int i = 0; i < relationships.size(); i++) {
            config.set("relations." + i, relationships.get(i));
        }
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        ConfigurationSection relationsSection = config.getConfigurationSection("relations");
        if (relationsSection == null) return;
        for (String r : relationsSection.getKeys(false)) {
            Relationship relationship = (Relationship) relationsSection.get(r);
            this.relationships.add(relationship);
        }
    }
    
    public List<Relationship> getRelationships() {
        return new ArrayList<>(relationships);
    }
    
    public List<Relationship> getRelationships(Kingdom kingdom) {
        List<Relationship> relationships = new ArrayList<>();
        for (Relationship relationship : this.relationships) {
            if (relationship.getKingdom1().equals(kingdom) || relationship.getKingdom2().equals(kingdom)) {
                relationships.add(relationship);
            }
        }
        return relationships;
    }
    
    public Relation getRelation(Kingdom first, Kingdom second) {
        Relationship relationship = getRelationship(first, second);
        return (relationship != null) ? relationship.getRelation() : Relation.NEUTRAL;
    }
    
    public Relationship getRelationship(Kingdom first, Kingdom second) {
        for (Relationship relationship : this.relationships) {
            if ((relationship.getKingdom1().equals(first) || relationship.getKingdom2().equals(first)) &&
                    (relationship.getKingdom1().equals(second) || relationship.getKingdom2().equals(second))) {
                return relationship;
            }
        }
        return null;
    }
}