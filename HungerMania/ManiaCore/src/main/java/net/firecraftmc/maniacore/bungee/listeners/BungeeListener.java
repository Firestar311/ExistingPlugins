package net.firecraftmc.maniacore.bungee.listeners;

import net.firecraftmc.maniacore.CenturionsCoreProxy;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.bungee.user.BungeeUser;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("DuplicatedCode")
public class BungeeListener implements Listener {
    
    private CenturionsCoreProxy centurionsCoreProxy;
    
    public BungeeListener(CenturionsCoreProxy centurionsCoreProxy) {
        this.centurionsCoreProxy = centurionsCoreProxy;
    }
    
    @EventHandler
    public void onLogin(LoginEvent e) {
        Redis.deleteUserData(e.getConnection().getUniqueId());
        Redis.deleteUserStats(e.getConnection().getUniqueId());
        Redis.deleteToggles(e.getConnection().getUniqueId());
        BungeeUser bungeeUser = (BungeeUser) CenturionsCore.getInstance().getUserManager().getUser(e.getConnection().getUniqueId());
        Redis.pushUser(bungeeUser);
        CenturionsCore.getInstance().getFriendsManager().loadDataFromDatabase(e.getConnection().getUniqueId());
        Redis.sendCommand("userJoin " + bungeeUser.getUniqueId());
    }
    
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        centurionsCoreProxy.getManiaCore().getMessageHandler().sendServerSwitchMessage(e.getPlayer().getUniqueId(), e.getPlayer().getServer().getInfo().getName());
    }
    
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        centurionsCoreProxy.getManiaCore().getMessageHandler().sendNetworkLeaveMessage(e.getPlayer().getUniqueId());
    
        CenturionsCore.getInstance().getPlugin().runTaskLaterAsynchronously(() -> {
            BungeeUser bungeeUser = new BungeeUser(Redis.getUserData(e.getPlayer().getUniqueId()));
            CenturionsCoreProxy.saveUserData(bungeeUser);
        }, 1L);
    }
}
