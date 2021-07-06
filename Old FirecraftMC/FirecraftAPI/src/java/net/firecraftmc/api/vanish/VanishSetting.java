package net.firecraftmc.api.vanish;

import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class VanishSetting {
    
    protected final String name, description;
    protected final Material material;
    protected final int slot;
    protected final boolean defaultValue;
    protected final Rank minimumRank;
    protected final ItemStack itemStack;
    
    public static VanishSetting PICKUP, DROP, INTERACT, DAMAGE, CHAT, COLLISION, ENTITY_TARGET, DESTROY_VEHICLE, BREAK, PLACE, SILENT;
    //TODO Collision has to be set when it is toggled
    
    public static final Set<VanishSetting> TOGGLES = new HashSet<>();
    
    public VanishSetting(String name, String description, Material material, int slot, boolean defaultValue, Rank rank) {
        this.name = name;
        this.description = description;
        this.material = material;
        this.slot = slot;
        this.defaultValue = defaultValue;
        this.minimumRank = rank;
        
        this.itemStack = new ItemStackBuilder(material).withName(ChatColor.GOLD + name).withLore(ChatColor.GRAY + description, ChatColor.GREEN + "Minimum Rank: " + rank.getPrefix()).buildItem();
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
    
    public final String getName() {
        return name;
    }
    
    public final String getDescription() {
        return description;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public Rank getMinimumRank() {
        return minimumRank;
    }
    
    public static VanishSetting getToggle(String name) {
        for (VanishSetting toggle : TOGGLES) {
            if (toggle.getName().equalsIgnoreCase(ChatColor.stripColor(name))) {
                return toggle;
            }
        }
        return null;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VanishSetting toggle = (VanishSetting) o;
        return slot == toggle.slot && defaultValue == toggle.defaultValue && Objects.equals(name, toggle.name) && material == toggle.material;
    }
    
    public int hashCode() {
        return Objects.hash(name, material, slot, defaultValue);
    }
}