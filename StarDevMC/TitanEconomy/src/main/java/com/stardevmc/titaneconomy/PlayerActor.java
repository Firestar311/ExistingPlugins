package com.stardevmc.titaneconomy;

import com.firestar311.lib.player.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerActor extends Actor {
    private UUID uniqueId;
    
    public PlayerActor(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public PlayerActor(Player player) {
        this.uniqueId = player.getUniqueId();
    }
    
    public PlayerActor(User user) {
        this.uniqueId = user.getUniqueId();
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public User getPlayerInfo() {
        return TitanEconomy.getInstance().getPlayerManager().getUser(this.uniqueId);
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }
}