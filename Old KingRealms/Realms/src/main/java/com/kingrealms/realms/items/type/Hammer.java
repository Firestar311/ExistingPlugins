package com.kingrealms.realms.items.type;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.HammerMineEvent;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.items.*;
import com.kingrealms.realms.util.RealmsUtils;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Hammer extends CustomItem implements Listener {
    
    private static final Map<UUID, BlockFace> BLOCK_FACE_MAP = new HashMap<>();
    private ToolType toolType;
    
    public Hammer(ToolType type) {
        super(new ID(type.name().toLowerCase() + "_hammer"), Utils.capitalizeEveryWord(type.name()) + " Hammer", "A 3 x 3 tool for mining", ToolType.PICKAXES.get(type), ItemType.TOOL, true);
        Realms.getInstance().getSeason().addListener(this);
        this.lore.add("&b" + description);
        this.toolType = type;
    }
    
    public ToolType getToolType() {
        return toolType;
    }
    
    public static boolean isHammer(ItemStack itemStack) {
        try {
            String itemid = NBTWrapper.getNBTString(itemStack, "itemid");
            if (StringUtils.isEmpty(itemid)) return false;
            CustomItem customItem = CustomItemRegistry.REGISTRY.get(new ID(itemid));
            return customItem instanceof Hammer;
        } catch (Exception e) {
            return false;
        }
    }
    
    @EventHandler
    protected void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            BLOCK_FACE_MAP.put(e.getPlayer().getUniqueId(), e.getBlockFace());
        }
    }
    
    @EventHandler
    protected void onBlockBreak(BlockBreakEvent e) {
        if (Hammer.isHammer(e.getPlayer().getInventory().getItemInMainHand())) {
            List<Block> blocks = RealmsUtils.handleThreeByThreeTool(e, this.material, BLOCK_FACE_MAP, this.id);
            if (blocks == null) {
                e.setCancelled(true);
                return;
            }
            HammerMineEvent hammerMineEvent = new HammerMineEvent(RealmsAPI.getProfile(e.getPlayer()), e.getBlock().getLocation(), blocks);
            Bukkit.getPluginManager().callEvent(hammerMineEvent);
        }
    }
}