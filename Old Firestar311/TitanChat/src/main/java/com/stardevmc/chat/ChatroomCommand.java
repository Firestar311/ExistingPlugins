package com.stardevmc.chat;

import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.*;
import com.stardevmc.chat.convo.ChatRoomDisplayName;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChatroomCommand implements CommandExecutor {
    
    private TitanChat plugin;
    
    public ChatroomCommand(TitanChat plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cInvalid amount of arguments"));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "create", "c")) {
            ///chatroom create <name> <owner|self|server>
            if (!(args.length == 3)) {
                player.sendMessage(Utils.color("&cUsage: /chatroom create <name> <owner|self|server"));
                return true;
            }
            
            String name = args[1];
            String o = args[2];
            IOwner roomOwner;
            
            if (o.toLowerCase().contains("server")) {
                roomOwner = new ServerOwner();
            } else if (o.toLowerCase().contains("self")) {
                roomOwner = new PlayerOwner(player.getUniqueId());
            } else {
                if (Bukkit.getPlayer(o) != null) {
                    roomOwner = new PlayerOwner(Bukkit.getPlayer(o).getUniqueId());
                } else {
                    UUID uuid = Utils.getUUIDFromName(o);
                    if (uuid == null) {
                        player.sendMessage(Utils.color("&cYou provided an invalid name for the chat room owner."));
                        return true;
                    }
                    
                    roomOwner = new PlayerOwner(uuid);
                }
            }
            
            RoomBuilder roomBuilder = new RoomBuilder(roomOwner).setId(name);
            plugin.getRoomBuilders().put(player.getUniqueId(), roomBuilder);
            
            player.sendMessage(Utils.color("&aStarting the creation of a chat room, you will not be able to chat normally until finished or you type 'cancel'"));
            ConversationFactory cf = new ConversationFactory(plugin);
            cf.withLocalEcho(false).withFirstPrompt(new ChatRoomDisplayName()).buildConversation(player).begin();
        } else if (Utils.checkCmdAliases(args, 0, "edit", "e")) {
            if (!player.hasPermission("titanchat.command.edit")) {
                player.sendMessage(Utils.color("&cYou are not allowed to edit chatrooms"));
                return true;
            }
            
            if (!(args.length > 0)) {
                player.sendMessage(Utils.color("&cYou must provide a channel name."));
                return true;
            }
            
            IChatroom chatroom = plugin.getChatroomManager().getChatroom(args[1]);
            if (chatroom == null) {
                player.sendMessage(Utils.color("&cThe name you provided did not match a chatroom."));
                return true;
            }
    
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cYou must provide a subcommand."));
                return true;
            }
            
            //Displayname, format, description, icon, permission
            if (Utils.checkCmdAliases(args, 2, "displayname", "dn")) {
                if (!(args.length > 3)) {
                    player.sendMessage(Utils.color("&cNot enough arguments."));
                    return true;
                }
                
                if (!chatroom.getRole(player).hasPermission(player, RoomPermission.MANAGE_DISPLAYNAME)) {
                    player.sendMessage(Utils.color("&cYou cannot edit that in that chatroom."));
                    return true;
                }
                
                String value = StringUtils.join(args, " ", 3, args.length);
                chatroom.setDisplayName(value);
            } else if (Utils.checkCmdAliases(args, 2, "format", "f")) {
                if (!(args.length > 3)) {
                    player.sendMessage(Utils.color("&cNot enough arguments."));
                    return true;
                }
    
                if (!chatroom.getRole(player).hasPermission(player, RoomPermission.MANAGE_FORMAT)) {
                    player.sendMessage(Utils.color("&cYou cannot edit that in that chatroom."));
                    return true;
                }
    
                String value = StringUtils.join(args, " ", 3, args.length);
                chatroom.setFormat(value);
            } else if (Utils.checkCmdAliases(args, 2, "description", "desc")) {
                if (!(args.length > 3)) {
                    player.sendMessage(Utils.color("&cNot enough arguments."));
                    return true;
                }
    
                if (!chatroom.getRole(player).hasPermission(player, RoomPermission.MANAGE_DESCRIPTION)) {
                    player.sendMessage(Utils.color("&cYou cannot edit that in that chatroom."));
                    return true;
                }
    
                String value = StringUtils.join(args, " ", 3, args.length);
                chatroom.setDescription(value);
            } else if (Utils.checkCmdAliases(args, 2, "icon", "i")) {
                if (!chatroom.getRole(player).hasPermission(player, RoomPermission.MANAGE_ICON)) {
                    player.sendMessage(Utils.color("&cYou cannot edit that in that chatroom."));
                    return true;
                }
    
                ItemStack icon = player.getInventory().getItemInMainHand();
                if (icon == null) {
                    player.sendMessage(Utils.color("&cYou must be holding an item."));
                    return true;
                }
    
                chatroom.setIcon(icon.getType());
            } else if (Utils.checkCmdAliases(args, 2, "permission", "perm")) {
                if (!(args.length > 3)) {
                    player.sendMessage(Utils.color("&cNot enough arguments."));
                    return true;
                }
    
                if (!chatroom.getRole(player).hasPermission(player, RoomPermission.MANAGE_PERMISSION)) {
                    player.sendMessage(Utils.color("&cYou cannot edit that in that chatroom."));
                    return true;
                }
    
                String value = StringUtils.join(args, " ", 3, args.length);
                chatroom.setPermission(value);
            }
        } else if (Utils.checkCmdAliases(args, 0, "manage", "m")) {
        
        } else if(Utils.checkCmdAliases(args, 0, "join", "j")) {
            if (!(args.length > 0)) {
                player.sendMessage(Utils.color("&cYou must provide a channel name."));
                return true;
            }
    
            IChatroom chatroom = plugin.getChatroomManager().getChatroom(args[1]);
            if (chatroom == null) {
                player.sendMessage(Utils.color("&cThe name you provided did not match a valid chatroom name."));
                return true;
            }
            
            if (!chatroom.hasPermission(player)) {
                player.sendMessage(Utils.color("&cYou do not have permission to join that chatroom."));
                return true;
            }
            
            if (!chatroom.isGlobal()) {
                player.sendMessage(Utils.color("&cThat chatroom is not globally joinable."));
                return true;
            }
            
            chatroom.addMember(player.getUniqueId(), DefaultRoles.MEMBER);
            player.sendMessage(Utils.color("&aYou have joined the chatroom " + chatroom.getDisplayName()));
        } else if (Utils.checkCmdAliases(args, 0, "gui")) {
            plugin.getChatroomManager().openGUI(player);
        } else {
            if (!(args.length > 0)) {
                player.sendMessage(Utils.color("&cYou must provide a channel name."));
                return true;
            }
            
            String argument = args[0];
            
            ChatroomCommandEvent cmdEvent = new ChatroomCommandEvent(player, argument);
            Bukkit.getServer().getPluginManager().callEvent(cmdEvent);
            argument = cmdEvent.getRoomId();
            
            IChatroom chatroom = plugin.getChatroomManager().getChatroom(argument);
            if (chatroom == null) {
                player.sendMessage(Utils.color("&cThe name you provided did not match a valid chatroom name."));
                return true;
            }
    
            if (!chatroom.hasPermission(player)) {
                player.sendMessage(Utils.color("&cYou do not have permission to join that chatroom."));
                return true;
            }
            
            plugin.getChatroomManager().changeActiveChatroom(player, chatroom);
            player.sendMessage(Utils.color("&aYou changed your chatroom to " + plugin.getChatroomManager().getActiveChatroom(player).getDisplayName()));
        }
    
        return true;
    }
    
}
