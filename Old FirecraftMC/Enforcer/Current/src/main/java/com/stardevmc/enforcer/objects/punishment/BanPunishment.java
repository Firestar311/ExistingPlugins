package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.Target;
import com.stardevmc.enforcer.util.Messages;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class BanPunishment extends Punishment {
    
    public BanPunishment(Type type, String server, Actor punisher, Target target, String reason, long date) {
        super(type, server, punisher, target, reason, date);
    }
    
    public BanPunishment(Type type, String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(type, server, punisher, target, reason, date, visibility);
    }
    
    public BanPunishment(String id, Type type, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility) {
        super(id, type, server, punisher, target, reason, date, active, visibility);
    }
    
    public static BanPunishment deserialize(Map<String, Object> serialized) {
        return (BanPunishment) Punishment.deserializeBase(serialized).build();
    }
    
    public void executePunishment(ExecuteOptions... options) {
        Player player = target.getPlayer();
        if (player != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Enforcer.getInstance(), () -> player.kickPlayer(Utils.color(Messages.formatPunishKick(this))));
        } else {
            setOffline(true);
        }
        
        setActive(true);
        sendPunishMessage(options);
    }
    
    public void reversePunishment(Actor remover, long removedDate, String removedReason) {
        setRemover(remover);
        setRemovedDate(removedDate);
        setActive(false);
        setRemovedReason(removedReason);
        sendRemovalMessage();
    }
    
    public Map<String, Object> serialize() {
        return super.serializeBase();
    }
}
