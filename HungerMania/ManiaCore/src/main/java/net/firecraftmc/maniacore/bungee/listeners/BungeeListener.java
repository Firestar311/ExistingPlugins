package net.firecraftmc.maniacore.bungee.listeners;

import net.firecraftmc.maniacore.ManiaCoreProxy;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.bungee.user.BungeeUser;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@SuppressWarnings("DuplicatedCode")
public class BungeeListener implements Listener {
    
    private ManiaCoreProxy maniaCoreProxy;
    
    public BungeeListener(ManiaCoreProxy maniaCoreProxy) {
        this.maniaCoreProxy = maniaCoreProxy;
    }
    
    @EventHandler
    public void onLogin(LoginEvent e) {
        Redis.deleteUserData(e.getConnection().getUniqueId());
        Redis.deleteUserStats(e.getConnection().getUniqueId());
        Redis.deleteToggles(e.getConnection().getUniqueId());
        BungeeUser bungeeUser = (BungeeUser) ManiaCore.getInstance().getUserManager().getUser(e.getConnection().getUniqueId());
        Redis.pushUser(bungeeUser);
        ManiaCore.getInstance().getFriendsManager().loadDataFromDatabase(e.getConnection().getUniqueId());
        Redis.sendCommand("userJoin " + bungeeUser.getUniqueId());
    }
    
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent e) {
        maniaCoreProxy.getManiaCore().getMessageHandler().sendServerSwitchMessage(e.getPlayer().getUniqueId(), e.getPlayer().getServer().getInfo().getName());
    }
    
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        maniaCoreProxy.getManiaCore().getMessageHandler().sendNetworkLeaveMessage(e.getPlayer().getUniqueId());
    
        ManiaCore.getInstance().getPlugin().runTaskLaterAsynchronously(() -> {
            BungeeUser bungeeUser = new BungeeUser(Redis.getUserData(e.getPlayer().getUniqueId()));
            ManiaCoreProxy.saveUserData(bungeeUser);
        }, 1L);
    }
}
