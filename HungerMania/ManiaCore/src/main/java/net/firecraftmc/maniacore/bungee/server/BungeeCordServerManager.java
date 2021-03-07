package net.firecraftmc.maniacore.bungee.server;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.server.ServerManager;

public class BungeeCordServerManager extends ServerManager {
    public BungeeCordServerManager(ManiaCore maniaCore) {
        super(maniaCore);
    }

    @Override
    public void init() {
        this.currentServer = new net.firecraftmc.maniacore.api.server.ManiaServer("Proxy", TimoCloudAPI.getBungeeAPI().getThisProxy().getPort());
        this.currentServer.setType(net.firecraftmc.maniacore.api.server.ServerType.PROXY);
    }
    
    protected void handleServerStart(String server) {
        
    }
    
    protected void handleGameReady(String server) {
        
    }
    
    protected void handleServerStop(String server) {
        
    }
}
