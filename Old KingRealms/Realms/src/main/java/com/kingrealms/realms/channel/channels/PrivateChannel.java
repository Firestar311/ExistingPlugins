package com.kingrealms.realms.channel.channels;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.Channel;
import com.kingrealms.realms.channel.Participant;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.moderation.SocialSpyUtils;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.profile.ServerProfile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
public class PrivateChannel extends Channel {
    
    public PrivateChannel(RealmProfile profile1, RealmProfile profile2) {
        super("Private channel between " + profile1.getName() + " and " + profile2.getName(), "Console", System.currentTimeMillis());
        this.addParticipant(profile1.getUniqueId(), Role.MEMBER);
        this.addParticipant(profile2.getUniqueId(), Role.MEMBER);
        setName("Private");
        setPrefix("P");
        setColor(ChatColor.GRAY);
    }
    
    public PrivateChannel(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public void sendMessage(UUID sender, String message) {
        TextComponent ob = new TextComponent(new ComponentBuilder("[").color(ChatColor.DARK_GRAY).bold(true).create());
        TextComponent p = new TextComponent(new ComponentBuilder(this.prefix).color(color).bold(true).create());
        TextComponent cb = new TextComponent(new ComponentBuilder("]").color(ChatColor.DARK_GRAY).bold(true).create());
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(sender);
        TextComponent name = generateNameComponent(profile);
        TextComponent msgComponent = new TextComponent(new ComponentBuilder(message).color(color).create());
        
        TextComponent target = new TextComponent(new ComponentBuilder("<target>").color(color).create());
        
        RealmProfile other = null, senderProfile = null;
        for (Participant participant : participants) {
            if (participant.getUniqueId().equals(sender)) {
                senderProfile = participant.getProfile();
            } else {
                other = participant.getProfile();
            }
        }
        
        if (other == null || senderProfile == null) return;
        for (Participant participant : participants) {
            if (participant.getUniqueId().equals(other.getUniqueId())) {
                target.setText(senderProfile.getName());
            } else {
                target.setText(other.getName());
            }
            
            TextComponent format = new TextComponent(new ComponentBuilder(ob).color(color).append(p).append(":").append(cb).append(" ").append(name).append(" ").append(msgComponent).create());
            participant.sendMessage(format);
        }
    
        SocialSpyUtils.sendSocialSpyMessage(senderProfile.getName() + " -> " + other.getName() + ": " + message);
    }
    
    @Override
    public ServerProfile getOwner() {
        return new ServerProfile();
    }
    
}