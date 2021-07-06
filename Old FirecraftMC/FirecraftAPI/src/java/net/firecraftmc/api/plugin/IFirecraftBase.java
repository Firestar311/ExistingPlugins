package net.firecraftmc.api.plugin;

import net.firecraftmc.api.model.Database;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.model.server.FirecraftServer;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.UUID;

public interface IFirecraftBase extends Plugin {
    
    FirecraftPlayer getPlayer(String name);
    FirecraftPlayer getPlayer(UUID uuid);
    FirecraftServer getFCServer();
    FirecraftServer getFCServer(String id);
    Collection<FirecraftPlayer> getPlayers();
    Database getFCDatabase();
}