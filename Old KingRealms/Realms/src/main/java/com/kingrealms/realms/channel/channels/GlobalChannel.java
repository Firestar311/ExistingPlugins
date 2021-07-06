package com.kingrealms.realms.channel.channels;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.*;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.profile.ServerProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SerializableAs("GlobalChannel")
public class GlobalChannel extends Channel {
    
    private Map<UUID, Long> lastMessage = new HashMap<>();
    
    private static final Map<String, Long> RANK_COOLDOWNS = new HashMap<>() {{
        put("default", TimeUnit.SECONDS.toMillis(5));
        put("soldier", TimeUnit.SECONDS.toMillis(4));
        put("knight", TimeUnit.SECONDS.toMillis(3));
        put("marshall", TimeUnit.SECONDS.toMillis(2));
        put("god", TimeUnit.SECONDS.toMillis(1));
    }};
    
    public GlobalChannel(long created) {
        super("Global", "Console", created);
        setId(ChannelManager.GLOBAL_ID);
        setPrefix("G");
        setSymbol(ChannelManager.GLOBAL_SYMBOL);
    }
    
    public GlobalChannel(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public void sendMessage(UUID sender, String message) {
        if (lastMessage.containsKey(sender)) {
            RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(sender);
            String permissionGroup = profile.getPermissionGroup().toLowerCase();
            if (RANK_COOLDOWNS.containsKey(permissionGroup)) {
                long cooldown = RANK_COOLDOWNS.get(permissionGroup);
                long nextMessage = lastMessage.get(sender) + cooldown;
                if (System.currentTimeMillis() < nextMessage) {
                    profile.sendMessage("&cYou must wait " + Utils.formatTime(nextMessage - System.currentTimeMillis()) + " before you can talk again.");
                    return;
                }
            }
        }
        super.sendMessage(sender, message);
        this.lastMessage.put(sender, System.currentTimeMillis());
    }
    
    public Set<Participant> getParticipants() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getParticipant(player.getUniqueId()) == null) {
                addParticipant(player.getUniqueId(), Role.MEMBER);
            }
        }
        return super.getParticipants();
    }
    
    @Override
    public ServerProfile getOwner() {
        return new ServerProfile();
    }
    
    @Override
    public void setPrefix(String prefix) {
        this.prefix = "G";
    }
}