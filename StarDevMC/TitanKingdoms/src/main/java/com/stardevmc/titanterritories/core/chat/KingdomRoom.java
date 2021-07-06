package com.stardevmc.titanterritories.core.chat;

import com.stardevmc.chat.Chatroom;
import com.stardevmc.chat.api.*;
import com.stardevmc.titanterritories.core.leader.PlayerMonarch;
import com.stardevmc.titanterritories.core.leader.ServerMonarch;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.member.UserBan;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class KingdomRoom extends Chatroom {
    
    public static final String BASE_FORMAT = "&8[&dK&8] {changeable}&d{message}";
    public static final String DEFAULT_FORMAT = BASE_FORMAT.replace("{changeable}", "{displayname}&8: ");
    
    private Kingdom kingdom;
    
    public KingdomRoom(Kingdom kingdom) {
        super("kingdom:" + kingdom.getUniqueId().toString(), null, "&d" + kingdom.getName(), null, DEFAULT_FORMAT, Material.ARROW);
        if (kingdom.getMonarch() instanceof ServerMonarch) {
            this.owner = new ServerOwner();
        } else {
            PlayerMonarch playerMonarch = ((PlayerMonarch) kingdom.getMonarch());
            this.owner = new PlayerOwner(playerMonarch.getUser().getUniqueId());
        }
        this.kingdom = kingdom;
        this.hiddenToNonMembers = true;
        this.description = kingdom.getDescription();
    }
    
    public void addParticipant(UUID uuid) {
        if (kingdom.getUserController().get(uuid) != null) {
            super.addParticipant(uuid);
        }
    }
    
    public void removeParticipant(UUID uuid) {
        if (kingdom.getUserController().get(uuid) == null) {
            super.removeParticipant(uuid);
        }
    }
    
    public void setOwner(IOwner owner) {
        if (owner instanceof ServerOwner) {
            if (!(this.owner instanceof ServerOwner)) {
                if (kingdom.getMonarch() instanceof ServerMonarch) {
                    super.setOwner(owner);
                    return;
                }
            }
        }
        if (kingdom.getMonarch() instanceof ServerMonarch) {
            this.owner = new ServerOwner();
        } else {
            PlayerMonarch playerMonarch = ((PlayerMonarch) kingdom.getMonarch());
            if (this.owner instanceof PlayerOwner) {
                PlayerOwner playerOwner = ((PlayerOwner) this.owner);
                if (playerOwner.getOwner().equals(playerMonarch.getUser().getUniqueId())) {
                    return;
                }
            }
            
            this.owner = new PlayerOwner(playerMonarch.getUser().getUniqueId());
        }
    }
    
    public void setGlobal(boolean global) {
        super.setGlobal(false);
    }
    
    public Set<UUID> getBannedUsers() {
        List<UserBan> banned = kingdom.getUserController().getBanned();
        Set<UUID> roomBanned = new HashSet<>();
        for (UserBan userBan : banned) {
            roomBanned.add(userBan.getUuid());
        }
        this.bannedUsers.addAll(roomBanned);
        return super.getBannedUsers();
    }
    
    public void addMember(UUID uuid, IRole rank) {
        if (kingdom.getUserController().get(uuid) != null) {
            super.addMember(uuid, rank);
        }
    }
    
    public void removeMember(UUID uuid) {
        if (kingdom.getUserController().get(uuid) == null) {
            super.removeMember(uuid);
        }
    }
    
    public boolean hasPermission(Player player) {
        return kingdom.getUserController().get(player) != null;
    }
    
    public Kingdom getKingdom() {
        return kingdom;
    }
}