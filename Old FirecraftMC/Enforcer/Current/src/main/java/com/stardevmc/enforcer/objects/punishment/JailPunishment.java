package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.prison.Prison;
import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.Target;
import com.stardevmc.enforcer.util.Messages;
import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class JailPunishment extends Punishment {
    
    private String jailedInventory;
    private int prisonId;
    private boolean unjailedWhileOffline, notifiedOfOfflineJail, notifiedOfOfflineUnjail;
    
    public JailPunishment(String server, Actor punisher, Target target, String reason, long date, int prisonId) {
        super(Type.JAIL, server, punisher, target, reason, date);
        this.prisonId = prisonId;
    }
    
    public JailPunishment(String server, Actor punisher, Target target, String reason, long date, Visibility visibility, int prisonId) {
        super(Type.JAIL, server, punisher, target, reason, date, visibility);
        this.prisonId = prisonId;
    }
    
    public JailPunishment(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility, int prisonId) {
        super(id, Type.JAIL, server, punisher, target, reason, date, active, visibility);
        this.prisonId = prisonId;
    }
    
    public void executePunishment(ExecuteOptions... options) {
        Player player = target.getPlayer();
        Prison prison = Enforcer.getInstance().getPrisonModule().getManager().getPrison(this.prisonId);
        prison.addInmate(player);
        if (player != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Enforcer.getInstance(), () -> player.teleport(prison.getLocation()));
            player.sendMessage(Utils.color(Messages.targetJail(getPunisherName(), getReason())));
            this.jailedInventory = InventoryStore.itemsToString(player.getInventory().getContents());
            player.getInventory().clear();
        } else {
            setOffline(true);
        }
        
        sendPunishMessage(options);
    }
    
    public void reversePunishment(Actor remover, long removedDate, String removedReason) {
        setRemover(remover);
        setRemovedDate(removedDate);
        setActive(false);
        setRemovedReason(removedReason);
        sendRemovalMessage();
        
        Player target = this.target.getPlayer();
        Prison prison = Enforcer.getInstance().getPrisonModule().getManager().getPrison(this.prisonId);
        if (prison != null) {
            prison.removeInmate(target.getUniqueId());
        }
        
        if (target != null) {
            target.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            target.sendMessage(Utils.color(Messages.playerUnjailed(this.getRemoverName())));
            try {
                ItemStack[] items = InventoryStore.stringToItems(this.jailedInventory);
                target.getInventory().setContents(items);
                target.sendMessage(Utils.color("&7&oYour inventory items have been restored."));
            } catch (Exception e) {
                target.sendMessage(Utils.color("&cThere was a problem restoring your inventory. Please contact the plugin developer"));
            }
        } else {
            this.unjailedWhileOffline = true;
            this.notifiedOfOfflineJail = false;
        }
    }
    
    public boolean wasNotifiedOfOfflineUnjail() {
        return notifiedOfOfflineUnjail;
    }
    
    public boolean wasUnjailedWhileOffline() {
        return unjailedWhileOffline;
    }
    
    public boolean wasNotifiedOfOfflineJail() {
        return notifiedOfOfflineJail;
    }
    
    public int getPrisonId() {
        return prisonId;
    }
    
    public void setPrisonId(int id) {
        this.prisonId = id;
    }
    
    public String getJailedInventory() {
        return jailedInventory;
    }
    
    public void setJailedInventory(String jailedInventory) {
        this.jailedInventory = jailedInventory;
    }
    
    public void setNotifiedOfOfflineUnjail(boolean notifiedOfOfflineUnjail) {
        this.notifiedOfOfflineUnjail = notifiedOfOfflineUnjail;
    }
    
    public void setNotifiedOfOfflineJail(boolean notifiedOfOfflineJail) {
        this.notifiedOfOfflineJail = notifiedOfOfflineJail;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serializeBase();
        serialized.put("jailedInventory", this.jailedInventory);
        serialized.put("prisonId", this.prisonId + "");
        serialized.put("unjailedWhileOffline", unjailedWhileOffline);
        serialized.put("notifiedOfOfflineJail", notifiedOfOfflineJail);
        serialized.put("notifiedOfOfflineUnjail", notifiedOfOfflineUnjail);
        return serialized;
    }
    
    public static JailPunishment deserialize(Map<String, Object> serialized) {
        String jailedInventory = (String) serialized.get("jailedInventory");
        int prisonId = Integer.parseInt((String) serialized.get("prisonId"));
        boolean unjailedWhileOffline = (boolean) serialized.get("unjailedWhileOffline");
        boolean notifiedOfOfflineJail = (boolean) serialized.get("notifiedOfOfflineJail");
        boolean notifiedOfOfflineUnjail = (boolean) serialized.get("notifiedOfOfflineUnjail");
        JailPunishment jailPunishment = (JailPunishment) deserializeBase(serialized).build();
        jailPunishment.notifiedOfOfflineJail = notifiedOfOfflineJail;
        jailPunishment.notifiedOfOfflineUnjail = notifiedOfOfflineUnjail;
        jailPunishment.jailedInventory = jailedInventory;
        jailPunishment.unjailedWhileOffline = unjailedWhileOffline;
        jailPunishment.prisonId = prisonId;
        return jailPunishment;
    }
}
