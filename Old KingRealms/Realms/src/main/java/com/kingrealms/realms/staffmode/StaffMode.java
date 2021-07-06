package com.kingrealms.realms.staffmode;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.InventoryStore;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("StaffMode")
public class StaffMode implements ConfigurationSerializable {
    
    private long date; //mysql
    private String playInventory, staffInventory; //mysql
    private GameMode playGameMode = GameMode.SURVIVAL, staffGameMode = GameMode.SPECTATOR; //mysql
    private boolean active, itemPickup = false, autojoinStaffChat = true, oldFly, oldCollidable; //mysql
    private UUID followTarget; //mysql
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("date", date + "");
            put("playInventory", playInventory);
            put("staffInventory", staffInventory);
            put("playGameMode", playGameMode.name());
            put("staffGameMode", staffGameMode.name());
            put("active", active);
            put("itemPickup", itemPickup);
            put("autojoinStaffChat", autojoinStaffChat);
            put("oldFly", oldFly);
            put("oldCollidable", oldCollidable);
            if (followTarget != null) {
                put("followTarget", followTarget.toString());
            }
        }};
    }
    
    public StaffMode(Map<String, Object> serialized) {
        this.date = Long.parseLong((String) serialized.get("date"));
        this.playInventory = (String) serialized.get("playInventory");
        this.staffInventory = (String) serialized.get("staffInventory");
        this.playGameMode = GameMode.valueOf((String) serialized.get("playGameMode"));
        this.staffGameMode = GameMode.valueOf((String) serialized.get("staffGameMode"));
        this.active = (boolean) serialized.get("active");
        this.autojoinStaffChat = (boolean) serialized.get("autojoinStaffChat");
        this.oldFly = (boolean) serialized.get("oldFly");
        this.oldCollidable = (boolean) serialized.get("oldCollidable");
        if (serialized.containsKey("followTarget")) {
            this.followTarget = UUID.fromString((String) serialized.get("followTarget"));
        }
    }
    
    public StaffMode() {}
    
    public boolean toggleStaffMode(Player player) {
        if (isActive()) {
            setActive(false);
            setStaffInventory(player.getInventory().getContents());
            this.staffGameMode = player.getGameMode();
            player.getInventory().clear();
            player.getInventory().setContents(getPlayInventory());
            if (getPlayGameMode() == GameMode.SURVIVAL || getPlayGameMode() == GameMode.ADVENTURE) {
                player.setGameMode(getPlayGameMode());
                player.setAllowFlight(oldFly);
            }
            player.setCollidable(oldCollidable);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showPlayer(Realms.getInstance(), player);
            }
            player.setGameMode(playGameMode);
        } else {
            setActive(true);
            setPlayInventory(player.getInventory().getContents());
            player.getInventory().clear();
            player.getInventory().setContents(getStaffInventory());
            this.playGameMode = player.getGameMode();
            this.oldFly = player.getAllowFlight();
            this.oldCollidable = player.isCollidable();
            player.setGameMode(staffGameMode);
            if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
                player.setAllowFlight(true);
            }
            player.setCollidable(false);
            if (autojoinStaffChat) {
                RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(player);
                profile.setChannelFocus(Realms.getInstance().getChannelManager().getStaffChannel());
            }
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.hasPermission("realms.staff")) {
                    p.hidePlayer(Realms.getInstance(), player);
                }
            }
        }
        
        return isActive();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setActive(boolean value) {
        this.active = value;
    }
    
    public GameMode getPlayGameMode() {
        return (playGameMode != null) ? playGameMode : GameMode.SURVIVAL;
    }
    
    public void setPlayGameMode(GameMode playGameMode) {
        this.playGameMode = playGameMode;
    }
    
    public void setItemPickup(boolean itemPickup) {
        this.itemPickup = itemPickup;
    }
    
    public boolean canPickupItems() {
        return itemPickup;
    }
    
    public boolean autoJoinStaffChat() {
        return autojoinStaffChat;
    }
    
    public void setAutojoinStaffChat(boolean value) {
        this.autojoinStaffChat = value;
    }
    
    public void setPlayInventory(ItemStack[] itemStacks) {
        this.playInventory = InventoryStore.itemsToString(itemStacks);
    }
    
    public void setStaffInventory(ItemStack[] itemStacks) {
        this.staffInventory = InventoryStore.itemsToString(itemStacks);
    }
    
    public ItemStack[] getPlayInventory() {
        if (StringUtils.isEmpty(this.playInventory)) {
            return new ItemStack[0];
        }
        
        return InventoryStore.stringToItems(this.playInventory);
    }
    
    public ItemStack[] getStaffInventory() {
        if (StringUtils.isEmpty(this.staffInventory)) {
            return new ItemStack[0];
        }
        
        return InventoryStore.stringToItems(this.staffInventory);
    }
    
    public void setFollowTarget(UUID followTarget) {
        this.followTarget = followTarget;
    }
    
    public UUID getFollowTarget() {
        return followTarget;
    }
}