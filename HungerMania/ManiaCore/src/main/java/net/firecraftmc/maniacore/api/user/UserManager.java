package net.firecraftmc.maniacore.api.user;

import net.firecraftmc.maniacore.api.records.IgnoreInfoRecord;
import net.firecraftmc.maniacore.api.records.StatRecord;
import net.firecraftmc.maniacore.api.records.ToggleRecord;
import net.firecraftmc.maniacore.api.records.UserRecord;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.stats.Stat;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.user.toggle.Toggle;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.util.ManiaUtils;
import net.firecraftmc.manialib.sql.IRecord;

import java.util.*;

public abstract class UserManager {
    public net.firecraftmc.maniacore.api.user.User getUser(String name) {
        System.out.println("Getting user by name " + name);
        if (name == null && name.isEmpty()) { return null; }
        UUID uuid = net.firecraftmc.maniacore.api.redis.Redis.getUUIDFromName(name);
        System.out.println("UUID is " + uuid);
        net.firecraftmc.maniacore.api.user.User user = getUser(uuid);
        System.out.println("User is " + user);
        if (user == null) {
            System.out.println("User is null, loading from database");
            List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.api.records.UserRecord.class, "name", name);
            System.out.println("Record size " + records.size());
            if (!records.isEmpty()) {
                System.out.println("Record exists, constructing");
                user = constructUser(((net.firecraftmc.maniacore.api.records.UserRecord) records.get(0)).toObject());
                System.out.println("User is " + user);
            } else {
                System.out.println("No record exists");
                user = constructUser(ManiaUtils.getUUIDFromName(name), name);
                System.out.println("User is " + user);
                ManiaCore.getInstance().getDatabase().pushRecord(new net.firecraftmc.maniacore.api.records.UserRecord(user));
                System.out.println("Pushed to database");
            }
            System.out.println("Loading data based on UUID");
            user = getUser(user.getUniqueId()); //TODO Inefficient, temporary fix, trying to find the cause
            System.out.println("Loaded, pushing to Redis");
            net.firecraftmc.maniacore.api.redis.Redis.pushUser(user);
        }
        
