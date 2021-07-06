package com.stardevmc.enforcer.modules.punishments.listeners;

import com.firestar311.lib.util.Utils;
import com.stardevmc.enforcer.modules.base.ModuleListener;
import com.stardevmc.enforcer.modules.punishments.PunishmentManager;
import com.stardevmc.enforcer.modules.punishments.type.abstraction.Punishment;
import com.stardevmc.enforcer.modules.punishments.type.impl.WarnPunishment;
import com.stardevmc.enforcer.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class PlayerChatListener extends ModuleListener {
    
    private Map<UUID, Integer> notifications = new HashMap<>();
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
    
        PunishmentManager punishmentManager = plugin.getPunishmentModule().getManager();
        if (punishmentManager.isJailed(player.getUniqueId())) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou cannnot speak while jailed."));
            return;
        }
        if (punishmentManager.isMuted(player.getUniqueId())) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou cannot speak while muted."));
            if (this.notifications.containsKey(player.getUniqueId())) {
                this.notifications.put(player.getUniqueId(), this.notifications.get(player.getUniqueId()) + 1);
            } else {
                this.notifications.put(player.getUniqueId(), 1);
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(Perms.NOTIFY_PUNISHMENTS)) {
                    if (this.notifications.containsKey(player.getUniqueId())) {
                        if (this.notifications.get(player.getUniqueId()) < 5) {
                            p.sendMessage(Utils.color("&c" + player.getName() + " tried to speak but is muted."));
                        } else if (this.notifications.get(player.getUniqueId()) == 5) {
                            p.sendMessage(Utils.color("&c" + player.getName() + " continues to speak, silencing notifications"));
                        }
                    }
                }
            }
            return;
        }
    
        e.getRecipients().removeIf(recipient -> punishmentManager.isJailed(recipient.getUniqueId()));
        
        for (Punishment punishment : punishmentManager.getWarnings(player.getUniqueId())) {
            if (!plugin.getTrainingModule().getManager().isTrainingMode(punishment.getPunisher())) {
                WarnPunishment warning = (WarnPunishment) punishment;
                if (!warning.isAcknowledged()) {
                    e.setCancelled(true);
                    warning.createPrompt();
                    break;
                }
            }
        }
    }
}