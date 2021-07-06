package com.stardevmc.chat;

import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Member implements IMember, ConfigurationSerializable {
    
    private UUID uuid;
    private IRole role;
    
    public Member(UUID uuid, IRole role) {
        this.uuid = uuid;
        this.role = role;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.uuid.toString());
        serialized.put("role", this.role.getName());
        return serialized;
    }
    
    public static Member deserialize(Map<String, Object> serialized) {
        UUID uuid = UUID.fromString((String) serialized.get("uuid"));
        IRole role = DefaultRoles.valueOf(((String) serialized.get("role")).toUpperCase()); //This will change when custom roles are implemented
        return new Member(uuid, role);
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public IRole getRole() {
        return role;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    public void sendMessage(String message) {
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(Utils.color(message));
        }
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Member member = (Member) o;
        return Objects.equals(uuid, member.uuid);
    }
    
    public int hashCode() {
        return Objects.hash(uuid);
    }
}