        return user;
    }
    
    public void loadIgnoredPlayers(net.firecraftmc.maniacore.api.user.User user) {
        List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.api.records.IgnoreInfoRecord.class, "player", user.getUniqueId().toString());
        Set<IgnoreInfo> ignoredPlayers = new HashSet<>();
        if (!records.isEmpty()) {
            for (IRecord record : records) {
                if (record instanceof net.firecraftmc.maniacore.api.records.IgnoreInfoRecord) {
                    ignoredPlayers.add(((IgnoreInfoRecord) record).toObject());
                }
            }
        }
        
        user.setIgnoredPlayers(ignoredPlayers);
    }
    
    public net.firecraftmc.maniacore.api.user.User getUser(int userId) {
        if (userId < 1) { return null; }
        UUID uuid = net.firecraftmc.maniacore.api.redis.Redis.getUUIDFromID(userId);
        net.firecraftmc.maniacore.api.user.User user = getUser(uuid);
        
        if (user == null) {
            List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.api.records.UserRecord.class, "id", userId);
            if (!records.isEmpty()) {
                user = constructUser(((net.firecraftmc.maniacore.api.records.UserRecord) records.get(0)).toObject());
                net.firecraftmc.maniacore.api.redis.Redis.pushUser(user);
            }
        }
        
        return user;
    }
    
    public net.firecraftmc.maniacore.api.user.User getUser(UUID uuid) {
        if (uuid == null) { return null; }
    
        System.out.println("Loading user " + uuid);
        Map<String, String> redisData = net.firecraftmc.maniacore.api.redis.Redis.getUserData(uuid);
        Map<String, net.firecraftmc.maniacore.api.stats.Statistic> stats = new HashMap<>();
        Map<String, net.firecraftmc.maniacore.api.stats.Statistic> fakedStats = new HashMap<>();
        Map<net.firecraftmc.maniacore.api.user.toggle.Toggles, net.firecraftmc.maniacore.api.user.toggle.Toggle> toggles = new HashMap<>();
        net.firecraftmc.maniacore.api.user.User user;
        if (!redisData.isEmpty()) {
            System.out.println("Redis contains user data, loading...");
            user = constructUser(redisData);
            stats = net.firecraftmc.maniacore.api.redis.Redis.getUserStats(uuid);
            toggles = net.firecraftmc.maniacore.api.redis.Redis.getToggles(uuid);
            fakedStats = net.firecraftmc.maniacore.api.redis.Redis.getUserFakedStats(uuid);
            System.out.println("Loaded user data from redis");
        } else {
            System.out.println("Redis does not contain user data, loading from main database");
            List<IRecord> userRecords = ManiaCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.api.records.UserRecord.class, "uniqueId", uuid.toString());
            if (!userRecords.isEmpty()) {
                System.out.println("Database contains user data, loading...");
                user = constructUser(((net.firecraftmc.maniacore.api.records.UserRecord) userRecords.get(0)).toObject());
                System.out.println("Loaded from database");
            } else {
                System.out.println("Database does not contain user data, creating");
                user = constructUser(uuid, ManiaUtils.getNameFromUUID(uuid));
                System.out.println("Created new user data, saving to database...");
                ManiaCore.getInstance().getDatabase().pushRecord(new net.firecraftmc.maniacore.api.records.UserRecord(user));
                System.out.println("Saved successfully");
            }
        
            System.out.println("User: " + user.getName());
            List<IRecord> statsRecords = ManiaCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.api.records.StatRecord.class, "uuid", uuid.toString());

            System.out.println("Stats Records Size: " + statsRecords.size());
            if (!statsRecords.isEmpty()) {
                System.out.println("Loading stats");
                for (IRecord statsRecord : statsRecords) {
                    net.firecraftmc.maniacore.api.stats.Statistic statistic = ((net.firecraftmc.maniacore.api.records.StatRecord) statsRecord).toObject();
                    stats.put(statistic.getName(), statistic);
                }
                System.out.println("Loaded " + stats.size() + " stats");
            }
        
            List<IRecord> togglesRecords = ManiaCore.getInstance().getDatabase().getRecords(net.firecraftmc.maniacore.api.records.ToggleRecord.class, "uuid", uuid.toString());
            System.out.println("Toggles Record size: " + togglesRecords.size());
            if (!togglesRecords.isEmpty()) {
                System.out.println("Loading Toggles");
                for (IRecord togglesRecord : togglesRecords) {
                    net.firecraftmc.maniacore.api.user.toggle.Toggle toggle = ((net.firecraftmc.maniacore.api.records.ToggleRecord) togglesRecord).toObject();
                    net.firecraftmc.maniacore.api.user.toggle.Toggles type = net.firecraftmc.maniacore.api.user.toggle.Toggles.valueOf(toggle.getName().toUpperCase());
                    toggles.put(type, toggle);
                }
                System.out.println("Loaded " + toggles.size() + " toggles");
            }
        }

        System.out.println("Creating default stat information for missing stats");
        for (net.firecraftmc.maniacore.api.stats.Stat value : Stat.REGISTRY.values()) {
            if (!stats.containsKey(value.getName())) {
                Statistic statistic = value.create(uuid);
                ManiaCore.getInstance().getDatabase().pushRecord(new StatRecord(statistic));
                stats.put(value.getName(), statistic);
            }
        }
        System.out.println("Completed");

        System.out.println("Creating default toggle information for default toggles");
        for (net.firecraftmc.maniacore.api.user.toggle.Toggles value : Toggles.values()) {
            if (!toggles.containsKey(value)) {
                Toggle toggle = value.create(uuid);
                ManiaCore.getInstance().getDatabase().pushRecord(new ToggleRecord(toggle));
                toggles.put(value, toggle);
            }
        }
        System.out.println("Completed");

        System.out.println("Setting in memory information");
        user.setStats(stats);
        user.setToggles(toggles);
        user.setFakeStats(fakedStats);
        System.out.println("Done");
        
        if (user.getName() == null || user.getName().equals("") || user.getName().equals("null")) {
            System.out.println("updated user name");
            user.setName(ManiaUtils.getNameFromUUID(uuid));
            ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(user));
        }
        System.out.println("Pushing user to redis");
        Redis.pushUser(user);
        System.out.println("Pushed user to redis");
        return user;
    }
    
    public abstract net.firecraftmc.maniacore.api.user.User constructUser(UUID uuid, String name);
    
    public abstract net.firecraftmc.maniacore.api.user.User constructUser(Map<String, String> data);
    
    public abstract net.firecraftmc.maniacore.api.user.User constructUser(User user);
}