package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class MailToggle extends Toggle {
    
    public MailToggle(int slot) {
        super("Mail", "Toggles the sending and receiving of mail", Material.PAPER, slot, true, true);
    }
    
    public void onToggle(boolean value, Object... args) {
    
    }
}
