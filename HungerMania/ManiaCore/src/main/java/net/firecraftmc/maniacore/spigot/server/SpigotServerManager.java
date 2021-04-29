package net.firecraftmc.maniacore.spigot.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.server.CenturionsServer;
import net.firecraftmc.maniacore.api.server.ServerManager;
import net.firecraftmc.maniacore.api.server.ServerType;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotServerManager extends ServerManager {
    public SpigotServerManager(CenturionsCore centurionsCore) {
        super(centurionsCore);
    }

    @Override
    public void init() {
        ServerObject server = TimoCloudAPI.getBukkitAPI().getThisServer();
        this.currentServer = new CenturionsServer(server.getName(), server.getPort());
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
                p.sendMessage(CenturionsUtils.color(message));
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
                User user = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
                if (user.getToggle(Toggles.STAFF_NOTIFICATIONS).getAsBoolean()) {
                    player.sendMessage(CenturionsUtils.color(format.toString()));
                }
            }
        }
    }
}
