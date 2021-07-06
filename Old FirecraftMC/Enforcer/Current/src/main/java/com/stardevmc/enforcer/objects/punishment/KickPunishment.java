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

public class KickPunishment extends Punishment {
    
    public KickPunishment(String server, Actor punisher, Target target, String reason, long date) {
        super(Type.KICK, server, punisher, target, reason, date);
    }
    
    public KickPunishment(String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(Type.KICK, server, punisher, target, reason, date, visibility);
    }
    
    public KickPunishment(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility) {
        super(id, Type.KICK, server, punisher, target, reason, date, active, visibility);
    }
    
    public void executePunishment(ExecuteOptions... options) {
        Player player = target.getPlayer();
        if (player != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Enforcer.getInstance(), () -> player.kickPlayer(Utils.color(Messages.formatPunishKick(this))));
        }
    
        sendPunishMessage();
    }
    
    public void reversePunishment(Actor remover, long removedDate, String removedReason) {}
    
    public Map<String, Object> serialize() {
        return super.serializeBase();
    }
    
    public static KickPunishment deserialize(Map<String, Object> serialized) {
        return (KickPunishment) deserializeBase(serialized).build();
    }
}
