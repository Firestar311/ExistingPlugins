package com.stardevmc.enforcer.objects.rules;

import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class Rule implements IElement, Comparable<Rule>, ConfigurationSerializable {
    
    private int id;
    private String internalId, name, description, playerDescription;
    private SortedMap<Integer, RuleViolation> violations = new TreeMap<>();
    private Material material;
    
    private ItemStack itemStack;
    
    public Rule(int id, String internalId, String name, String description) {
        this.id = id;
        this.internalId = internalId;
        this.name = name;
        this.description = description;
    }
    
    public Rule(String internalId, String name) {
        this(-1, internalId, name, "");
    }
    
    public int getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void addViolation(RuleViolation action) {
        action.setViolationNumber(this.violations.size() + 1);
        this.violations.put(action.getViolationNumber(), action);
    }
    
    public void addViolation(int offenseNumber, RuleViolation offense) {
        this.violations.put(offenseNumber, offense);
    }
    
    public RuleViolation getViolation(int offenseCount) {
        if (offenseCount > violations.size()) {
            return this.violations.get(this.violations.size()-1);
        }
        return this.violations.get(offenseCount);
    }
    
    public String getName() {
        return name;
    }
    
    public SortedMap<Integer, RuleViolation> getViolations() {
        return new TreeMap<>(violations);
    }
    
    public String getInternalId() {
        return internalId;
    }
    
    public String formatLine(String... args) {
        return " &8- &2" + name + "&8: &d" + description;
    }
    
    public String getPlayerDescription() {
        return playerDescription;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void removeOffense(int offenseNumber) {
        this.violations.remove(offenseNumber);
    }
    
    public void setMaterial(Material material) {
        this.material = material;
        this.itemStack = getItemStack();
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public ItemStack getItemStack() {
        List<String> descLore = Utils.wrapLore(45, description);
        
        if (this.itemStack == null) {
            if (material != null) this.itemStack = ItemBuilder.start(material).withName("&a&l" + getName()).withLore(descLore).buildItem();
        }
        return itemStack;
    }
    
    public int compareTo(Rule o) {
        return Integer.compare(this.id, o.id);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setInternalId(String internalId) {
        this.internalId = internalId.toLowerCase().replace(" ", "_");
    }
    
    public void clearOffenses() {
        this.violations.clear();
    }
    
    public String getPermission() {
        return "enforcer.rules." + internalId;
    }
    
    public boolean hasPermission(Player player) {
        return player.hasPermission("enforcer.rules.*") || player.hasPermission(getPermission());
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id + "");
        serialized.put("internalId", this.internalId);
        serialized.put("name", this.name);
        serialized.put("description", this.description);
        serialized.put("material", this.material.name());
        serialized.put("playerDescription", this.playerDescription);
        for (Entry<Integer, RuleViolation> entry : violations.entrySet()) {
            serialized.put("violations" + entry.getKey(), entry.getValue());
        }
        return serialized;
    }
    
    public static Rule deserialize(Map<String, Object> serialized) {
        int id = Integer.parseInt((String) serialized.get("id"));
        String internalId = (String) serialized.get("internalId");
        String name = (String) serialized.get("name");
        String description = (String) serialized.get("description");
        Material material = Material.valueOf((String) serialized.get("material"));
        String playerDescription = (String) serialized.get("playerDescription");
        Rule rule = new Rule(id, internalId, name, description);
        SortedMap<Integer, RuleViolation> offenses = new TreeMap<>();
        serialized.forEach((s, o) -> {
            if (s.startsWith("violations")) {
                RuleViolation ruleViolation = (RuleViolation) o;
                ruleViolation.setParent(rule);
                offenses.put(ruleViolation.getViolationNumber(), ruleViolation);
            }
        });
        
        rule.material = material;
        rule.violations = offenses;
        rule.playerDescription = playerDescription;
        return rule;
    }
    
    public void setPlayerDescription(String playerDescription) {
        this.playerDescription = playerDescription;
    }
}