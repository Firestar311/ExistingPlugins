package com.stardevmc.chat;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.config.ConfigManager;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.*;
import com.stardevmc.chat.defaultrooms.GlobalRoom;
import com.stardevmc.chat.defaultrooms.StaffRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

final class ChatroomManager implements IChatroomManager {
    
    private Map<String, IChatroom> chatrooms = new HashMap<>();
    private Map<UUID, IChatroom> activeChatrooms = new HashMap<>();
    private IChatroom globalRoom, staffroom;
    private TitanChat plugin;
    private ConfigManager configManager;
    
    public ChatroomManager(TitanChat plugin) {
        this.plugin = plugin;
        globalRoom = new GlobalRoom();
        this.staffroom = new StaffRoom();
        registerChatroom(globalRoom);
        registerChatroom(staffroom);
        
        this.configManager = new ConfigManager(plugin, "chatrooms");
        this.configManager.setup();
    }
    
    public void changeActiveChatroom(Player player, IChatroom newActiveRoom) {
        if (!newActiveRoom.isMember(player)) {
            if (newActiveRoom.hasPermission(player) || newActiveRoom.isAutoJoin()) {
                newActiveRoom.addMember(player.getUniqueId(), DefaultRoles.MEMBER);
            }
        }
    
        this.activeChatrooms.replace(player.getUniqueId(), newActiveRoom);
    }
    
    public Map<String, IChatroom> getChatrooms() {
        return new HashMap<>(chatrooms);
    }
    
    public boolean registerChatroom(IChatroom chatroom) {
        if (chatroom.getId().toLowerCase().contains("default")) return false;
        this.chatrooms.put(chatroom.getId(), chatroom);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (chatroom.isAutoJoin()) {
                if (chatroom.hasPermission(player)) {
                    chatroom.addMember(player.getUniqueId(), DefaultRoles.MEMBER);
                }
            }
        }
        
        return true;
    }
    
    public boolean unregisterChatroom(IChatroom chatroom) {
        if (chatroom.getId().toLowerCase().contains("default")) return false;
        this.chatrooms.remove(chatroom.getId());
        return true;
    }
    
    public String extractName(ItemStack stack) {
        try {
            return NBTWrapper.getNBTString(stack, "channel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return null;
    }
    
    public IChatroom getGlobalRoom() {
        return globalRoom;
    }
    
    public IChatroom getActiveChatroom(Player player) {
        this.activeChatrooms.computeIfAbsent(player.getUniqueId(), k -> this.globalRoom);
        return this.activeChatrooms.get(player.getUniqueId());
    }
    
    public List<IChatroom> getChatrooms(Player player) {
        List<IChatroom> playerRooms = new ArrayList<>();
        for (IChatroom chatroom : this.chatrooms.values()) {
            if (chatroom.isMember(player)) {
                playerRooms.add(chatroom);
            }
        }
        return playerRooms;
    }
    
    public IChatroom getChatroom(String name) {
        name = ChatColor.stripColor(name);
        for (IChatroom chatroom : this.chatrooms.values()) {
            if (chatroom.getId().equalsIgnoreCase(name)) {
                return chatroom;
            }
        }
        return null;
    }
    
    public void saveData() {
        this.chatrooms.forEach((id, chatroom) -> configManager.getConfig().set("chatrooms." + id, chatroom));
        this.activeChatrooms.forEach((uuid, chatroom) -> configManager.getConfig().set("activerooms." + uuid.toString(), chatroom.getId()));
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = this.configManager.getConfig();
        ConfigurationSection chatroomSection = config.getConfigurationSection("chatrooms");
        if (chatroomSection != null) {
            for (String cr : chatroomSection.getKeys(false)) {
                Chatroom chatroom = (Chatroom) config.get("chatrooms." + cr);
                this.chatrooms.put(cr, chatroom);
            }
        }
        
        ConfigurationSection activeSection = config.getConfigurationSection("activerooms");
        if (activeSection != null) {
            for (String u : activeSection.getKeys(false)) {
                UUID uuid = UUID.fromString(u);
                IChatroom chatroom = getChatroom(config.getString("activerooms." + u));
                this.activeChatrooms.put(uuid, chatroom);
            }
        }
    }
    
    public void openGUI(Player player) {
        PaginatedGUI chatroomGui = new PaginatedGUI(plugin, "Chatrooms", true, 54, false);
        for (IChatroom chatroom : chatrooms.values()) {
            if (chatroom.hasPermission(player)) {
                ItemBuilder itemBuilder = ItemBuilder.start(chatroom.getIcon()).withLore("");
                
                if (chatroom.isMember(player)) {
                    itemBuilder.withLore("&aYou are a member of this chatroom.");
                } else {
                    itemBuilder.withLore("&cYou are not a member of this chatroom");
                }
                
                if (getActiveChatroom(player).getId().equalsIgnoreCase(chatroom.getId())) {
                    itemBuilder.withLore("&eThis chatroom is your current focus.");
                }
                
                
                
                GUIButton button = new GUIButton(itemBuilder.buildItem());
                button.setListener(e -> {
                    Player p = (Player) e.getWhoClicked();
                    plugin.getChatroomManager().changeActiveChatroom(p, chatroom);
                    p.closeInventory();
                    p.sendMessage(Utils.color("&aChanged your channel to " + chatroom.getDisplayName()));
                });
                chatroomGui.addButton(button);
            }
        }
        chatroomGui.openGUI(player);
    }
    
    public IChatroom getStaffRoom() {
        return staffroom;
    }
}