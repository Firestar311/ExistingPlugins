package com.stardevmc.enforcer.modules.punishments.listeners;

import com.stardevmc.enforcer.modules.base.ModuleListener;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.punishment.Punishment.Type;
import com.stardevmc.enforcer.objects.punishment.TemporaryBan;
import com.stardevmc.enforcer.util.Messages;
import com.stardevmc.enforcer.util.Perms;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.*;

public class PlayerJoinListener extends ModuleListener {
    
    private Map<UUID, Integer> notifications = new HashMap<>();
    
    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        if (plugin.getPunishmentModule().getManager().isBlacklisted(e.getAddress().toString().split(":")[0].replace("/", ""))) {
            e.disallow(Result.KICK_BANNED, Utils.color("Blacklisted")); //TODO Better message
            return;
        }
        
        UUID player = e.getUniqueId();
        if (plugin.getPunishmentModule().getManager().isBanned(e.getUniqueId())) {
            List<Punishment> bans = new ArrayList<>(plugin.getPunishmentModule().getManager().getActiveBans(e.getUniqueId()));
            Punishment worsePunishment = null;
            for (Punishment IPunishment : bans) {
                if (worsePunishment == null) {
                    worsePunishment = IPunishment;
                } else {
                    if (IPunishment.getType().equals(Type.PERMANENT_BAN) && worsePunishment.getType().equals(Type.TEMPORARY_BAN)) {
                        worsePunishment = IPunishment;
                    } else if (IPunishment.getType().equals(Type.TEMPORARY_BAN) && worsePunishment.getType().equals(Type.TEMPORARY_BAN)){
                        TemporaryBan tempBan = (TemporaryBan) IPunishment;
                        TemporaryBan worseTempBan = (TemporaryBan) worsePunishment;
                        if (tempBan.getExpireDate() > worseTempBan.getExpireDate()) {
                            worsePunishment = IPunishment;
                        }
                    }
                }
            }
            e.disallow(Result.KICK_BANNED, Utils.color(Messages.formatPunishKick(worsePunishment)));
            
            if (this.notifications.containsKey(player)) {
                this.notifications.put(player, this.notifications.get(player) + 1);
            } else {
                this.notifications.put(player, 1);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(Perms.NOTIFY_JOIN)) {
                    if (this.notifications.containsKey(player)) {
                        if (this.notifications.get(player) < 5) {
                            p.sendMessage(Utils.color(Messages.punishmentNoAction(e.getName(), "join", "banned")));
                        } else if (this.notifications.get(player) == 5) {
                            p.sendMessage(Utils.color(Messages.punishmentNotifySilence(e.getName(), "joining")));
                        }
                    }
                    int punismentCount = plugin.getPunishmentModule().getManager().getPunishments(e.getUniqueId()).size();
                    if (punismentCount > 14) { //TODO Make this a setting
                        p.sendMessage(Utils.color(Messages.historyInfractionLimit(e.getName(), punismentCount)));
                    }
                }
            }
        }
    }
}