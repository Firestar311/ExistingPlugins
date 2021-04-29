package net.firecraftmc.maniacore.bungee.user;

import net.firecraftmc.maniacore.CenturionsCoreProxy;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.redis.RedisListener;

import java.util.UUID;

public class UserRedisListener implements RedisListener {
    public void onCommand(String cmd, String[] args) {
        if (cmd.equalsIgnoreCase("saveUserData")) {
            UUID uuid = UUID.fromString(args[0]);
            BungeeUser bungeeUser = new BungeeUser(Redis.getUserData(uuid));
            CenturionsCoreProxy.saveUserData(bungeeUser);
        }
    }
}
