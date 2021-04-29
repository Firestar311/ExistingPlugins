package net.firecraftmc.maniacore.bungee.user;

import net.firecraftmc.maniacore.CenturionsCoreProxy;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.UserManager;
import redis.clients.jedis.Jedis;

import java.util.*;

public class BungeeUserManager extends UserManager {
    
    public BungeeUserManager(CenturionsCoreProxy plugin) {
        plugin.runTaskTimerAsynchronously(() -> {
            try (Jedis jedis = Redis.getConnection()) {
                Set<String> keys = jedis.keys("USER:*");
                for (String key : keys) {
                    UUID uuid = UUID.fromString(key.split(":")[1]);
                    BungeeUser bungeeUser = new BungeeUser(Redis.getUserData(uuid));
                    CenturionsCoreProxy.saveUserData(bungeeUser);
                }
            }
        }, 20L, 6000L);
        
    }
    
    public User constructUser(UUID uuid, String name) {
        return new BungeeUser(uuid, name);
    }
    
    public User constructUser(Map<String, String> data) {
        return new BungeeUser(data);
    }
    
    public User constructUser(User user) {
        return new BungeeUser(user);
    }
}
