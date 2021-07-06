package com.stardevmc.chat.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public interface IChatroom {
    
    //TODO: Add invite tracking similar to how TitanKingdoms does it
    boolean isAutoJoin();
    void setAutoJoin(boolean value);
    void recalculateMembers();
    boolean isHiddenToNonMembers();
    void setHiddenToNonMembers(boolean value);
    boolean isMember(Player player);
    String getId();
    String getDisplayName();
    String getPermission();
    String getFormat();
    ItemStack getIcon();
    Set<UUID> getParticipants();
    void addParticipant(UUID uuid);
    void removeParticipant(UUID uuid);
    String getDescription();
    IRole getRole(Player player);
    void setDescription(String description);
    IOwner getOwner();
    void setOwner(IOwner uuid);
    Set<UUID> getInvited();
    void addInvited(UUID uuid);
    void removeInvited(UUID uuid);
    boolean isGlobal();
    void setGlobal(boolean global);
    Set<UUID> getBannedUsers();
    void addBannedUser(UUID uuid);
    Map<UUID, IRole> getMembers();
    void addMember(UUID uuid, IRole rank);
    void removeMember(UUID uuid);
    void setSilenced(UUID member, boolean value);
    boolean hasRoomSilenced(UUID member);
    String formatMessage(Player player, String message);
    void sendChatMessage(String format);
    boolean isParticipating(Player player);
    boolean hasPermission(Player player);
    void setDisplayName(String value);
    void setFormat(String value);
    void setPermission(String value);
    void setIcon(Material iconMaterial);
    List<String> getAliases();
    void addAlias(String alias);
    void removeAlias(String alias);
}