package net.firecraftmc.api.menus;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Bukkit;

import java.util.Map;

public class PlayerToggleMenu extends ToggleMenu {
    
    private static final String NAME = "Toggles";
    
    public PlayerToggleMenu(FirecraftPlayer player) {
        super(player);
        this.inventory = Bukkit.createInventory(null, INV_SIZE, NAME);
        
        for (Map.Entry<Toggle, Boolean> entry : player.getToggles().entrySet()) {
            Toggle toggle = entry.getKey();
            inventory.setItem(toggle.getSlot(), toggle.getItemStack());
            Entry slotItem = getItemForValue(toggle, entry.getValue());
            inventory.setItem(slotItem.getSlot(), slotItem.getItemStack());
        }
    }
    
    public static ToggleMenu.Entry getItemForValue(Toggle toggle, boolean value) {
        return ToggleMenu.getItemForValue(toggle.getName(), toggle.getSlot(), value);
    }
    
    public static String getName() {
        return NAME;
    }
}