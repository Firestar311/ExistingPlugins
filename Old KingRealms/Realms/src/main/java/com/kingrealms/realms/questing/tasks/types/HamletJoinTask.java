package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.HamletJoinEvent;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class HamletJoinTask extends HamletTask {
    public HamletJoinTask(ID id, ID questId) {
        super(id, questId, "Join a Hamlet");
        setOptional(true);
        
        new BukkitRunnable() {
            public void run() {
                if (!Realms.getInstance().getSeason().isActive()) return;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(p);
                    if (!profile.isTaskComplete(getQuest().getId(), getId())) {
                        if (Realms.getInstance().getTerritoryManager().getTerritory(profile) != null) {
                            onComplete(profile);
                        }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    @EventHandler
    public void onHamletJoin(HamletJoinEvent e) {
        onComplete(e.getProfile());
    }
}