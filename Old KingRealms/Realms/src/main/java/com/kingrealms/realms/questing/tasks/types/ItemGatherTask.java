package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.util.ID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Material.AIR;

public class ItemGatherTask extends Task {
    
    protected int goal;
    private ItemStack itemStack;
    
    public ItemGatherTask(ID id, ID questId, String name, ItemStack itemStack, int goal) {
        super(id, questId, name);
        if (itemStack != null) {
            this.itemStack = itemStack.clone();
            this.itemStack.setAmount(1);
        }
        this.goal = goal;
        
        new BukkitRunnable() {
            public void run() {
                if (!Realms.getInstance().getSeason().isActive()) return;
                if (itemStack == null) {
                    cancel();
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(player);
                    if (!profile.isTaskComplete(getQuest().getId(), getId()) && !profile.isQuestLocked(getQuest())) {
                        ItemStack compare = itemStack.clone();
                        if (profile.getInventory().containsAtLeast(compare, goal)) {
                            onComplete(profile);
                        }
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    public int getGoal() {
        return goal;
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        int amount = 0;
        for (ItemStack itemStack : profile.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == AIR) { continue; }
            if (itemStack.isSimilar(this.itemStack)) {
                amount += itemStack.getAmount();
            }
        }
        
        return getName() + " (" + amount + "/" + goal + ")";
    }
}