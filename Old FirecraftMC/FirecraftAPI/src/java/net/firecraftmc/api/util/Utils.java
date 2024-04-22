package net.firecraftmc.api.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.enums.Channel;
import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.Database;
import net.firecraftmc.api.model.Report;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.model.player.Skin;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.packets.*;
import net.firecraftmc.api.plugin.IFirecraftCore;
import net.firecraftmc.api.punishments.Punishment;
import net.firecraftmc.api.punishments.Punishment.Type;
import net.firecraftmc.api.punishments.TemporaryPunishment;
import net.firecraftmc.api.toggles.Toggle;
import net.firecraftmc.api.util.chat.DefaultFontInfo;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.time.Duration;
import java.util.*;

public final class Utils {
    public static final String codeCharacters = "1234567890";
    
    private Utils() {
    }
    
    public static String color(String uncolored) {
        return ChatColor.translateAlternateColorCodes('&', uncolored);
    }
    
    public static boolean checkFirecraftPlayer(Player p, FirecraftPlayer player) {
        if (player == null) {
            p.sendMessage("§7§oYour data has not been received yet.");
            return false;
        }
        return true;
    }
    
    public static FirecraftPlayer getPlayer(String name, Collection<FirecraftPlayer> players) {
        for (FirecraftPlayer fp : players) {
            if (fp.isNicked()) {
                if (fp.getNick().getProfile().getName().equalsIgnoreCase(name)) {
                    return fp;
                }
            } else {
                if (fp.getName().equalsIgnoreCase(name)) {
                    return fp;
                }
            }
        }
        return null;
    }
    
    public static final class Command {
        
        private Command() {
        }
        
        public static boolean checkArgCountGreater(CommandSender sender, String[] args, int amount) {
            if (!(args.length > amount)) {
                sender.sendMessage("§cYou did not provide enough arguments.");
                return false;
            }
            return true;
        }
        
        public static boolean checkArgCountExact(CommandSender sender, String[] args, int amount) {
            if (args.length != amount) {
                sender.sendMessage("§cInvalid amount of arguments.");
                return false;
            }
            return true;
        }
        
        public static boolean checkCmdAliases(String[] args, int index, String... aliases) {
            if (aliases == null) return false;
            for (String s : aliases) {
                if (args[index].equalsIgnoreCase(s)) return true;
            }
            
            return false;
        }
        
        public static void registerCommands(JavaPlugin plugin, CommandExecutor executor, String... cmds) {
            if (cmds != null && cmds.length > 0) {
                for (String c : cmds) {
                    plugin.getCommand(c).setExecutor(executor);
                }
            }
        }

//        public static boolean isQAAllowed(FirecraftServer server, FirecraftPlayer player) {
//            if (server.getType().equals(ServerType.QA) || server.getType().equals(ServerType.DEV)) {
//                return player.hasRank(Rank.QUALITY_ASSURANCE) || player.getMainRank().equals(Rank.FIRECRAFT_TEAM);
//            }
//            return false;
//        }
    }
    
    /**
     * Utilities for getting information from the Mojang API
     * All methods are self explanatory and do not have direct documentation
     */
    public static final class Mojang {
        private static final String profileUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
        
        private Mojang() {
        }
        
        public static UUID getUUIDFromName(String username) {
            String s = "https://api.mojang.com/users/profiles/minecraft/" + username;
            s += "?at=" + (System.currentTimeMillis() / 1000);
            try {
                URL url = new URL(s);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                int read;
                char[] chars = new char[256];
                while ((read = reader.read(chars)) != -1) {
                    buffer.append(chars, 0, read);
                }
                
                reader.close();
                
                JSONObject json = (JSONObject) new JSONParser().parse(buffer.toString());
                String uuidString = (String) json.get("id");
                
                String finalUUIDString = uuidString.substring(0, 8) + "-";
                finalUUIDString += uuidString.substring(8, 12) + "-";
                finalUUIDString += uuidString.substring(12, 16) + "-";
                finalUUIDString += uuidString.substring(16, 20) + "-";
                finalUUIDString += uuidString.substring(20, 32);
                return UUID.fromString(finalUUIDString);
            } catch (Exception e) {
            }
            return null;
        }
        
        public static String getNameFromUUID(String uuid) {
            String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "");
            try {
                URL url = new URL(profileURL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                int read;
                char[] chars = new char[256];
                while ((read = reader.read(chars)) != -1) {
                    buffer.append(chars, 0, read);
                }
                
                reader.close();
                
                JSONObject json = (JSONObject) new JSONParser().parse(buffer.toString());
                return (String) json.get("name");
            } catch (Exception e) {
            }
            return null;
        }
    }
    
    public static final class Socket {
        private Socket() {
        }
        
        public static void handlePunish(FirecraftPacket packet, Database database, Collection<FirecraftPlayer> players) {
            FPacketPunish punishPacket = ((FPacketPunish) packet);
            Punishment punishment = database.getPunishment(punishPacket.getPunishmentId());
            String format = punishment.formatMessage();
            for (FirecraftPlayer player : players) {
                if (Rank.isStaff(player.getMainRank())) {
                    player.sendMessage(format);
                }
            }
            
            FirecraftPlayer target = FirecraftAPI.getPlayer(punishment.getTarget());
            if (punishment.getType().equals(Type.MUTE) || punishment.getType().equals(Type.TEMP_MUTE)) {
                if (Bukkit.getPlayer(punishment.getTarget()) != null) {
                    target.sendMessage("");
                    target.sendMessage("&4&l╔══════════════════════════");
                    target.sendMessage("&4&l║ &c&lYou have been muted!");
                    target.sendMessage("&4&l║");
                    if (punishment.getType().equals(Type.TEMP_MUTE))
                        target.sendMessage("&4&l║ <nc>The mute expires in <vc>" + punishment.formatExpireTime());
                    else target.sendMessage("&4&l║ <nc>This mute is <vc>&lPERMANENT");
                    target.sendMessage("&4&l║ <nc>The reason is <vc>" + punishment.getReason());
                    target.sendMessage("&4&l║ <nc>The staff member that muted you is <vc>" + punishment.getPunisherName());
                    target.sendMessage("&4&l║");
                    target.sendMessage("&4&l╚══════════════════════════");
                    target.sendMessage("");
                }
            }
        }
    }
}
