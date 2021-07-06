package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.util.Utils;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class MutePunishment extends Punishment {
    public MutePunishment(Type type, String server, Actor punisher, Target target, String reason, long date) {
        super(type, server, punisher, target, reason, date);
    }
    
    public MutePunishment(Type type, String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(type, server, punisher, target, reason, date, visibility);
    }
    
    public MutePunishment(String id, Type type, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility) {
        super(id, type, server, punisher, target, reason, date, active, visibility);
    }
    
    public void executePunishment(ExecuteOptions... options) {
        Player player = this.target.getPlayer();
        if (player != null) {
            String message = "&cYou have been muted by &7" + getPunisherName() + " &cfor &7" + this.reason;
            if (this instanceof Expireable) {
                message += " " + ((Expireable) this).formatExpireTime();
            }
            player.sendMessage(Utils.color(message));
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
        Player player = this.target.getPlayer();
        if (player != null) {
            player.sendMessage(Utils.color("&aYou have been unmuted by &b" + getRemoverName()));
        }
    }
    
    public Map<String, Object> serialize() {
        return super.serializeBase();
    }
    
    public static MutePunishment deserialize(Map<String, Object> serialized) {
        return (MutePunishment) Punishment.deserializeBase(serialized).build();
    }
}
