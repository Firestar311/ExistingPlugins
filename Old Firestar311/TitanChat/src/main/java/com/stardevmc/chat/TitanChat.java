package com.stardevmc.chat;

import com.stardevmc.chat.api.IChatroom;
import com.stardevmc.chat.api.IChatroomManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TitanChat extends JavaPlugin {
    
    private static TitanChat INSTANCE;
    private IChatroomManager chatRoomManager;
    
    private Map<UUID, IChatroom> editmode = new HashMap<>();
    private Map<UUID, RoomBuilder> roomBuilders = new HashMap<>();
    
    private Chat chat = null;
    private Permission permission;
    
    public void onEnable() {
        INSTANCE = this;
        chatRoomManager = new ChatroomManager(this);
        getCommand("chatroom").setExecutor(new ChatroomCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        this.chat = chatProvider.getProvider();
        
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        this.permission = permissionProvider.getProvider();
        
        Bukkit.getServer().getServicesManager().register(IChatroomManager.class, chatRoomManager, this, ServicePriority.High);
        this.chatRoomManager.loadData();
    }
    
    public void onDisable() {
        chatRoomManager.saveData();
    }
    
    public IChatroomManager getChatroomManager() {
        return chatRoomManager;
    }
    
    public Map<UUID, IChatroom> getEditmode() {
        return editmode;
    }
    
    public Map<UUID, RoomBuilder> getRoomBuilders() {
        return roomBuilders;
    }
    
    public RoomBuilder getBuilder(ConversationContext context) {
        Player player = (Player) context.getForWhom();
        return roomBuilders.get(player.getUniqueId());
    }
    
    public Chat getVaultChat() {
        return chat;
    }
    
    public Permission getVaultPermission() {
        return permission;
    }
    
    public static TitanChat getInstance() {
        return INSTANCE;
    }
    
    static {
        ConfigurationSerialization.registerClass(Chatroom.class);
        ConfigurationSerialization.registerClass(Member.class);
    }
}
