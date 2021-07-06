package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class MessageToggle extends Toggle {

    public MessageToggle(int slot) {
        super("Messages", "Toggles messaging", Material.SIGN, slot, true, true);
    }
    
    public void onToggle(boolean value, Object... args) {
    
    }
}
