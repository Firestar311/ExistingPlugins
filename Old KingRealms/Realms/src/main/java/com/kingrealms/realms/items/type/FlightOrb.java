package com.kingrealms.realms.items.type;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.flight.FlightResult;
import com.kingrealms.realms.items.*;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.*;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FlightOrb extends CustomItem implements Listener {
    
    public static final long DEFAULT_DURATION = Unit.MINUTES.convertTime(1);
    
    public FlightOrb() {
        super(new ID("orb_of_flight"), "&9&lOrb of Flight", "", Material.MAGMA_CREAM, ItemType.LEGENDARY_ITEM, true);
        CustomItemRegistry.LEGENDARY_ITEMS.addItem(material, this);
        Realms.getInstance().getSeason().addListener(this);
    }
    
    public ItemStack getItemStack(long duration) {
        return getItemStack(duration, 1);
    }
    
    public ItemStack getItemStack(long duration, int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.add("");
        lore.add(Utils.color("&6&lRight Click &fthis item to activate flight in your territory"));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "duration", duration + "");
        } catch (Exception e) {
            return null;
        }
    
        return itemStack;
    }
    
    public static long getDuration(ItemStack itemStack) {
        try {
            return Long.parseLong(NBTWrapper.getNBTString(itemStack, "duration"));
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public ItemStack getItemStack() {
        return getItemStack(DEFAULT_DURATION);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        return getItemStack(DEFAULT_DURATION, amount);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) { 
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
        ItemStack item = e.getItem();
        if (item == null) return;
        if (!matches(item)) return;
        
        long duration = getDuration(item);
        if (duration == 0) {
            e.getPlayer().sendMessage("&cUnable to determine flight duration");
            return;
        }
    
        Territory territory = Realms.getInstance().getTerritoryManager().getTerritory(e.getPlayer());
        if (territory == null) {
            e.getPlayer().sendMessage(Utils.color("&cYou must be a member of a territory in order to activate an orb of flight."));
            return;
        }
        
        if (territory.getFlightInfo().checkExpired() == FlightResult.ACTIVE) {
            e.getPlayer().sendMessage(Utils.color("&cThere already is an orb of flight active."));
            return;
        }
        
        territory.getFlightInfo().activate(RealmsAPI.getProfile(e.getPlayer()), duration);
        for (Member member : territory.getMembers()) {
            member.getRealmProfile().getBukkitPlayer().setAllowFlight(true);
        }
        territory.sendMemberMessage("&sAn orb of flight was activated by &t" + e.getPlayer().getName() + " &sfor &t" + Utils.formatTime(duration));
        ItemStack clone = item.clone();
        clone.setAmount(1);
        e.getPlayer().getInventory().removeItem(clone);
    }
}
