package net.firecraftmc.maniacore.bungee.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.server.CenturionsServer;
import net.firecraftmc.maniacore.api.server.ServerManager;
import net.firecraftmc.maniacore.api.server.ServerType;

public class BungeeCordServerManager extends ServerManager {
    public BungeeCordServerManager(CenturionsCore centurionsCore) {
        super(centurionsCore);
    }

    @Override
    public void init() {
        this.currentServer = new CenturionsServer("Proxy", TimoCloudAPI.getBungeeAPI().getThisProxy().getPort());
        this.currentServer.setType(ServerType.PROXY);
    }
    
    protected void handleServerStart(String server) {
        
    }
    
    protected void handleGameReady(String server) {
        
    }
    
    protected void handleServerStop(String server) {
        
    }
}
