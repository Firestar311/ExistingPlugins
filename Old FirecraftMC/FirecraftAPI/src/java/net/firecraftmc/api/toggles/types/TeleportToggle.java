package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class TeleportToggle extends Toggle {
    
    public TeleportToggle(int slot) {
        super("Teleport Requests", "Toggles teleport requests", Material.ENDER_PEARL, slot, true, true);
    }
    
    public void onToggle(boolean value, Object... args) {
    
    }
}
