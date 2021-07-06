package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import org.bukkit.event.Listener;

public interface IStaffmodeManager extends Listener {
    boolean inStaffMode(FirecraftPlayer player);
}