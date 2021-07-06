package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.command.FirecraftCommand;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class VanishToggle extends Toggle {
    
    public VanishToggle(int slot) {
        super("Vanish", "Toggles vanish mode", Material.GLASS, slot, false, true);
    }
    
    public void onToggle(boolean value, Object... args) {
        if (args.length > 0) {
            if (args[0] instanceof FirecraftPlayer) {
                if (FirecraftAPI.isCore()) {
                    FirecraftCommand vanish = FirecraftAPI.getFirecraftCore().getCommandManager().getCommand("vanish");
                    vanish.executePlayer((FirecraftPlayer) args[0], new String[0]);
                }
            }
        }
    }
    
}
