package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Relation;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Relationship implements ConfigurationSerializable, IElement {
    private Kingdom kingdom1, kingdom2;
    private Relation relation;
    
    public Relationship(Kingdom kingdom1, Kingdom kingdom2, Relation relation) {
        this.kingdom1 = kingdom1;
        this.kingdom2 = kingdom2;
        this.relation = relation;
    }
    
    public Relationship(Map<String, Object> serialized) {
        this.kingdom1 = TitanTerritories.getInstance().getKingdomManager().getKingdom(UUID.fromString((String) serialized.get("kingdom1")));
        this.kingdom2 = TitanTerritories.getInstance().getKingdomManager().getKingdom(UUID.fromString((String) serialized.get("kingdom2")));
        this.relation = Relation.valueOf((String) serialized.get("relation"));
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("kingdom1", kingdom1.getUniqueId().toString());
        serialized.put("kingdom2", kingdom2.getUniqueId().toString());
        serialized.put("relation", relation.name());
        return serialized;
    }
    
    public String formatLine(String... args) {
        String kingdomName = args[0];
        String otherKingdom;
        if (this.kingdom1.getName().equalsIgnoreCase(kingdomName)) {
            otherKingdom = kingdom2.getName();
        } else {
            otherKingdom = kingdom1.getName();
        }
        return " &8- &e" + otherKingdom + " -> " + this.relation.getColor() + this.relation.name();
    }
    
    public int hashCode() {
        return Objects.hash(kingdom1, kingdom2, relation);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Relationship that = (Relationship) o;
        return kingdom1.getUniqueId().equals(that.kingdom1.getUniqueId()) && kingdom2.getUniqueId().equals(that.kingdom2.getUniqueId()) && relation == that.relation;
    }
    
    public Kingdom getKingdom1() {
        return kingdom1;
    }
    
    public Kingdom getKingdom2() {
        return kingdom2;
    }
    
    public Relation getRelation() {
        return relation;
    }
    
    public void setRelation(Relation relation) {
        this.relation = relation;
    }
}