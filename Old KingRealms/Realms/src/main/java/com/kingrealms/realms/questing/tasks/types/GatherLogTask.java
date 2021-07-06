package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Material.*;

public class GatherLogTask extends ItemGatherTask {
    
    private final List<Material> logMaterials = new ArrayList<>(Arrays.asList(OAK_LOG, ACACIA_LOG, BIRCH_LOG, DARK_OAK_LOG, JUNGLE_LOG, SPRUCE_LOG));
    
    public GatherLogTask(ID id, ID questId, String name, int goal) {
        super(id, questId, name, null, goal);
        
        new BukkitRunnable() {
            public void run() {
                if (!Realms.getInstance().getSeason().isActive()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(player);
                    if (!profile.isTaskComplete(getQuest().getId(), getId()) && !profile.isQuestLocked(getQuest())) {
                        int amount = 0;
                        for (ItemStack item : profile.getInventory().getContents()) {
                            if (item != null && !item.getType().equals(AIR)) {
                                if (logMaterials.contains(item.getType())) {
                                    amount += item.getAmount();
                                }
                            }
                        }
                        
                        if (amount >= goal) {
                            onComplete(profile);
                        }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        int amount = 0;
        for (ItemStack itemStack : profile.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == AIR) { continue; }
            
            if (logMaterials.contains(itemStack.getType())) {
                amount += itemStack.getAmount();
            }
        }
        
        return getName() + " (" + amount + "/" + goal + ")";
    }
}