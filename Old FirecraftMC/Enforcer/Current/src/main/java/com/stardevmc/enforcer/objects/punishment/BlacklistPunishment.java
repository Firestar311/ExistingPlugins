package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.*;
import com.starmediadev.lib.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class BlacklistPunishment extends Punishment {
    
    public BlacklistPunishment(String server, Actor punisher, Target target, String reason, long date) {
        super(Type.BLACKLIST, server, punisher, target, reason, date);
    }
    
    public BlacklistPunishment(String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(Type.BLACKLIST, server, punisher, target, reason, date, visibility);
    }
    
    public BlacklistPunishment(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility) {
        super(id, Type.BLACKLIST, server, punisher, target, reason, date, active, visibility);
    }
    
    public void executePunishment(ExecuteOptions... options) {
        List<User> affectedPlayers = new ArrayList<>();
        for (User info : Enforcer.getInstance().getPlayerManager().getUsers().values()) {
            playerIPLoop:
            for (String ip : info.getIpAddresses()) {
                if (target instanceof IPTarget) {
                    IPTarget ipTarget = (IPTarget) this.target;
                    if (ip.equals(ipTarget.getIpAddress())) {
                        affectedPlayers.add(info);
                    }
                } else if (target instanceof IPListTarget) {
                    IPListTarget ipListTarget = (IPListTarget) this.target;
                    for (String ipt : ipListTarget.getIpAddresses()) {
                        if (ip.equals(ipt)) {
                            affectedPlayers.add(info);
                            continue playerIPLoop;
                        }
                    }
                }
            }
        }
        
        for (User affectedPlayer : affectedPlayers) {
            Player player = Bukkit.getPlayer(affectedPlayer.getUniqueId());
            if (player != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Enforcer.getInstance(), () -> player.kickPlayer("Blacklisted"));
            }
        }
        
        sendPunishMessage(options);
    }
    
    public void reversePunishment(Actor remover, long removedDate, String removedReason) {
        setActive(false);
        setRemover(remover);
        setRemovedDate(removedDate);
        setRemovedReason(removedReason);
        sendRemovalMessage();
    }
    
    public Map<String, Object> serialize() {
        return super.serializeBase();
    }
    
    public static BlacklistPunishment deserialize(Map<String, Object> serialized) {
        return ((BlacklistPunishment) Punishment.deserializeBase(serialized).build());
    }
}