package com.stardevmc.titanterritories.core.chat;

import com.stardevmc.chat.Chatroom;
import com.stardevmc.chat.api.*;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import com.stardevmc.titanterritories.core.objects.member.UserBan;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class TownRoom extends Chatroom {
    
    private static final String BASE_FORMAT = "&8[&dT&8] {changeable} &d{message}";
    private static final String DEFAULT_FORMAT = BASE_FORMAT.replace("{changeable}", "{displayname}&8:");
    
    private Town town;
    
    public TownRoom(Town town) {
        super("town:" + town.getUniqueId().toString(), null, town.getName(), null, "format", Material.ARROW);
        this.town = town;
        this.setFormat(DEFAULT_FORMAT);
        this.hiddenToNonMembers = true;
    }
    
    public void addParticipant(UUID uuid) {
        if (town.getUserController().get(uuid) != null) {
            super.addParticipant(uuid);
        }
    }
    
    public void removeParticipant(UUID uuid) {
        if (town.getUserController().get(uuid) == null) {
            super.removeParticipant(uuid);
        }
    }
    
    public void setOwner(IOwner owner) {
        if (owner instanceof PlayerOwner) {
            if (town.getBaron().getUser().getUniqueId().equals(((PlayerOwner) owner).getOwner())) {
                super.setOwner(owner);
            }
        }
    }
    
    public void setGlobal(boolean global) {
        super.setGlobal(false);
    }
    
    public Set<UUID> getBannedUsers() {
        List<UserBan> banned = town.getUserController().getBanned();
        Set<UUID> roomBanned = new HashSet<>();
        for (UserBan userBan : banned) {
            roomBanned.add(userBan.getUuid());
        }
        this.bannedUsers.addAll(roomBanned);
        return super.getBannedUsers();
    }
    
    public void addMember(UUID uuid, IRole rank) {
        if (town.getUserController().get(uuid) != null) {
            super.addMember(uuid, rank);
        }
    }
    
    public void removeMember(UUID uuid) {
        if (town.getUserController().get(uuid) == null) {
            super.removeMember(uuid);
        }
    }
    
    public boolean hasPermission(Player player) {
        return town.getUserController().get(player) != null;
    }
    
    public void setFormat(String format) {
        super.setFormat(BASE_FORMAT.replace("{changeable}", format));
    }
    
    public Town getTown() {
        return town;
    }
}