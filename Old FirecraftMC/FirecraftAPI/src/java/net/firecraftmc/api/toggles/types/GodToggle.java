package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.command.FirecraftCommand;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class GodToggle extends Toggle {
    
    public GodToggle(int slot) {
        super("God Mode", "Toggles the ability to not take any damage", Material.GOLDEN_APPLE, slot, false, true);
    }
    
    public void onToggle(boolean value, Object... args) {
        if (args.length > 0) {
            if (args[0] instanceof FirecraftPlayer) {
                if (FirecraftAPI.isCore()) {
                    FirecraftCommand god = FirecraftAPI.getFirecraftCore().getCommandManager().getCommand("god");
                    god.executePlayer((FirecraftPlayer) args[0], new String[0]);
                }
            }
        }
    }
}
