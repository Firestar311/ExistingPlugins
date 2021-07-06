package com.stardevmc.titanterritories.core.listeners;

import com.firestar311.lib.player.User;
import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.ChatroomCommandEvent;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.*;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.kingdom.Rank;
import com.stardevmc.titanterritories.core.objects.member.Member;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        User info = plugin.getPlayerManager().getUser(e.getPlayer().getUniqueId());
        if (plugin.getMemberManager().getMember(e.getPlayer().getUniqueId()) == null) {
            plugin.getMemberManager().addMember(new Member(info));
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity ed = event.getDamager();
        Entity et = event.getEntity();
        
        if (!(ed instanceof Player && et instanceof Player)) return;
        
        Player damager = ((Player) ed);
        Player target = ((Player) et);
    
        Kingdom locationKingdom = plugin.getKingdomManager().getKingdom(damager.getLocation());
        if (locationKingdom != null) {
            if (!locationKingdom.getFlagController().getFlags().contains(Flag.PVP)) {
                event.setCancelled(true);
                damager.sendMessage(Utils.color("&cYou cannot PVP at this location."));
                return;
            }
        }
        
        Kingdom damagerKingdom = plugin.getKingdomManager().getKingdom(damager.getUniqueId());
        Kingdom targetKingdom = plugin.getKingdomManager().getKingdom(target.getUniqueId());
        
        if (damagerKingdom != null && targetKingdom != null) {
            if (damagerKingdom.equals(targetKingdom)) {
                if (!damagerKingdom.getFlagController().getFlags().contains(Flag.FRIENDLY_FIRE)) {
                    event.setCancelled(true);
                    damager.sendMessage(Utils.color("&cYou cannot damage other kingdom members."));
                }
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Kingdom playerKingdom = plugin.getKingdomManager().getKingdom(player);
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            Block block = e.getClickedBlock();
            BlockState state = block.getState();
            BlockData data = block.getBlockData();
            if (data instanceof Door || state instanceof Container || data instanceof Switch) {
                Kingdom blockKingdom = plugin.getKingdomManager().getKingdom(block.getLocation());
                if (blockKingdom == null) return;
                Rank playerRank = null;
                if (blockKingdom.getUserController().get(player) == null) {
                    Relation relation = plugin.getRelationsManager().getRelation(playerKingdom, blockKingdom);
                    switch (relation) {
                        case ALLY: playerRank = blockKingdom.getRankController().getAllyRank(); break;
                        case NEUTRAL: playerRank = blockKingdom.getRankController().getNeutralRank(); break;
                        case ENEMY: playerRank = blockKingdom.getRankController().getEnemyRank(); break;
                    }
                } else {
                    playerRank = blockKingdom.getUserController().get(player).getRank();
                }
                
                if (data instanceof Door) {
                    if (!playerRank.hasPermission(Permission.DOOR)) {
                        e.setCancelled(true);
                    }
                } else if (state instanceof Container) {
                    if (!playerRank.hasPermission(Permission.CONTAINER)) {
                        e.setCancelled(true);
                    }
                } else if (data instanceof Switch) {
                    if (!playerRank.hasPermission(Permission.REDSTONE)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onChatroomCommand(ChatroomCommandEvent e) {
        Player player = e.getPlayer();
        String argument = e.getRoomId();
        if (argument.equalsIgnoreCase("kingdom") || argument.equalsIgnoreCase("k")) {
            Kingdom kingdom = plugin.getKingdomManager().getKingdom(player);
            if (kingdom != null) {
                e.setRoomId(kingdom.getChatroom().getId());
            }
        }
    }
}