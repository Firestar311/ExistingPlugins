package net.firecraftmc.maniacore.bungee.user;

import net.firecraftmc.maniacore.ManiaCoreProxy;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.UserManager;
import redis.clients.jedis.Jedis;

import java.util.*;

public class BungeeUserManager extends UserManager {
    
    public BungeeUserManager(ManiaCoreProxy plugin) {
        plugin.runTaskTimerAsynchronously(() -> {
            try (Jedis jedis = Redis.getConnection()) {
                Set<String> keys = jedis.keys("USER:*");
                for (String key : keys) {
                    UUID uuid = UUID.fromString(key.split(":")[1]);
                    net.firecraftmc.maniacore.bungee.user.BungeeUser bungeeUser = new net.firecraftmc.maniacore.bungee.user.BungeeUser(Redis.getUserData(uuid));
                    ManiaCoreProxy.saveUserData(bungeeUser);
                }
            }
        }, 20L, 6000L);
        
    }
    
    public User constructUser(UUID uuid, String name) {
        return new net.firecraftmc.maniacore.bungee.user.BungeeUser(uuid, name);
    }
    
    public User constructUser(Map<String, String> data) {
        return new net.firecraftmc.maniacore.bungee.user.BungeeUser(data);
    }
    
    public User constructUser(User user) {
        return new BungeeUser(user);
    }
}
