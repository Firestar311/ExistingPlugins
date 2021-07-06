package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.command.FirecraftCommand;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class FlightToggle extends Toggle {
    
    public FlightToggle(int slot) {
        super("Flight", "Toggles the ability to fly or not", Material.FEATHER, slot, false, true);
    }
    
    public void onToggle(boolean value, Object... args) {
        if (args.length > 0) {
            if (args[0] instanceof FirecraftPlayer) {
                if (FirecraftAPI.isCore()) {
                    FirecraftCommand fly = FirecraftAPI.getFirecraftCore().getCommandManager().getCommand("fly");
                    fly.executePlayer((FirecraftPlayer) args[0], new String[0]);
                }
            }
        }
    }
    
}
