package com.kingrealms.realms.warps.type;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.ServerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@SerializableAs("ServerWarp")
public class ServerWarp extends Warp {
    //mysql
    
    public ServerWarp(int id, String name, String description, String permission, Location location) {
        super(id, "Console", name, description, permission, location);
    }
    
    public ServerWarp(Location location) {
        super("Console", location);
    }
    
    public ServerWarp(String name, Location location) {
        super("Console", name, location);
    }
    
    public ServerWarp(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public ServerProfile getOwner() {
        return new ServerProfile();
    }
    
    @Override
    public boolean canAccess(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            if (player.hasPermission("realms.warps.override.server") || player.hasPermission("realms.warps.override.*")) return true;
        }
        return hasPermission(Realms.getInstance().getProfileManager().getProfile(uuid));
    }
}