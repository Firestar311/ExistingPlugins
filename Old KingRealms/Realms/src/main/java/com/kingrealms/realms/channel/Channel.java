package com.kingrealms.realms.channel;

import com.kingrealms.realms.IOwner;
import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.channel.enums.Visibility;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Pair;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SuppressWarnings("DuplicatedCode")
@SerializableAs("Channel")
public abstract class Channel implements ConfigurationSerializable {
    protected boolean autoJoin, allowInvites; //mysql
    protected ChatColor color;
    protected long created; //mysql
    protected int id; //mysql
    protected String name, owner, description, prefix, permission, symbol; //mysql
    protected Set<Participant> participants = new HashSet<>();
    protected Visibility visibility = Visibility.OPEN; //mysql
    
    public Channel(String name, String owner, long created) {
        this.name = name;
        this.owner = owner;
        this.created = created;
        this.color = ChatColor.WHITE;
    }
    
    public Channel(Map<String, Object> serialized) {
        this.id = Integer.parseInt((String) serialized.get("id"));
        this.name = (String) serialized.get("name");
        this.owner = (String) serialized.get("owner");
        this.color = ChatColor.getByChar(((String) serialized.get("color")).charAt(0));
        this.description = (String) serialized.get("description");
        this.created = Long.parseLong((String) serialized.get("created"));
        this.autoJoin = (boolean) serialized.get("autoJoin");
        this.allowInvites = (boolean) serialized.get("allowInvites");
        this.visibility = Visibility.valueOf((String) serialized.get("visibility"));
        this.prefix = (String) serialized.get("prefix");
        this.permission = (String) serialized.get("permission");
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id + "");
            put("name", name);
            put("owner", owner);
            put("color", color.toString().replace("§", ""));
            put("description", description);
            put("created", created + "");
            put("autoJoin", autoJoin);
            put("visibility", visibility.name());
            put("allowInvites", allowInvites);
            put("prefix", prefix);
            put("permission", permission);
            for (Participant participant : participants) {
                put("participant-" + participant.getUniqueId().toString(), participant);
            }
        }};
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public long getCreated() {
        return created;
    }
    
    public Set<Participant> getParticipants() {
        return new HashSet<>(participants);
    }
    
    public boolean isAutoJoin() {
        return autoJoin;
    }
    
    public void setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
    }
    
    public boolean getAllowInvites() {
        return allowInvites;
    }
    
    public void setAllowInvites(boolean allowInvites) {
        this.allowInvites = allowInvites;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    public abstract <T extends IOwner> T getOwner();
    
    public String getOwnerRaw() {
        return this.owner;
    }
    
    public boolean canView(UUID uuid) {
        if (this.visibility == Visibility.OPEN) {
            return true;
        } else if (this.visibility == Visibility.PRIVATE) {
            Participant participant = getParticipant(uuid);
            return participant != null;
        }
        return false;
    }
    
    public Participant getParticipant(UUID uuid) {
        for (Participant participant : this.participants) {
            if (participant.getUniqueId().equals(uuid)) {
                return participant;
            }
        }
        
        return null;
    }
    
    protected TextComponent generateHeaderComponent() {
        TextComponent ob = new TextComponent(new ComponentBuilder("[").color(ChatColor.DARK_GRAY).bold(true).create());
        TextComponent p = new TextComponent(new ComponentBuilder(this.prefix).color(color).bold(true).create());
        TextComponent cb = new TextComponent(new ComponentBuilder("]").color(ChatColor.DARK_GRAY).bold(true).create());
        return new TextComponent(new ComponentBuilder("").append(ob).append(p).append(cb).create());
    }
    
    protected TextComponent generateNameComponent(RealmProfile profile) {
        TextComponent senderName = new TextComponent(new ComponentBuilder(profile.getDisplayName()).create());
        TextComponent separator = new TextComponent(new ComponentBuilder(":").color(ChatColor.DARK_GRAY).create());
        return new TextComponent(new ComponentBuilder(senderName).append(separator).create());
    }
    
    public void sendMessage(UUID sender, String message) {
        TextComponent header = generateHeaderComponent();
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(sender);
        TextComponent name = generateNameComponent(profile);
        TextComponent msgComponent = new TextComponent(new ComponentBuilder(message).color(color).create());
        TextComponent format = new TextComponent(new ComponentBuilder(header).append(" ").append(name).append(" ").append(msgComponent).create());
        
        for (Participant participant : this.participants) {
            if (participant.getProfile().hasPermission(getPermission())) {
                participant.sendMessage(format);
            }
        }
        
        logToConsole(getName(), profile.getName(), message);
    }
    
    public void logToConsole(String channelName, String senderName, String message) {
        Bukkit.getConsoleSender().sendMessage("[" + channelName + "] " + senderName + ": " + message);
    }
    
    public String getPermission() {
        return permission;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public void sendMessage(String message) {
        TextComponent header = generateHeaderComponent();
        TextComponent msgComponent = new TextComponent(new ComponentBuilder(message).color(color).create());
        
        TextComponent format = new TextComponent(new ComponentBuilder(header).append(" ").append(msgComponent).create());
        
        for (Participant participant : getParticipants()) {
            if (participant.getProfile().hasPermission(getPermission())) {
                participant.sendMessage(format);
            }
        }
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public Participant addParticipant(UUID uuid, Role role) {
        Participant participant = getParticipant(uuid);
        if (participant == null) {
            participant = new Participant(uuid, role);
            this.participants.add(participant);
        }
        return participant;
    }
    
    public boolean mute(UUID member, UUID muter) {
        Participant participant = getParticipant(member);
        Participant actor = getParticipant(muter);
        if (actor == null) {
            return false;
        }
        
        if (participant == null) { return false; }
        if (actor.getRole() == Role.SERVER_STAFF) {
            participant.mute(actor.getUniqueId());
        } else {
            if (participant.getRole().getOrder() <= Role.MANAGER.getOrder()) {
                if (actor.getRole().getOrder() < participant.getRole().getOrder()) {
                    participant.mute(actor.getUniqueId());
                }
            }
        }
        
        return true;
    }
    
    public boolean ban(UUID member, UUID banner) {
        Pair<Participant, Participant> actionParticipants = getModerationParticipants(member, banner);
        if (actionParticipants == null) { return false; }
        Participant participant = actionParticipants.getValue1();
        Participant actor = actionParticipants.getValue2();
        
        if (actor.getRole() == Role.SERVER_STAFF) {
            participant.ban(actor.getUniqueId());
        } else {
            if (participant.getRole().getOrder() <= Role.MANAGER.getOrder()) {
                if (actor.getRole().getOrder() < participant.getRole().getOrder()) {
                    participant.ban(actor.getUniqueId());
                }
            }
        }
        
        return true;
    }
    
    private Pair<Participant, Participant> getModerationParticipants(UUID member, UUID actor) {
        Participant participant = getParticipant(member);
        Participant actorParticipant = getParticipant(actor);
        if (actor == null) {
            return null;
        }
        
        if (participant == null) { return null; }
        return new Pair<>(participant, actorParticipant);
    }
    
    public String getDisplayName() {
        return this.getColor() + this.getName();
    }
    
    public ChatColor getColor() {
        return color;
    }
    
    public void setColor(ChatColor color) {
        this.color = color;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public boolean isParticipant(RealmProfile profile) {
        return isParticipant(profile.getUniqueId());
    }
    
    public boolean isParticipant(UUID uuid) {
        for (Iterator<Participant> iterator = this.participants.iterator(); iterator.hasNext(); ) {
            Participant participant = iterator.next();
            if (!this.hasPermission(participant.getProfile())) {
                iterator.remove();
                continue;
            }
            if (participant.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean hasPermission(RealmProfile profile) {
        if (StringUtils.isEmpty(this.permission)) {
            return true;
        } else {
            return profile.hasPermission(this.permission);
        }
    }
    
    public void removeParticipant(UUID uniqueId) {
        this.participants.removeIf(participant -> participant.getUniqueId().equals(uniqueId));
    }
}