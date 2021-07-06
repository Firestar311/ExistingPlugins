package net.firecraftmc.api.menus;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.util.ItemStackBuilder;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ToggleMenu {
    
    protected static final ItemStackBuilder ENABLED_ITEM = new ItemStackBuilder(Material.LIME_DYE).withLore(ChatColor.GRAY + "Click to disable");
    protected static final ItemStackBuilder DISABLED_ITEM = new ItemStackBuilder(Material.GRAY_DYE).withLore(ChatColor.GRAY + "Click to enable");
    protected static final int INV_SIZE = 54;
    
    
    protected Inventory inventory;
    protected final FirecraftPlayer player;
    
    protected ToggleMenu(FirecraftPlayer player) {
        this.player = player;
    }
    
    public static ToggleMenu.Entry getItemForValue(String toggleName, int slot, boolean value) {
        if (value) {
            return new ToggleMenu.Entry(slot + 9, ENABLED_ITEM.withName(ChatColor.GREEN + toggleName).buildItem());
        } else {
            return new ToggleMenu.Entry(slot + 9, DISABLED_ITEM.withName(ChatColor.RED + toggleName).buildItem());
        }
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public FirecraftPlayer getPlayer() {
        return player;
    }
    
    public void openPlayer() {
        this.player.getPlayer().openInventory(inventory);
    }
    
    public static class Entry {
        private int slot;
        private ItemStack itemStack;
        
        public Entry(int slot, ItemStack itemStack) {
            this.slot = slot;
            this.itemStack = itemStack;
        }
        
        public int getSlot() {
            return slot;
        }
        
        public ItemStack getItemStack() {
            return itemStack;
        }
    }
}

