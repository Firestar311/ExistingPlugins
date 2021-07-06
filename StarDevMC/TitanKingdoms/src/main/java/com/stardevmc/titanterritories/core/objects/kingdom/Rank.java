package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.lists.PermissionList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Rank implements ConfigurationSerializable, IElement {
    private String name;
    private String displayName;
    private String prefix;
    private PermissionList permissionList;
    private int order;
    //private boolean mustConfirm;
    
    public Rank(String name, int order) {
        this.name = ChatColor.stripColor(name).toLowerCase();
        this.displayName = name;
        this.prefix = "";
        this.permissionList = new PermissionList();
        this.order = order;
    }
    
    public Rank(Rank rank) {
        this.name = rank.getName();
        this.displayName = rank.getDisplayName();
        this.prefix = rank.getPrefix();
        this.permissionList = new PermissionList(rank.getPermissionList());
        this.order = rank.getOrder();
    }
    
    public Rank(Map<String, Object> serialized) {
        if (serialized.containsKey("name")) {
            this.name = (String) serialized.get("name");
        }
        
        if (serialized.containsKey("displayName")) {
            this.displayName = (String) serialized.get("displayName");
        }
        
        if (serialized.containsKey("prefix")) {
            this.prefix = (String) serialized.get("prefix");
        }
        
        if (serialized.containsKey("order")) {
            this.order = (int) serialized.get("order");
        }
        
        if (serialized.containsKey("permissions")) {
            this.permissionList = (PermissionList) serialized.get("permissions");
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("displayName", displayName);
        serialized.put("prefix", prefix);
        serialized.put("order", order);
        serialized.put("permissions", permissionList);
        return serialized;
    }
    
    public void addPermission(Permission permission) {
        this.permissionList.addPermission(permission);
    }
    
    public void removePermission(Permission permission) {
        this.permissionList.removePermission(permission);
    }
    
    public boolean hasPermission(Permission permission) {
        return this.permissionList.hasPermission(permission);
    }
    
    public String formatLine(String... args) {
        return " &8- &6" + this.order + "&8: &a" + this.name + " &8(" + this.displayName + "&8)";
    }
    
    public int hashCode() {
        return Objects.hash(name, order);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Rank that = (Rank) o;
        return order == that.order && name.equals(that.name);
    }
    
    public String toViewString() {
        return "&aName: &e" + name + "\n" + "&aDisplay Name: &e" + displayName + "\n" + "&aPrefix: &e" + prefix + "\n"
                + "&aPermissions: &e" + permissionList.toString() + "\n" + "&aOrder: &e" + order;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = ChatColor.stripColor(name).toLowerCase();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = Utils.color(displayName);
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = Utils.color(prefix);
    }
    
    public PermissionList getPermissionList() {
        return permissionList;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int value) {
        this.order = value;
    }
}