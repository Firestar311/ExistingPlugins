package com.kingrealms.realms.items;

import com.kingrealms.realms.items.category.ItemCategory;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {
    protected ID id;
    protected String displayName, description;
    protected Material material;
    protected List<String> lore = new ArrayList<>();
    protected ItemType type;
    protected boolean glowing;
    protected double sellMultiplier = 1;
    
    public CustomItem(Material material, ItemType type) {
        this.material = material;
        this.id = new ID(material.name().toLowerCase());
        this.displayName = MaterialNames.getName(material);
        this.type = type;
        this.description = "";
        CustomItemRegistry.REGISTRY.put(this.id, this);
    }
    
    protected CustomItem() {}
    
    public CustomItem(ID id, String displayName, String description, Material material, ItemType type, boolean glowing) {
        this(id, displayName, description, material, type, glowing, true);
    }
    
    public double getSellMultiplier() {
        return sellMultiplier;
    }
    
    public CustomItem setSellMultiplier(double sellMultiplier) {
        this.sellMultiplier = sellMultiplier;
        return this;
    }
    
    public CustomItem(ID id, String displayName, String description, Material material, ItemType type, boolean glowing, boolean register) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.material = material;
        this.type = type;
        this.glowing = glowing;
        if (register) {
            CustomItemRegistry.REGISTRY.put(this.id, this);
        }
    }
    
    public CustomItem setCategory(ItemCategory category) {
        category.addItem(material, this);
        return this;
    }
    
    public boolean matches(ItemStack itemStack) {
        try {
            String id = NBTWrapper.getNBTString(itemStack, "itemid");
            return this.id.toString().equalsIgnoreCase(id);
        } catch (Exception e) {}
        return false;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }
    
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
    
    public ItemStack getItemStack(int amount) {
        List<String> newLore = new ArrayList<>(List.of("&7&o" + Utils.capitalizeEveryWord(type.name())));
        if (!lore.isEmpty()) {
            newLore.add("");
            newLore.addAll(this.lore);
        }
        return ItemBuilder.start(material).withName("&e" + displayName).withLore(newLore).setGlowing(glowing).withAmount(amount).addNBTString("itemid", this.id.toString()).buildItem();
    }
    
    public void setId(ID id) {
        this.id = id;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ID getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    public CustomItem addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }
    
    public void setLore(List<String> lore) {
        this.lore = lore;
    }
    
    public List<String> getLore() {
        return lore;
    }
    
    public void giveItem(Player player) {
        player.getInventory().addItem(getItemStack());
    }
    
    public Material getMaterial() {
        return material;
    }
}