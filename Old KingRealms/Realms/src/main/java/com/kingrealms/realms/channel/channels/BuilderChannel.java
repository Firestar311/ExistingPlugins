package com.kingrealms.realms.channel.channels;

import com.kingrealms.realms.channel.Channel;
import com.kingrealms.realms.channel.ChannelManager;
import com.kingrealms.realms.profile.ServerProfile;
import net.md_5.bungee.api.ChatColor;

import java.util.Map;

public class BuilderChannel extends Channel {
    public BuilderChannel(long created) {
        super("Builders", "Console", created);
        setId(ChannelManager.BUILD_ID);
        setPrefix("B");
        setColor(ChatColor.AQUA);
        setPermission("realms.channels.builder");
        setSymbol(ChannelManager.BUILD_SYMBOL);
    }
    
    public BuilderChannel(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public ServerProfile getOwner() {
        return new ServerProfile();
    }
    
    @Override
    public void setPrefix(String prefix) {
        this.prefix = "B";
    }
}