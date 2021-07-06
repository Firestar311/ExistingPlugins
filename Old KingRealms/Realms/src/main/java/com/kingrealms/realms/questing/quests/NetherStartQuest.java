package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.types.TravelAreaTask;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.util.ID;
import org.bukkit.scheduler.BukkitRunnable;

public class NetherStartQuest extends Quest {
    private ID travel = new ID("travel_to_spawn_portal");
    
    public NetherStartQuest() {
        super("Starting the Nether", new ID("starting_nether_quest"));
        setDescription("So you want to visit the nether? Come and find me at the spawn Nether Portal");
        TravelAreaTask travelAreaTask = new TravelAreaTask(travel, this.id, "Travel to Spawn Nether Portal");
        addTask(travelAreaTask);
        new BukkitRunnable() {
            @Override
            public void run() {
                Cuboid cuboid = Realms.getInstance().getSettingsManager().getNetherStartCuboid();
                if (cuboid != null) {
                    travelAreaTask.setCuboid(cuboid);
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
        
    }
    
    @Override
    public boolean checkComplete(RealmProfile profile) {
        if (((TravelAreaTask) getTask(travel)).getCuboid() == null) {
            return false;
        }
        
//        if (profile.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE)) {
//            return false;
//        }
        
        return super.checkComplete(profile);
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cI am impressed with your bravery... and stupidity.");
            profile.sendDelayedMessage("&4&lPortal Keeper &cTo access the Nether, you must do some other tasks for me.", 20L);
            return true;
        }
        return false;
    }
}