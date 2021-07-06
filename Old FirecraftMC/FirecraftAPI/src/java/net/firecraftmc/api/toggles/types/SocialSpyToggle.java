package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class SocialSpyToggle extends Toggle {
    
    public SocialSpyToggle(int slot) {
        super("Social Spy", "Toggles the ability to spy on chat", Material.BEACON, slot, false, false);
    }
    
    public void onToggle(boolean value, Object... args) {}
}
