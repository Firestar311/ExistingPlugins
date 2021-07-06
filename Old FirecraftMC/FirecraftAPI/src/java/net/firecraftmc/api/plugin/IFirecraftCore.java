package net.firecraftmc.api.plugin;

import net.firecraftmc.api.interfaces.*;
import net.firecraftmc.api.model.FirecraftSocket;
import net.firecraftmc.api.model.server.FirecraftServer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;
import java.util.logging.Logger;

public interface IFirecraftCore extends IFirecraftBase {

    NickWrapper getNickWrapper();
    FirecraftSocket getSocket();
    IPlayerManager getPlayerManager();
    Location getSpawn();
    void setSpawn(Location location);
    Location getJailLocation();
    void setJailLocation(Location location);
    boolean isWarnAcknowledged(UUID uuid);
    String getAckCode(UUID uuid);
    void acknowledgeWarn(UUID uuid, String name);
    void addAckCode(UUID uuid, String code);
    IHomeManager getHomeManager();
    IServerManager getServerManager();
    void setServer(FirecraftServer server);
    IStaffmodeManager getStaffmodeManager();
    IWarpManager getWarpManager();
    IEconomyManager getEconomyManager();
    NBTWrapper getNbtWrapper();
    Logger getLogger();
    FileConfiguration getConfig();
    ICommandManager getCommandManager();
    IToggleManager getToggleManager();
}