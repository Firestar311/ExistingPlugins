package net.firecraftmc.maniacore.spigot.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.server.ManiaServer;
import net.firecraftmc.maniacore.api.server.ServerManager;
import net.firecraftmc.maniacore.api.server.ServerType;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.util.ManiaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotServerManager extends ServerManager {
    public SpigotServerManager(ManiaCore maniaCore) {
        super(maniaCore);
    }

    @Override
    public void init() {
        ServerObject server = TimoCloudAPI.getBukkitAPI().getThisServer();
        this.currentServer = new ManiaServer(server.getName(), server.getPort());
        this.currentServer.setNetworkType(networkType);
    }
    
    protected void handleServerStart(String server) {
        Channel c = Channel.STAFF;
        StringBuilder format = new StringBuilder().append(c.getChatPrefix()).append(server).append(" has started.");
        handleServerMessage(c, format);
    }
    
    protected void handleGameReady(String server) {
        if (getCurrentServer().getType() == ServerType.HUB) {
            String message = "&6&l>> &a&lA game is ready at the server " + server + "!";
            //TODO Click text
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(ManiaUtils.color(message));
            }
        }
    }
    
    protected void handleServerStop(String server) {
        Channel c = Channel.STAFF;
        StringBuilder format = new StringBuilder().append(c.getChatPrefix()).append(server).append(" has stopped.");
        handleServerMessage(c, format);
    }
    
    private void handleServerMessage(Channel c, StringBuilder format) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(c.getPermission())) {
                User user = ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (user.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(ManiaUtils.color(format.toString()));
                }
            }
        }
    }
}
