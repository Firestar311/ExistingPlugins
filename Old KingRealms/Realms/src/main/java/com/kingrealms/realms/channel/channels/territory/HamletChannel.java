package com.kingrealms.realms.channel.channels.territory;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.*;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.channel.enums.Visibility;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.medievil.Hamlet;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("DuplicatedCode")
@SerializableAs("HamletChannel")
public class HamletChannel extends Channel {
    
    private Hamlet territory; //cache
    
    public HamletChannel(Hamlet hamlet, long created) {
        super(hamlet.getUniqueId(), hamlet.getUniqueId(), created);
        super.setPrefix("H");
        try {
            setColor(ChatColor.getByChar(Utils.getColorCode(Constants.HAMLET_BASE_COLOR)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.territory = hamlet;
        setDescription("Channel for " + hamlet.getName());
        this.visibility = Visibility.PRIVATE;
        setName("Hamlet");
        try {
            addParticipant(hamlet.getLeader().getUniqueId(), Role.OWNER);
        } catch (Exception e) {}
    }
    
    public HamletChannel(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public Hamlet getOwner() {
        if (territory == null) {
            this.territory = (Hamlet) Realms.getInstance().getTerritoryManager().getTerritory(this.owner);
        }
        
        return territory;
    }
    
    @Override
    public void sendMessage(UUID sender, String message) {
        TextComponent header = generateHeaderComponent();
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(sender);
        TextComponent name = generateNameComponent(profile);
        TextComponent msgComponent = new TextComponent(new ComponentBuilder(message).color(color).create());
    
        TextComponent format = new TextComponent(new ComponentBuilder(header).append(" ").append(name)
                .append(" ").append(msgComponent).create());
    
        for (Participant participant : this.participants) {
            if (participant.getProfile().hasPermission(getPermission())) {
                participant.sendMessage(format);
            }
        }
        
        Bukkit.getConsoleSender().sendMessage("[" + getName() + ":" + getOwner().getName() + "] " + profile.getName() + ": " + message);
    }
    
    @Override
    public void setPrefix(String prefix) {
        this.prefix = "H";
    }
    
    @Override
    public String getDisplayName() {
        return getColor() + "Hamlet";
    }
}