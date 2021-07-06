package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.kingrealms.realms.questing.tasks.TaskProgress;
import com.kingrealms.realms.questing.tasks.progress.IntegerTaskProgress;
import com.starmediadev.lib.util.ID;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class MineObsidianTask extends Task {
    
    private Material material;
    int amount;
    
    public MineObsidianTask(ID questId) {
        super(new ID("mine_obsidian"), questId, "Mine 32 more Obsidian");
        this.material = Material.OBSIDIAN;
        this.amount = 32;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(e.getPlayer());
        Block block = e.getBlock();
        if (!profile.isTaskComplete(questId, id) && profile.isActiveQuestLine(getQuest().getParentLine())) {
            if (block.getType().equals(material)) {
                if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    if (profile.isActiveQuestLine(Realms.getInstance().getQuestManager().getNetherQuestLine())) {
                        profile.resetProgress(Realms.getInstance().getQuestManager().getNetherQuestLine());
                        profile.sendMessage("&c&lPortal Keeper &cYou broke obsidian while in CREATIVE while doing the nether quest line. Progress Reset!");
                        return;
                    }
                }
                
                ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
                if (hand.getType() != Material.DIAMOND_PICKAXE) {
                    profile.sendMessage("&c&lPortal Keeper &cYou can only mine obsidian with a Diamond Pickaxe you imbicile.");
                    e.setCancelled(true);
                }
                
                TaskProgress progress = profile.getTaskProgress(id);
                if (progress == null) {
                    progress = new IntegerTaskProgress(this);
                    profile.addTaskProgress(this, progress);
                }
    
                IntegerTaskProgress intProgress = (IntegerTaskProgress) progress;
                
                if (hand.getItemMeta().hasEnchant(Enchantment.DIG_SPEED)) {
                    intProgress.setValue(0);
                    profile.sendMessage("&c&lPortal Keeper &cOh... You cannot mine obsidian with the Efficiency Enchantment.");
                    profile.sendDelayedMessage("&c&lPortal Keeper &cI have reset your current progress", 10L);
                    e.setCancelled(true);
                }
    
                if (e.getPlayer().getActivePotionEffects().size() != 0) {
                    intProgress.setValue(0);
                    profile.sendMessage("&c&lPortal Keeper &cOh... You cannot mine obsidian with any potion effects.");
                    profile.sendDelayedMessage("&c&lPortal Keeper &cI have reset your current progress", 10L);
                    e.setCancelled(true);
                }
    
                if (intProgress.getValue() < amount) {
                    intProgress.setValue(intProgress.getValue() + 1);
                    if (intProgress.getValue() >= amount) {
                        onComplete(profile);
                    }
                } else {
                    onComplete(profile);
                }
            }
        }
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        IntegerTaskProgress progress = (IntegerTaskProgress) profile.getTaskProgress(getId());
        int amount = 0;
        if (progress != null) {
            amount = progress.getValue();
        }
        return getName() + " (" + amount + " / " + this.amount + ")";
    }
}