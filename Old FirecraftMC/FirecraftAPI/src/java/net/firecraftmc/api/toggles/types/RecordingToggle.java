package net.firecraftmc.api.toggles.types;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.command.FirecraftCommand;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.toggles.Toggle;
import org.bukkit.Material;

public class RecordingToggle extends Toggle {
    
    public RecordingToggle(int slot) {
        super("Recording", "Toggles the recording mode status.", Material.BRICK, slot, false, true);
    }
    
    public void onToggle(boolean value, Object... args) {
        if (args.length > 0) {
            if (args[0] instanceof FirecraftPlayer) {
                if (FirecraftAPI.isCore()) {
                    FirecraftCommand record = FirecraftAPI.getFirecraftCore().getCommandManager().getCommand("record");
                    record.executePlayer((FirecraftPlayer) args[0], new String[0]);
                }
            }
        }
    }
}
