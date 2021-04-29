package net.firecraftmc.maniacore.api.server;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.redis.RedisListener;
import net.firecraftmc.maniacore.api.util.CenturionsProperties;

@SuppressWarnings("DuplicatedCode")
public abstract class ServerManager implements RedisListener {
    protected CenturionsServer currentServer;
    protected CenturionsCore centurionsCore;
    protected NetworkType networkType = CenturionsProperties.NETWORK_TYPE;
    
    public ServerManager(CenturionsCore centurionsCore) {
        this.centurionsCore = centurionsCore;
    }
    
    public abstract void init();
    
    public CenturionsServer getCurrentServer() {
        return currentServer;
    }
    
    protected abstract void handleServerStart(String server);
    protected abstract void handleGameReady(String server);
    public void onCommand(String cmd, String[] args) {
        if (cmd.equals("serverStart")) {
            if (args.length != 1) return;
            String server = args[0];
            handleServerStart(server);
        } else if (cmd.equals("gameReady")) {
            if (args.length != 1) return;
            String server = args[0];
            handleGameReady(server);
        } else if (cmd.equals("serverStop")) {
            if (args.length != 1) {
                String server = args[0];
                handleServerStop(server);
            }
        }
    }
    
    protected abstract void handleServerStop(String server);
    
    public void sendServerStart(String server) {
        Redis.sendCommand("serverStart " + server);
    }
    
    public void sendServerStop(String server) {
//        try (Jedis jedis = Redis.getConnection()) {
//            jedis.publish(CROSSTALK_CHANNEL, "serverStop " + server);
//        }
    }
    
    public void sendGameReady(String server) {
        Redis.sendCommand("gameReady " + server);
    }
}