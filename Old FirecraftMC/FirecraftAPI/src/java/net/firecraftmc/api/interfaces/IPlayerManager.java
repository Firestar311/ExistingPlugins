package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.UUID;

/**
 * The interface for the PlayerManager class for FirecraftPlugin
 * Implemented in FirecraftCore
 */
public interface IPlayerManager extends Listener {
    FirecraftPlayer getPlayer(UUID uuid);
    FirecraftPlayer getPlayer(String name);
    Collection<FirecraftPlayer> getPlayers();
    void addPlayer(FirecraftPlayer player);
    void removePlayer(UUID uuid);
    void addCachedPlayer(FirecraftPlayer player);
    FirecraftPlayer getCachedPlayer(UUID uuid);
}