package com.stardevmc.enforcer.modules.punishments.listeners;

import com.stardevmc.enforcer.modules.base.ModuleListener;
import com.stardevmc.enforcer.manager.PunishmentManager;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.punishment.WarnPunishment;
import com.stardevmc.enforcer.util.Messages;
import com.stardevmc.enforcer.util.Perms;
import com.starmediadev.lib.util.Utils;
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
            player.sendMessage(Utils.color(Messages.prisonNoAction("speak")));
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
                            p.sendMessage(Utils.color(Messages.punishmentNoAction(player.getName(), "speak", "muted")));
                        } else if (this.notifications.get(player.getUniqueId()) == 5) {
                            p.sendMessage(Utils.color(Messages.punishmentNotifySilence(player.getName(), "speak")));
                        }
                    }
                }
            }
            return;
        }
    
        e.getRecipients().removeIf(recipient -> punishmentManager.isJailed(recipient.getUniqueId()));
        
        for (Punishment IPunishment : punishmentManager.getWarnings(player.getUniqueId())) {
            if (!plugin.getTrainingModule().getManager().isTrainingMode(IPunishment.getActor())) {
                WarnPunishment warning = (WarnPunishment) IPunishment;
                if (!warning.isAcknowledged()) {
                    e.setCancelled(true);
                    warning.createPrompt();
                    break;
                }
            }
        }
    }
}