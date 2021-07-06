package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.*;
import com.kingrealms.realms.channel.channels.*;
import com.kingrealms.realms.channel.channels.territory.HamletChannel;
import com.kingrealms.realms.moderation.SocialSpyUtils;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.medievil.Hamlet;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ChatCommands extends BaseCommand {
    
    private static final String DIRECT_MSG_FORMAT = "&b<player1> &a-> &b<player2>&8: &7<message>";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("message") && !cmd.getName().equalsIgnoreCase("reply")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cOnly players may use that command."));
                return true;
            }
        }
        
        RealmProfile player = Realms.getInstance().getProfileManager().getProfile(sender);
        ChannelManager channelManager = Realms.getInstance().getChannelManager();
        
        if (cmd.getName().equalsIgnoreCase("global")) {
            if (args.length == 0) {
                GlobalChannel channel = channelManager.getGlobalChannel();
                player.setChannelFocus(channel);
                player.sendMessage("&gSet your channel to " + channel.getDisplayName());
            } else {
                String message = StringUtils.join(args, " ", 0, args.length);
                if (StringUtils.isEmpty(message)) {
                    player.sendMessage("You provided an invalid message.");
                    return true;
                }
                
                channelManager.getGlobalChannel().sendMessage(player.getUniqueId(), message);
            }
        } else if (cmd.getName().equalsIgnoreCase("staff")) {
            StaffChannel channel = channelManager.getStaffChannel();
            if (player.hasPermission(channel.getPermission())) {
                if (args.length == 0) {
                    player.setChannelFocus(channel);
                    player.sendMessage("&gSet your channel to " + channel.getDisplayName());
                } else {
                    String message = StringUtils.join(args, " ", 0, args.length);
                    if (StringUtils.isEmpty(message)) {
                        player.sendMessage("You provided an invalid message.");
                        return true;
                    }
        
                    channelManager.getStaffChannel().sendMessage(player.getUniqueId(), message);
                }
            } else {
                player.sendMessage("&cYou do not have permission to use that channel.");
            }
        } else if (cmd.getName().equalsIgnoreCase("channel")) {
            if (args.length == 0) {
                Channel focus = player.getChannelFocus();
                player.sendMessage("&gYour current channel focus is " + focus.getDisplayName());
                return true;
            }
            
            Channel channel;
            if (Utils.checkCmdAliases(args, 0, "private", "p")) {
                if (!(args.length > 1)) {
                    player.sendMessage("&cYou must provide a player name to focus private chat to.");
                    return true;
                }
                
                RealmProfile target = Realms.getInstance().getProfileManager().getProfile(args[1]);
                if (target == null) {
                    player.sendMessage("&cThe name you provided did not match a valid player.");
                    return true;
                }
                
                channel = channelManager.getPrivateChannel(player, target);
                if (channel == null) {
                    channel = new PrivateChannel(player, target);
                    channelManager.registerChannel(channel);
                }
                
            } else if (Utils.checkCmdAliases(args, 0, "hamlet", "h")) {
                Hamlet hamlet = (Hamlet) plugin.getTerritoryManager().getTerritory(player);
                if (hamlet == null) {
                    player.sendMessage("&cYou are not a member of a hamlet.");
                    return true;
                }
                
                channel = hamlet.getChannel();
            } else {
                channel = channelManager.getChannel(args[0]);
            }
            
            if (channel == null) {
                player.sendMessage("&cYou provided an invalid channel name or alias.");
                return true;
            }
            
            if (!player.hasPermission(channel.getPermission())) {
                player.sendMessage("&cYou do not have permission to use that channel.");
                return true;
            }
            
            if (args.length > 1) {
                if (Utils.checkCmdAliases(args, 1, "silence")) {
                    if (!channel.isParticipant(player)) {
                        player.sendMessage("&cYou are not a participant of that channel.");
                        return true;
                    }
                    
                    Participant participant = channel.getParticipant(player.getUniqueId());
                    participant.setRead(!participant.canRead());
                    if (participant.canRead()) {
                        player.sendMessage("&gYou have toggled seeing " + channel.getDisplayName() + " &gto &atrue");
                    } else {
                        player.sendMessage("&gYou have toggled seeing " + channel.getDisplayName() + " &gto &cfalse");
                    }
                    return true;
                }
            } else {
                if (!(channel.isParticipant(player))) {
                    player.sendMessage("&cYou are not a participant of that channel.");
                    return true;
                }
            }
            
            player.setChannelFocus(channel);
            player.sendMessage("&gSet your channel focus to &h" + channel.getDisplayName());
        } else if (cmd.getName().equalsIgnoreCase("message")) {
            if (!(args.length > 1)) {
                sender.sendMessage("&cYou must provide a target name and a message.");
                return true;
            }
            
            String targetName = args[0];
            String message = StringUtils.join(args, " ", 1, args.length);
            String format = DIRECT_MSG_FORMAT.replace("<message>", message);
            RealmProfile target = plugin.getProfileManager().getProfile(targetName);
            if (target == null) {
                sender.sendMessage(Utils.color("&cYou provided an invalid player."));
                return true;
            }
            
            String targetFormat = format.replace("<player1>", player.getName()).replace("<player2>", "me");
            String senderFormat = format.replace("<player1>", "me").replace("<player2>", target.getName());
            target.sendMessage(targetFormat);
            player.sendMessage(senderFormat);
            player.setLastMessage(target.getBukkitPlayer());
            target.setLastMessage(player.getBukkitPlayer());
            SocialSpyUtils.sendSocialSpyMessage(player.getName() + " -> " + target.getName() + ": " + message);
        } else if (cmd.getName().equalsIgnoreCase("reply")) {
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a message to send."));
                return true;
            }
            
            String message = StringUtils.join(args, " ", 0, args.length);
            
            CommandSender lastReply = player.getLastMessage();
            if (lastReply == null) {
                player.sendMessage("&cYou have not been messaged by or messaged anyone.");
                return true;
            }
            
            RealmProfile target = plugin.getProfileManager().getProfile(lastReply);
            
            if (target == null || !target.isOnline()) {
                sender.sendMessage(Utils.color("&cThe last person that messaged you could not be found or is offline."));
                return true;
            }
            
            String format = DIRECT_MSG_FORMAT.replace("<message>", message);
            String targetFormat = format.replace("<player1>", sender.getName()).replace("<player2>", "me");
            String senderFormat = format.replace("<player1>", "me").replace("<player2>", target.getName());
            target.sendMessage(Utils.color(targetFormat));
            sender.sendMessage(Utils.color(senderFormat));
            SocialSpyUtils.sendSocialSpyMessage(player.getName() + " -> " + target.getName() + ": " + message);
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        List<String> results = new ArrayList<>();
        List<String> possibleResults = new ArrayList<>();
        
        if (cmd.getName().equalsIgnoreCase("channel")) {
            if (args.length == 1) {
                possibleResults.addAll(Arrays.asList("Private", "Hamlet"));
                for (Channel channel : plugin.getChannelManager().getChannels()) {
                    if (!(channel instanceof PrivateChannel || channel instanceof HamletChannel)) {
                        if (channel.isParticipant(plugin.getProfileManager().getProfile(sender))) {
                            possibleResults.add(channel.getName());
                        }
                    }
                }
                
                results.addAll(Utils.getResults(args[0], possibleResults));
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "private", "p")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        results.add(p.getName());
                    }
                } else {
                    possibleResults.add("silence");
                }
                
                results.addAll(Utils.getResults(args[1], possibleResults));
            }
        } else if (cmd.getName().equalsIgnoreCase("message")) {
            if (args.length == 1) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    possibleResults.add(p.getName());
                }
                
                results.addAll(Utils.getResults(args[0], possibleResults));
            }
        }
        
        return results;
    }
}