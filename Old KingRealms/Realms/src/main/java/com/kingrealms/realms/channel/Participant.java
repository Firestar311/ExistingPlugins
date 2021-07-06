package com.kingrealms.realms.channel;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.profile.RealmProfile;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("Participant")
public class Participant implements ConfigurationSerializable {
    private final UUID uniqueId; //mysql
    private UUID inviter, actor; //mysql
    private RealmProfile profile = null; //cache
    private boolean read = true; //mysql
    private Role role, previousRole; //mysql
    
    public Participant(UUID uniqueId, Role role) {
        this.uniqueId = uniqueId;
        this.role = role;
    }
    
    public Participant(Map<String, Object> serialized) {
        this.uniqueId = UUID.fromString((String) serialized.get("uniqueId"));
        if (serialized.containsKey("inviter")) {
            this.inviter = UUID.fromString((String) serialized.get("inviter"));
        }
        try {
            this.actor = UUID.fromString((String) serialized.get("actor"));
        } catch (Exception e) {}
        this.read = (boolean) serialized.get("read");
        this.role = Role.valueOf((String) serialized.get("role"));
        try {
            previousRole = Role.valueOf((String) serialized.get("previousRole"));
        } catch (Exception e) {}
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("uniqueId", uniqueId.toString());
            if (inviter != null) {
                put("inviter", inviter.toString());
            }
            put("read", read);
            put("role", role.name());
            if (previousRole != null) {
                put("previousRole", previousRole.toString());
            }
            if (actor != null) {
                put("actor", actor.toString());
            }
        }};
    }
    
    public void sendMessage(String message) {
        if (role == Role.MUTED || role == Role.BANNED || role == Role.INVITED) { return; }
        if (canRead()) {
            getProfile().sendMessage(message);
        }
    }
    
    public boolean canRead() {
        return read;
    }
    
    public RealmProfile getProfile() {
        if (profile == null) {
            this.profile = Realms.getInstance().getProfileManager().getProfile(this.uniqueId);
        }
        return profile;
    }
    
    public void mute(UUID actor) {
        if (this.role != Role.MUTED || this.role != Role.BANNED || this.role != Role.INVITED) {
            this.previousRole = role;
            this.role = Role.MUTED;
            this.actor = actor;
        }
    }
    
    public void unmute() {
        if (previousRole == Role.MUTED) {
            this.role = Role.MEMBER;
        } else {
            this.role = previousRole;
            this.actor = null;
            this.previousRole = null;
        }
    }
    
    public void ban(UUID actor) {
        if (this.role != Role.BANNED || this.role != Role.INVITED) {
            this.previousRole = role;
            this.role = Role.MUTED;
            this.actor = actor;
        }
    }
    
    public void unban() {
        if (previousRole == Role.BANNED) {
            this.role = Role.MEMBER;
        } else {
            this.role = previousRole;
            this.actor = null;
            this.previousRole = null;
        }
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public UUID getInviter() {
        return inviter;
    }
    
    public void setInviter(UUID inviter) {
        this.inviter = inviter;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public UUID getActor() {
        return actor;
    }
    
    public void setActor(UUID actor) {
        this.actor = actor;
    }
    
    public Role getPreviousRole() {
        return previousRole;
    }
    
    public void setPreviousRole(Role previousRole) {
        this.previousRole = previousRole;
    }
    
    public void sendMessage(TextComponent text) {
        getProfile().sendMessage(text);
    }
}