package com.starmediadev.lib.user;

import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

public class UserManager implements Listener {
    
    private final Map<UUID, User> users = new HashMap<>();
    private final SortedSet<ChatMessage> messages = new TreeSet<>();
    private final SortedSet<CmdMessage> commands = new TreeSet<>();
    
    private ServerUser serverUser = null;
    
    private final ConfigManager configManager;
    
    public UserManager(JavaPlugin plugin) {
        this.configManager = new ConfigManager(plugin, "players");
        this.configManager.setup();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!users.containsKey(player.getUniqueId())) {
            this.users.put(player.getUniqueId(), new User(player.getUniqueId()));
        }
    
        User user = this.users.get(player.getUniqueId());
        user.setLastName(player.getName());
        user.addIpAddress(player.getAddress().toString());
        user.addPlaySession(new PlaySession(user.getUniqueId(), System.currentTimeMillis(), player.getLocation()));
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        User info = this.users.get(e.getPlayer().getUniqueId());
        if (info.getCurrentSession() != null) {
            info.getCurrentSession().setLogoutInfo(System.currentTimeMillis(), e.getPlayer());
        }
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        addChatMessage(e.getPlayer(), e.getMessage(), System.currentTimeMillis());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCmdPreprocess(PlayerCommandPreprocessEvent e) {
        addCommand(e.getPlayer(), e.getMessage(), System.currentTimeMillis());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent e) {
        addCommand(e.getSender(), e.getCommand(), System.currentTimeMillis());
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        User user = this.users.get(e.getEntity().getUniqueId());
        user.addDeath(new DeathSnapshot(e.getEntity(), System.currentTimeMillis()));
    }
    
    public User getUser(UUID uuid) {
        if (uuid == null) return null;
        if (serverUser != null) {
            if (serverUser.getUniqueId() != null) {
                if (serverUser.getUniqueId().equals(uuid)) {
                    return serverUser;
                }
            }
        }
        
        if (uuid == null) return null;
        
        if (!this.users.containsKey(uuid)) {
            String name = Utils.getNameFromUUID(uuid);
            if (name == null) return null;
            User user = new User(uuid);
            user.setLastName(name);
            this.users.put(uuid, user);
        }
        return this.users.get(uuid);
    }
    
    public User getUser(String name) {
        if (StringUtils.isEmpty(name)) return null;
        
        try {
            if(getServerUser(name) != null) {
                return serverUser;
            }
        } catch (Exception e) {}
        
        for (User info : this.users.values()) {
            if (info == null) continue;
            if (info.getLastName() == null) continue;
            if (info.getLastName().equalsIgnoreCase(name)) {
                return info;
            }
        }
        
        UUID uuid = Utils.getUUIDFromName(name);
        if (uuid == null) {
            return null;
        }
        User info = new User(uuid);
        info.setLastName(name);
        this.users.put(uuid, info);
        return info;
    }
    
    public void getUserInfoAsync(UUID uuid, Consumer<User> success, Consumer<Exception> fail) {
        if (!this.users.containsKey(uuid)) {
            Utils.Async.getNameFromUUID(uuid, name -> {
                User user = new User(uuid);
                user.setLastName(name);
                this.users.put(uuid, user);
                success.accept(user);
            }, fail);
        }
    }
    
    public void getUserInfoAsync(String name, Consumer<User> success, Consumer<Exception> fail) {
        for (User info : this.users.values()) {
            if (info.getLastName().equalsIgnoreCase(name)) {
                success.accept(info);
                return;
            }
        }
        
        Utils.Async.getUUIDFromName(name, uuid -> {
            User info = new User(uuid);
            info.setLastName(name);
            this.users.put(uuid, info);
            success.accept(info);
        }, fail);
    }
    
    public void savePlayerData() {
        FileConfiguration config = this.configManager.getConfig();
        for (User user : this.users.values()) {
            config.set("users." + user.getUniqueId().toString(), user);
        }
        
        for (ChatMessage msg : this.messages) {
            config.set("messages.chat." + msg.getTime(), msg);
        }
    
        for (CmdMessage msg : this.commands) {
            config.set("messages.cmd." + msg.getTime(), msg);
        }
        
        config.set("server", this.serverUser);
        configManager.saveConfig();
    }
    
    public void loadPlayerData() {
        this.users.clear();
        
        FileConfiguration config = this.configManager.getConfig();
        
        ConfigurationSection userSection = config.getConfigurationSection("users");
        if (userSection != null) {
            for (String u : userSection.getKeys(false)) {
                User user = (User) config.get("users." + u);
                this.users.put(user.getUniqueId(), user);
            }
        }
        
        ConfigurationSection chatMsgsSection = config.getConfigurationSection("messages.chat");
        if (chatMsgsSection != null) {
            for (String t : chatMsgsSection.getKeys(false)) {
                ChatMessage chatMessage = (ChatMessage) chatMsgsSection.get(t);
                this.messages.add(chatMessage);
            }
        }
    
        ConfigurationSection cmdSection = config.getConfigurationSection("messages.cmd");
        if (cmdSection != null) {
            for (String t : cmdSection.getKeys(false)) {
                CmdMessage chatMessage = (CmdMessage) cmdSection.get(t);
                this.commands.add(chatMessage);
            }
        }
    
        if (config.contains("server")) {
            this.serverUser = (ServerUser) config.get("server");
        }
        
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (!users.containsKey(offlinePlayer.getUniqueId())) {
                User info = new User(offlinePlayer.getUniqueId());
                this.users.put(info.getUniqueId(), info);
            }
        }
    }
    
    public ServerUser getServerUser() {
        return serverUser;
    }
    
    public User getServerUser(String infoString) {
        String[] infoSplit = infoString.split(":");
        long date = Long.parseLong(infoSplit[1]);
        if (serverUser.getHistory().getPreviousUUID(date) != null) {
            return serverUser;
        }
        return null;
    }
    
    public void setServerUser(ServerUser serverUser) {
        this.serverUser = serverUser;
    }
    
    public Map<UUID, User> getUsers() {
        return users;
    }
    
    public void addChatMessage(CommandSender sender, String message, long time) {
        String chatSender = null;
        if (sender instanceof Player) {
            chatSender = ((Player) sender).getUniqueId().toString();
        } else if (sender instanceof ConsoleCommandSender) {
            chatSender = "Console";
        }
        
        if (StringUtils.isEmpty(chatSender)) {
            return;
        }
        
        this.messages.add(new ChatMessage(chatSender, message, time));
    }
    
    public void addCommand(CommandSender sender, String message, long time) {
        String cmdSender = null;
        if (sender instanceof Player) {
            cmdSender = ((Player) sender).getUniqueId().toString();
        } else if (sender instanceof ConsoleCommandSender) {
            cmdSender = "Console";
        }
        
        if (StringUtils.isEmpty(cmdSender)) {
            return;
        }
        
        this.commands.add(new CmdMessage(cmdSender, message, time));
    }
    
    public SortedSet<ChatMessage> getMessages() {
        return messages;
    }
    
    public SortedSet<CmdMessage> getCommands() {
        return commands;
    }
    
    //TODO get from command sender
}