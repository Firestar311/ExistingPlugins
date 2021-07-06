package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class IncognitoToggle extends Toggle {
    
    public IncognitoToggle(int slot) {
        super("Incognito", "Toggles Incognito mode", Material.RED_BED, slot, false, false);
    }
    
    public void onToggle(boolean value, Object... args) {
    
    }
}
