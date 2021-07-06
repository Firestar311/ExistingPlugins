package net.firecraftmc.api.menus;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.vanish.VanishSetting;
import org.bukkit.Bukkit;

import java.util.Map;

public class VanishToggleMenu extends ToggleMenu {
    
    private static final String NAME = "Vanish Settings";
    
    public VanishToggleMenu(FirecraftPlayer player) {
        super(player);
        this.inventory = Bukkit.createInventory(null, INV_SIZE, NAME);
        
        if (player.isVanished()) {
            for (Map.Entry<VanishSetting, Boolean> entry : player.getVanishSettings().getSettings().entrySet()) {
                VanishSetting toggle = entry.getKey();
                inventory.setItem(toggle.getSlot(), toggle.getItemStack());
                Entry slotItem = getItemForValue(toggle, entry.getValue());
                inventory.setItem(slotItem.getSlot(), slotItem.getItemStack());
            }
        }
    }
    
    public static VanishToggleMenu.Entry getItemForValue(VanishSetting toggle, boolean value) {
        return ToggleMenu.getItemForValue(toggle.getName(), toggle.getSlot(), value);
    }
    
    public static String getName() {
        return NAME;
    }
}