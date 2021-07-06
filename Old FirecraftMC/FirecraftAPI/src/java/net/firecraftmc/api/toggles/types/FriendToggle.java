package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class FriendToggle extends Toggle {
    public FriendToggle(int slot) {
        super("Friend Requests", "Toggles the receiving of friend requests", Material.BLAZE_ROD, slot, true, false);
    }
    
    public void onToggle(boolean value, Object... args) {
    
    }
}
