package com.kingrealms.realms.questing.tasks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.quests.Quest;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

public class EnterDimensionTask extends Task {
    
    public EnterDimensionTask(ID id, ID questId, String name, Environment dimension) {
        super(id, questId, name);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    RealmProfile profile = RealmsAPI.getProfile(player);
                    if (!profile.isTaskComplete(questId, id)) {
                        if (profile.isActiveQuestLine(getQuest().getParentLine())) {
                            for (World world : Bukkit.getWorlds()) {
                                if (world.getEnvironment() == dimension) { //TODO Use a config for this
                                    onComplete(profile);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        if (e.getCause() == TeleportCause.NETHER_PORTAL) {
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            Quest quest = getQuest().getParentLine().getPreviousQuest(getQuest());
            if (profile.isQuestLocked(quest) || !profile.isQuestComplete(quest)) {
                e.setCancelled(true);
                profile.sendMessage("&4&lPortal Keeper &cYou must first complete the Nether Quest Line.");
            }
        }
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}