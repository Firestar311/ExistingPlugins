package com.kingrealms.realms.channel.channels;

import com.kingrealms.realms.channel.Channel;
import com.kingrealms.realms.channel.ChannelManager;
import com.kingrealms.realms.profile.ServerProfile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;

@SerializableAs("StaffChannel")
public class StaffChannel extends Channel {
    public StaffChannel(long created) {
        super("Staff", "Console", created);
        setId(ChannelManager.STAFF_ID);
        setPrefix("S");
        setColor(ChatColor.RED);
        setPermission("realms.channels.staff");
        setSymbol(ChannelManager.STAFF_SYMBOL);
    }
    
    public StaffChannel(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public ServerProfile getOwner() {
        return new ServerProfile();
    }
    
    @Override
    public void setPrefix(String prefix) {
        this.prefix = "S";
    }
    
    @Override
    public String getPermission() {
        return "realms.channels.staff";
    }
    
    @Override
    public void setPermission(String permission) {
        super.setPermission("realms.channels.staff");
    }
    
    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);
    }
}