package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.KitClaimEvent;
import com.kingrealms.realms.kits.Kit;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class KitTask extends Task {
    
    private String kitName;
    
    public KitTask(ID id, ID questId, String name) {
        super(id, questId, name);
        
        new BukkitRunnable() {
            public void run() {
                if (!Realms.getInstance().getSeason().isActive()) return;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(p);
                    if (!profile.isTaskComplete(getQuest().getId(), getId())) {
                        Kit kit = Realms.getInstance().getKitManager().getKit(kitName);
                        if (profile.getKitUses(kit) > 0) {
                            onComplete(profile);
                        }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    public KitTask(ID id, ID questId, String name, String description, String kitName) {
        this(id, questId, name);
        this.description = description;
        this.kitName = kitName;
    }
    
    @EventHandler
    public void onKitClaim(KitClaimEvent e) {
        if (e.getKit().getName().equalsIgnoreCase(kitName)) {
            onComplete(e.getProfile());
        }
    }
}