package net.firecraftmc.api.plugin;

import net.firecraftmc.api.model.Database;
import net.firecraftmc.api.model.ProxyWorker;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.model.server.FirecraftServer;

import java.util.Collection;
import java.util.UUID;

public interface IFirecraftProxy extends IFirecraftBase {

    FirecraftServer getServer(String serverId);

    Collection<FirecraftPlayer> getPlayers();

    void removeWorker(ProxyWorker proxyWorker);

    Database getFCDatabase();

    Collection<ProxyWorker> getProxyWorkers();

    FirecraftPlayer getPlayer(UUID uuid);

    FirecraftPlayer getPlayer(String name);

    FirecraftServer getFCServer();
}