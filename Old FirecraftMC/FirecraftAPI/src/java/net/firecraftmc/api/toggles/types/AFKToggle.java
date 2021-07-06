package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.command.FirecraftCommand;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class AFKToggle extends Toggle {
    public AFKToggle(int slot) {
        super("AFK", "Mark yourself as AFK", Material.QUARTZ, slot, false, true);
    }
    
    public void onToggle(boolean value, Object... args) {
        if (FirecraftAPI.isCore()) {
            if (args.length > 0) {
                if (args[0] instanceof FirecraftPlayer) {
                    FirecraftCommand afk = FirecraftAPI.getFirecraftCore().getCommandManager().getCommand("afk");
                    afk.executePlayer((FirecraftPlayer) args[0], new String[0]);
                }
            }
        }
    }
}
