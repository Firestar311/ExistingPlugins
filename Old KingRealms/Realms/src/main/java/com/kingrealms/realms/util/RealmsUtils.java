package com.kingrealms.realms.util;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.loot.Loot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.farming.CropBlock;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class RealmsUtils {
    
    private RealmsUtils() {}
    
    private static final Set<Material> UNBREAKABLE = Set.of(Material.BEDROCK, Material.END_PORTAL_FRAME, Material.END_PORTAL, Material.NETHER_PORTAL, Material.BARRIER, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK);
    
    public static  boolean isMember(Player player) {
        Territory territory = Realms.getInstance().getTerritoryManager().getTerritory(player);
        if (territory == null) {
            return true;
        }
        
        if (player == null) {
            return true;
        }
        
        Member member = territory.getMember(player.getUniqueId());
        return member != null;
    }
    
    public static void addCropLoot(CropBlock cropBlock, RealmProfile profile) {
        List<Loot> drops = cropBlock.getLootTable().generateLoot();
        for (Loot loot : drops) {
            profile.getInventory().addItem(loot.getItemStack());
        }
        profile.addSkillExperience(SkillType.FARMING, cropBlock.getLevel().getXpPerHarvest());
    }
    
    public static List<Block> handleThreeByThreeTool(BlockBreakEvent e, Material material, Map<UUID, BlockFace> BLOCK_FACE_MAP, ID id) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack hand = player.getInventory().getItemInMainHand();
    
        if (hand == null) {return null;}
        if (hand.getType() != material) return null;
    
        String itemId;
        try {
            itemId = NBTWrapper.getNBTString(hand, "itemid");
        } catch (Exception ex) { return null; }
    
        if (!itemId.equalsIgnoreCase(id.toString())) return null;
        if (!(hand.getItemMeta() instanceof Damageable)) return null;
    
        ItemMeta itemMeta = hand.getItemMeta();
        Damageable damageable = (Damageable) itemMeta;
        short maxDamage = hand.getType().getMaxDurability();
    
        if (!BLOCK_FACE_MAP.containsKey(player.getUniqueId())) return null;
    
        List<Block> blocks = Utils.getSurroundingBlocks(BLOCK_FACE_MAP.get(player.getUniqueId()), block);
        e.setCancelled(true);
        for (Block b : blocks) {
            if (damageable.getDamage() == maxDamage) break;
            if (Realms.getInstance().getSpawn().contains(b.getLocation()) || Realms.getInstance().getWarzone().contains(b.getLocation())) continue;
            if (Realms.getInstance().getMiningManager().getMysticalBlock(b.getLocation()) != null) {
                e.setCancelled(false);
                continue;
            }
            if (Realms.getInstance().getSpawnerManager().getSpawner(b.getLocation()) != null) continue;
            if (Realms.getInstance().getFarmingManager().getCropBlock(b.getLocation()) != null) continue;
            if (Realms.getInstance().getGraveManager().getGrave(b.getLocation()) != null) continue;
    
            Territory territory = Realms.getInstance().getTerritoryManager().getTerritory(b.getLocation());
            if (territory != null) {
                if (!territory.canEnter(RealmsAPI.getProfile(player))) continue;
            }
            
            if (UNBREAKABLE.contains(b.getType())) continue;
            
            boolean result = b.breakNaturally(hand);
            if (result) {
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    damageable.setDamage(damageable.getDamage() + 1);
                    hand.setItemMeta(itemMeta);
                }
            }
        }
        return blocks;
    }
}