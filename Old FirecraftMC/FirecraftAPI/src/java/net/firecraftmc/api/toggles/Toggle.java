package net.firecraftmc.api.toggles;

import net.firecraftmc.api.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Toggle {
    
    public static Toggle VANISH, MAIL, TELEPORT_REQUESTS, FRIEND_REQUESTS,
            RECORDING, SOCIAL_SPY, FLIGHT, GOD_MODE, MESSAGES, INCOGNITO, AFK;
    
    protected final String name, description;
    protected final Material material;
    protected final int slot;
    protected final boolean defaultValue, enabled;
    
    protected ItemStack itemStack;
    
    protected List<String> desc = new ArrayList<>();
    
    public static final Set<Toggle> TOGGLES = new HashSet<>();
    
    public Toggle(String name, String description, Material material, int slot, boolean defaultValue, boolean enabled) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.slot = slot;
        this.defaultValue = defaultValue;
        this.enabled = enabled;
    
        this.itemStack = new ItemStackBuilder(material).withName(ChatColor.GOLD + name).withLore(ChatColor.GRAY + description).buildItem();
    }
    
    public static Toggle getToggle(String name) {
        for (Toggle toggle : TOGGLES) {
            if (toggle.getName().equalsIgnoreCase(ChatColor.stripColor(name))) {
                return toggle;
            }
        }
        return null;
    }
    
    public final Material getMaterial() {
        return material;
    }
    
    public final int getSlot() {
        return slot;
    }
    
    public final boolean getDefaultValue() {
        return defaultValue;
    }
    
    public final boolean isEnabled() {
        return enabled;
    }
    
    public final String getName() {
        return name;
    }
    
    public final String getDescription() {
        return description;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public abstract void onToggle(boolean value, Object... args);
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Toggle toggle = (Toggle) o;
        return Objects.equals(name, toggle.name);
    }
    
    public int hashCode() {
        return Objects.hash(name);
    }
}