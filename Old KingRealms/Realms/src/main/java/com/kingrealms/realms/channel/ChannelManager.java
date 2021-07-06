package com.kingrealms.realms.channel;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.channels.*;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.storage.StorageManager;
import com.kingrealms.realms.territory.medievil.Hamlet;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChannelManager implements Listener {
    
    public static final int GLOBAL_ID = -1, STAFF_ID = -2, BUILD_ID = -3;
    public static final String GLOBAL_SYMBOL = "!", STAFF_SYMBOL = "#", HAMLET_SYMBOL = "@", BUILD_SYMBOL = "%";
    private final IncrementalMap<Channel> channels = new IncrementalMap<>();
    private final Realms plugin = Realms.getInstance();
    private final ConfigManager configManager = StorageManager.channelConfig;
    
    public ChannelManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.configManager.setup();
        
        new BukkitRunnable() {
            public void run() {
                Iterator<Channel> iterator = channels.values().iterator();
                while (iterator.hasNext()) {
                    Channel channel = iterator.next();
                    if (channel instanceof PrivateChannel) {
                        Map<UUID, Boolean> focusValues = new HashMap<>();
                        for (Participant participant : channel.getParticipants()) {
                            Channel focus = participant.getProfile().getChannelFocus();
                            boolean online = participant.getProfile().isOnline();
                            focusValues.put(participant.getUniqueId(), focus.getId() == channel.getId() && online);
                        }
                        
                        int amountFalse = 0;
                        for (Boolean value : focusValues.values()) {
                            if (!value) { amountFalse++; }
                        }
                        
                        if (amountFalse == focusValues.size()) {
                            iterator.remove();
                        }
                    }
                }
                
                Realms.updateChannels();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer());
        String message = e.getMessage();
        
        if (message.startsWith(GLOBAL_SYMBOL)) {
            message = message.replaceFirst(GLOBAL_SYMBOL, "");
            StringBuilder sb = new StringBuilder();
            for (String s : message.split(" ")) {
                sb.append(s).append(" ");
            }
            getGlobalChannel().sendMessage(profile.getUniqueId(), sb.toString());
        } else if (message.startsWith(STAFF_SYMBOL)) {
            message = message.replaceFirst(STAFF_SYMBOL, "");
            if (getStaffChannel().isParticipant(profile.getUniqueId())) {
                StringBuilder sb = new StringBuilder();
                for (String s : message.split(" ")) {
                    sb.append(s).append(" ");
                }
                getStaffChannel().sendMessage(profile.getUniqueId(), sb.toString());
            }
        } else if (message.startsWith(HAMLET_SYMBOL)) {
            message = message.replaceFirst(HAMLET_SYMBOL, "");
            Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(profile);
            Channel channel = hamlet.getChannel();
            if (channel.isParticipant(profile.getUniqueId())) {
                StringBuilder sb = new StringBuilder();
                for (String s : message.split(" ")) {
                    sb.append(s).append(" ");
                }
                channel.sendMessage(profile.getUniqueId(), sb.toString());
            } else {
                profile.sendMessage("&cYou are not a member of a hamlet.");
            }
        } else if (message.startsWith(BUILD_SYMBOL)) {
            message = message.replaceFirst(BUILD_SYMBOL, "");
            StringBuilder sb = new StringBuilder();
            for (String s : message.split(" ")) {
                sb.append(s).append(" ");
            }
            getBuilderChannel().sendMessage(profile.getUniqueId(), sb.toString());
        } else {
            Channel focus = profile.getChannelFocus();
            try {
                focus.sendMessage(profile.getUniqueId(), e.getMessage());
            } catch (Exception ex) {
                profile.setChannelFocus(plugin.getChannelManager().getGlobalChannel());
                focus.sendMessage(profile.getUniqueId(), e.getMessage());
            }
        }
        e.setCancelled(true);
    }
    
    public GlobalChannel getGlobalChannel() {
        if (getChannel(GLOBAL_ID) == null) {
            this.channels.put(GLOBAL_ID, new GlobalChannel(System.currentTimeMillis()));
        }
        
        return (GlobalChannel) getChannel(GLOBAL_ID);
    }
    
    public StaffChannel getStaffChannel() {
        if (getChannel(STAFF_ID) == null) {
            this.channels.put(STAFF_ID, new StaffChannel(System.currentTimeMillis()));
        }
        
        return (StaffChannel) getChannel(STAFF_ID);
    }
    
    public BuilderChannel getBuilderChannel() {
        if (getChannel(BUILD_ID) == null) {
            this.channels.put(BUILD_ID, new BuilderChannel(System.currentTimeMillis()));
        }
        
        return (BuilderChannel) getChannel(STAFF_ID);
    }
    
    public Channel getChannel(int id) {
        return channels.get(id);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        GlobalChannel globalChannel = getGlobalChannel();
        StaffChannel staffChannel = getStaffChannel();
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer());
        if (!globalChannel.isParticipant(profile.getUniqueId())) {
            globalChannel.addParticipant(profile.getUniqueId(), Role.MEMBER);
        }
        
        if (staffChannel.hasPermission(profile)) {
            staffChannel.addParticipant(profile.getUniqueId(), Role.MEMBER);
        }
    }
    
    public void saveData() {
        for (Channel channel : channels.values()) {
            if (!(channel instanceof PrivateChannel)) {
                configManager.getConfig().set("channels." + channel.getId(), channel);
                //plugin.getStorageManager().getDatabase().addRecordToQueue(new ChannelRecord(channel));
            }
        }
        
        configManager.saveConfig();
    }
    
    public void loadData() {
        ConfigurationSection channelsSection = configManager.getConfig().getConfigurationSection("channels");
        if (channelsSection == null) { return; }
        
        for (String c : channelsSection.getKeys(false)) {
            Channel channel = (Channel) channelsSection.get(c);
            this.channels.put(channel.getId(), channel);
        }
        
        if (getChannel(GLOBAL_ID) == null) {
            this.channels.put(GLOBAL_ID, new GlobalChannel(System.currentTimeMillis()));
        }
        
        if (getChannel(STAFF_ID) == null) {
            this.channels.put(STAFF_ID, new StaffChannel(System.currentTimeMillis()));
        }
        
        if (getChannel(BUILD_ID) == null) {
            this.channels.put(BUILD_ID, new BuilderChannel(System.currentTimeMillis()));
        }
    }
    
    public void registerChannel(Channel channel) {
        int pos = this.channels.add(channel);
        channel.setId(pos);
    }
    
    public Channel getChannel(String name) {
        for (Channel channel : this.channels.values()) {
            if (channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }
        
        return null;
    }
    
    public PrivateChannel getPrivateChannel(RealmProfile participant1, RealmProfile participant2) {
        for (Channel channel : this.channels.values()) {
            if (channel instanceof PrivateChannel) {
                if (channel.isParticipant(participant1) && channel.isParticipant(participant2)) {
                    return (PrivateChannel) channel;
                }
            }
        }
        
        return null;
    }
    
    public Set<Channel> getChannels() {
        return new HashSet<>(this.channels.values());
    }
    
    public void removeChannel(Channel channel) {
        this.channels.remove(channel.getId());
    }
}