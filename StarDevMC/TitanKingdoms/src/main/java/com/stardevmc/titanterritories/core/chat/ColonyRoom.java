package com.stardevmc.titanterritories.core.chat;

import com.stardevmc.chat.Chatroom;
import com.stardevmc.chat.api.*;
import com.stardevmc.titanterritories.core.objects.holder.Colony;
import com.stardevmc.titanterritories.core.objects.member.UserBan;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class ColonyRoom extends Chatroom {
    
    private static final String BASE_FORMAT = "&8[&dC&8] {changeable} &d{message}";
    private static final String DEFAULT_FORMAT = BASE_FORMAT.replace("{changeable}", "{displayname}&8:");
    
    private Colony colony;
    
    public ColonyRoom(Colony colony) {
        super("colony:" + colony.getUniqueId().toString(), new PlayerOwner(colony.getChief().getUser().getUniqueId()), colony.getName(), null, "format", Material.ARROW);
        this.colony = colony;
        this.setFormat(DEFAULT_FORMAT);
        this.hiddenToNonMembers = true;
    }
    
    public void addParticipant(UUID uuid) {
        if (colony.getUserController().get(uuid) != null) {
            super.addParticipant(uuid);
        }
    }
    
    public void removeParticipant(UUID uuid) {
        if (colony.getUserController().get(uuid) == null) {
            super.removeParticipant(uuid);
        }
    }
    
    public void setOwner(IOwner owner) {
        if (owner instanceof PlayerOwner) {
            if (colony.getChief().getUser().getUniqueId().equals(((PlayerOwner) owner).getOwner())) {
                super.setOwner(owner);
            }
        }
    }
    
    public void setGlobal(boolean global) {
        super.setGlobal(false);
    }
    
    public Set<UUID> getBannedUsers() {
        List<UserBan> banned = colony.getUserController().getBanned();
        Set<UUID> roomBanned = new HashSet<>();
        for (UserBan userBan : banned) {
            roomBanned.add(userBan.getUuid());
        }
        this.bannedUsers.addAll(roomBanned);
        return super.getBannedUsers();
    }
    
    public void addMember(UUID uuid, IRole rank) {
        if (colony.getUserController().get(uuid) != null) {
            super.addMember(uuid, rank);
        }
    }
    
    public void removeMember(UUID uuid) {
        if (colony.getUserController().get(uuid) == null) {
            super.removeMember(uuid);
        }
    }
    
    public boolean hasPermission(Player player) {
        return colony.getUserController().get(player) != null;
    }
    
    public void setFormat(String format) {
        super.setFormat(BASE_FORMAT.replace("{changeable}", format));
    }
    
    public Colony getColony() {
        return colony;
    }
}