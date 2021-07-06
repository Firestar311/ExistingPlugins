package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TravelAreaTask extends Task {
    
    private Cuboid cuboid;
    
    public TravelAreaTask(ID id, ID questId, String name) {
        super(id, questId, name);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cuboid != null) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(player);
                        if (!profile.isTaskComplete(questId, id)) {
                            if (cuboid.contains(player.getLocation())) {
                                onComplete(profile);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    public Cuboid getCuboid() {
        return cuboid;
    }
    
    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}