package com.stardevmc.chat;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Chatroom implements IChatroom, ConfigurationSerializable {
    
    protected String id, displayName, permission, format, description; //Some chatrooom information
    protected Material iconMaterial; //Icon in the soon to come gui
    protected Set<UUID> participants; //members who are actively participating in the chat, this is when they have set it via the command
    protected IOwner owner; //The owner of the room, player or server
    protected boolean global = true; //Controls whether anyone can join this chatroom
    protected Set<UUID> bannedUsers; //All people who are banned from this chatroom
    protected Set<IMember> members; //All members and their roles
    protected Set<UUID> invited; //All those who have been invited to this chatroom
    protected Set<UUID> silenced; //All those who have this room silenced
    protected boolean autoJoin, hiddenToNonMembers;
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id);
        serialized.put("displayName", this.displayName);
        serialized.put("permission", this.permission);
        serialized.put("format", this.format);
        serialized.put("description", this.description);
        serialized.put("iconMaterial", this.iconMaterial.toString());
        serialized.put("owner", this.owner.toString());
        serialized.put("participants", convertToStringList(participants));
        serialized.put("bannedUsers", convertToStringList(bannedUsers));
        serialized.put("invited", convertToStringList(invited));
        serialized.put("silenced", convertToStringList(silenced));
        serialized.put("autoJoin", this.autoJoin);
        serialized.put("hiddenToNonMembers", this.hiddenToNonMembers);
        serialized.put("global", this.global);
        serialized.put("memberCount", this.members.size() + "");
        int counter = 0;
        for (IMember member : this.members) {
            serialized.put("member" + counter, member);
            counter++;
        }
        serialized.put("global", this.global);
        return serialized;
    }
    
    public static Chatroom deserialize(Map<String, Object> serialized) {
        String id = (String) serialized.get("id");
        String displayName = (String) serialized.get("displayName");
        String permission = (String) serialized.get("permission");
        String format = (String) serialized.get("format");
        String description = (String) serialized.get("description");
        Material icon = Material.valueOf((String) serialized.get("iconMaterial"));
        boolean autoJoin = (boolean) serialized.get("autoJoin");
        boolean hiddenToNonMembers = (boolean) serialized.get("hiddenToNonMembers");
        boolean global = (boolean) serialized.get("global");
        String rawOwner = (String) serialized.get("owner");
        IOwner owner;
        if (rawOwner.equalsIgnoreCase("server")) {
            owner = new ServerOwner();
        } else {
            owner = new PlayerOwner(UUID.fromString(rawOwner));
        }
        Set<UUID> participants = convertFromStringList((List<String>) serialized.get("participants"));
        Set<UUID> banned = convertFromStringList((List<String>) serialized.get("bannedUsers"));
        Set<UUID> invited = convertFromStringList((List<String>) serialized.get("invited"));
        Set<UUID> silenced = convertFromStringList((List<String>) serialized.get("silenced"));
        Set<IMember> members = new HashSet<>();
        int memberCount = Integer.parseInt((String) serialized.get("memberCount"));
        for (int i = 0; i < memberCount; i++) {
            members.add((Member) serialized.get("member" + i));
        }
        Chatroom chatroom = new Chatroom(id, owner, displayName, permission, format, icon, participants);
        chatroom.bannedUsers = banned;
        chatroom.invited = invited;
        chatroom.silenced = silenced;
        chatroom.members = members;
        chatroom.description = description;
        chatroom.autoJoin = autoJoin;
        chatroom.hiddenToNonMembers = hiddenToNonMembers;
        chatroom.global = global;
        return chatroom;
    }
    
    private static Set<UUID> convertFromStringList(List<String> stringList) {
        Set<UUID> uuidSet = new HashSet<>();
        stringList.forEach(string -> uuidSet.add(UUID.fromString(string)));
        return uuidSet;
    }
    
    private List<String> convertToStringList(Collection<UUID> uuidCollection) {
        List<String> uuidStrings = new ArrayList<>();
        uuidCollection.forEach(uuid -> uuidStrings.add(uuid.toString()));
        return uuidStrings;
    }
    
    public Chatroom(String id, IOwner owner, String displayName, String permission, String format, Material iconMaterial) {
        this(id, owner, displayName, permission, format, iconMaterial, new HashSet<>());
    }
    
    public Chatroom(String id, IOwner owner, String displayName, String permission, String format, Material iconMaterial, Set<UUID> participants) {
        this.id = id;
        this.owner = owner;
        this.displayName = displayName;
        this.permission = permission;
        this.format = format;
        this.iconMaterial = iconMaterial;
        this.participants = participants;
        this.bannedUsers = new HashSet<>();
        this.members = new HashSet<>();
        this.invited = new HashSet<>();
        this.silenced = new HashSet<>();
        this.iconMaterial = iconMaterial;
    }
    
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public String getFormat() {
        return format;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chatroom channel = (Chatroom) o;
        return Objects.equals(id, channel.id);
    }
    
    public int hashCode() {
        return Objects.hash(id);
    }
    
    public ItemStack getIcon() {
        return ItemBuilder.start(this.iconMaterial).withName(this.displayName).withLore(this.description).buildItem();
    }
    
    public Set<UUID> getParticipants() {
        return participants;
    }
    
    public void addParticipant(UUID uuid) {
        this.participants.add(uuid);
    }
    
    public void removeParticipant(UUID uuid) {
        this.participants.remove(uuid);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public IRole getRole(Player player) {
        for (IMember member : this.members) {
            if (member.getUniqueId().equals(player.getUniqueId())) {
                return member.getRole();
            }
        }
        return null;
    }
    
    public void setDescription(String description) {
        this.description = Utils.color(description);
    }
    
    public IOwner getOwner() {
        return this.owner;
    }
    
    public void setOwner(IOwner uuid) {
        this.owner = uuid;
    }
    
    public Set<UUID> getInvited() {
        return this.invited;
    }
    
    public void addInvited(UUID uuid) {
        this.invited.add(uuid);
    }
    
    public void removeInvited(UUID uuid) {
        this.invited.remove(uuid);
    }
    
    public boolean isGlobal() {
        return this.global;
    }
    
    public void setGlobal(boolean global) {
        this.global = global;
    }
    
    public Set<UUID> getBannedUsers() {
        return this.bannedUsers;
    }
    
    public void addBannedUser(UUID uuid) {
        this.bannedUsers.add(uuid);
    }
    
    public Map<UUID, IRole> getMembers() {
        Map<UUID, IRole> members = new HashMap<>();
        this.members.forEach(member -> members.put(member.getUniqueId(), member.getRole()));
        return members;
    }
    
    public void addMember(UUID uuid, IRole rank) {
        this.members.add(new Member(uuid, rank));
        this.participants.add(uuid);
    }
    
    public void removeMember(UUID uuid) {
        IMember member = getMember(uuid);
        this.members.remove(member);
        this.participants.remove(uuid);
    }
    
    public IMember getMember(UUID uuid) {
        for (IMember member : this.members) {
            if (member.getUniqueId().equals(uuid)) {
                return member;
            }
        }
        return null;
    }
    
    public void removeInvite(UUID uuid) {
        this.invited.remove(uuid);
    }
    
    public void setSilenced(UUID member, boolean value) {
        if (value) {
            this.silenced.add(member);
        } else {
            this.silenced.remove(member);
        }
    }
    
    public boolean hasRoomSilenced(UUID member) {
        return this.silenced.contains(member);
    }
    
    public String formatMessage(Player player, String message) {
        return Utils.color(format.replace("{displayname}", player.getDisplayName()).replace("{message}", message));
    }
    
    public void sendChatMessage(String format) {
        for (UUID uuid : this.participants) {
            Player participant = Bukkit.getPlayer(uuid);
            if (participant != null) {
                if (!this.hasRoomSilenced(uuid)) {
                    participant.sendMessage(Utils.color(format));
                }
            }
        }
    }
    
    public boolean isParticipating(Player player) {
        return this.participants.contains(player.getUniqueId());
    }
    
    public boolean hasPermission(Player player) {
        if (player.hasPermission("titanchat.admin.seehiddenchatrooms")) {
            return true;
        }
        
        if (this.permission != null) {
            return player.hasPermission(permission);
        }
        return true;
    }
    
    public boolean isAutoJoin() {
        return autoJoin;
    }
    
    public void setAutoJoin(boolean value) {
        this.autoJoin = value;
    }
    
    public void recalculateMembers() {
        //This needs to be reworked
    }
    
    public boolean isHiddenToNonMembers() {
        return hiddenToNonMembers;
    }
    
    public void setHiddenToNonMembers(boolean value) {
        this.hiddenToNonMembers = value;
    }
    
    public boolean isMember(Player player) {
        IMember member = getMember(player.getUniqueId());
        return member != null;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public void setIcon(Material icon) {
        this.iconMaterial = icon;
    }
    
    public List<String> getAliases() {
        return null;
    }
    
    public void addAlias(String alias) {
    
    }
    
    public void removeAlias(String alias) {
    
    }
}