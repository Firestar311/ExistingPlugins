package com.stardevmc.titangames.kits;

import com.firestar311.lib.pagination.IElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Kit implements IElement {
    
    private String name;
    private List<ItemStack> items = new ArrayList<>();
    private String permission;
    private Material displayMaterial;
    
    public Kit(String name, String permission, Material displayMaterial) {
        this.name = name;
        this.permission = permission;
        this.displayMaterial = displayMaterial;
    }
    
    public Kit(String name, String permission, List<ItemStack> items, Material displayMaterial) {
        this(name, permission, displayMaterial);
        this.items.addAll(items);
    }
    
    public String getName() {
        return name;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void apply(Player player) {
        for (ItemStack itemStack : this.items) {
            if (itemStack != null) {
                player.getInventory().addItem(itemStack);
            }
        }
    }
    
    public void addItem(ItemStack itemStack) {
        this.items.add(itemStack);
    }
    
    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }
    
    public Material getDisplayMaterial() {
        return displayMaterial;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kit kit = (Kit) o;
        return Objects.equals(name, kit.name);
    }
    
    public int hashCode() {
        return Objects.hash(name);
    }
    
    public String formatLine(String... args) {
        return "&aKit with the name &b" + this.name + " &ahas &b" + this.items.size() + " &aitems.";
    }
}