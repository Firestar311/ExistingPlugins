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
    
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static String generateAckCode(String characters) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length() - 1)));
        }
        return builder.toString();
    }
    
    public static String convertLocationToString(Location location) {
        String string = "";
        string += location.getWorld().getName() + ":";
        string += location.getX() + ":";
        string += location.getY() + ":";
        string += location.getZ() + ":";
        string += location.getYaw() + ":";
        string += location.getPitch();
        return string;
    }
    
    public static Location getLocationFromString(String locString) {
        try {
            String[] stringArr = locString.split(":");
            World world = Bukkit.getWorld(stringArr[0]);
            double x = Double.parseDouble(stringArr[1]);
            double y = Double.parseDouble(stringArr[2]);
            double z = Double.parseDouble(stringArr[3]);
            float yaw = Float.parseFloat(stringArr[4]);
            float pitch = Float.parseFloat(stringArr[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getReason(int start, String[] args) {
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            reasonBuilder.append(args[i]);
            if (!(i == args.length - 1)) {
                reasonBuilder.append(" ");
            }
        }
        
        String reason = reasonBuilder.toString();
        reason = reason.replace("'", "\'");
        return reason;
    }
    
    public static boolean isInt(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
    
    public static void purgeDirectory(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) purgeDirectory(file);
            file.delete();
        }
    }
    
    public static void openBook(ItemStack book, FirecraftPlayer p) {
        int slot = p.getInventory().getHeldItemSlot();
        ItemStack old = p.getInventory().getItem(slot);
        p.getInventory().setItem(slot, book);
    
        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte)0);
        buf.writerIndex(1);
    
        try {
            Constructor<?> serializerConstructor = Reflection.getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class);
            Object packetDataSerializer = serializerConstructor.newInstance(buf);
        
            Constructor<?> keyConstructor = Reflection.getNMSClass("MinecraftKey").getConstructor(String.class);
            Object bookKey = keyConstructor.newInstance("minecraft:book_open");
        
            Constructor<?> titleConstructor = Reflection.getNMSClass("PacketPlayOutCustomPayload").getConstructor(bookKey.getClass(), Reflection.getNMSClass("PacketDataSerializer"));
            Object payload = titleConstructor.newInstance(bookKey, packetDataSerializer);
    
            Reflection.sendPacket(payload, p.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        p.getInventory().setItem(slot, old);
    }
    
    public static final class Chat {
        
        private Chat() {
        }
        
        private static String formatBase(FirecraftServer server, FirecraftPlayer player) {
            String format = "<cc>&l[<cp>] <sc>(<server>) <displayname> <cc>";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<cp>", Channel.STAFF.getChannelPrefix());
            format = format.replace("<sc>", server.getColor() + "");
            format = format.replace("<server>", server.getName().toUpperCase());
            format = format.replace("<displayname>", player.getNameNoPrefix());
            return format;
        }
        
        public static String formatGlobal(FirecraftPlayer player, String message) {
            String format = "<displayname>&8: [rc]" + message;
            format = format.replace("<displayname>", player.getDisplayName());
            if (player.getToggleValue(Toggle.RECORDING)) format = format.replace("[rc]", "&7");
            if (player.isNicked()) {
                if (Rank.isStaff(player.getNick().getProfile().getMainRank())) format = format.replace("[rc]", "&b");
                else if (player.getNick().getProfile().getMainRank().isEqualToOrHigher(Rank.EMBER))
                    format = format.replace("[rc]", "&f");
                else format = format.replace("[rc]", "&7");
            } else {
                if (Rank.isStaff(player.getMainRank())) format = format.replace("[rc]", "&b");
                else if (player.getMainRank().isEqualToOrHigher(Rank.EMBER)) format = format.replace("[rc]", "&f");
                else format = format.replace("[rc]", "&7");
            }
            
            return format;
        }
        
        public static String formatStaffMessage(FirecraftServer server, FirecraftPlayer player, String message) {
            return formatBase(server, player) + message;
        }
        
        public static String formatStaffJoinLeave(FirecraftServer server, FirecraftPlayer player, String action) {
            String format = formatBase(server, player) + "has <action> &f&lS&8:<cc>" + server.getName();
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<action>", action);
            return format;
        }
        
        public static String formatRankUpdate(FirecraftServer server, FirecraftPlayer player, FirecraftPlayer target, Rank old, Rank newRank) {
            String format = formatBase(server, player) + "changed <target><cc>'s rank from <or> <cc>to <nr>";
            format = format.replace("<target>", target.getNameNoPrefix());
            format = format.replace("<or>", (old == Rank.DEFAULT) ? old.getBaseColor() + "Default" : old.getPrefix());
            format = format.replace("<nr>", newRank.getPrefix());
            format = format.replace("<cc>", Channel.STAFF.getColor());
            return format;
        }
        
        public static String formatSetNick(FirecraftServer server, FirecraftPlayer player, String profile) {
            return formatBase(server, player) + "has nicked as " + profile + ".";
        }
        
        public static String formatResetNick(FirecraftServer server, FirecraftPlayer player) {
            return formatBase(server, player) + "reset their nickname.";
        }
        
        public static String formatVanishToggle(FirecraftServer server, FirecraftPlayer player, boolean isVanished) {
            String format = formatBase(server, player) + "<value> vanish mode.";
            format = isVanished ? format.replace("<value>", "entered") : format.replace("<value>", "exited");
            return format;
        }
        
        public static String formatIncognitoToggle(FirecraftServer server, FirecraftPlayer player, boolean inIncognito) {
            String format = formatBase(server, player) + "<value> incognito mode.";
            format = inIncognito ? format.replace("<value>", "entered") : format.replace("<value>", "exited");
            return format;
        }
        
        public static String formatVanishToggleOthers(FirecraftServer server, FirecraftPlayer player, FirecraftPlayer target) {
            String format = formatBase(server, player) + "<value> vanish mode for <target><cc>.";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = target.isVanished() ? format.replace("<value>", "enabled") : format.replace("<value>", "disabled");
            format = format.replace("<target>", target.getNameNoPrefix());
            return format;
        }
        
        public static String formatSetGamemode(FirecraftServer server, FirecraftPlayer player, GameMode mode) {
            String format = formatBase(server, player) + "set own gamemode to <mode>";
            format = format.replace("<mode>", mode.toString().toLowerCase());
            return format;
        }
        
        public static String formatSetGamemodeOthers(FirecraftServer server, FirecraftPlayer player, GameMode mode, FirecraftPlayer target) {
            String format = formatBase(server, player) + "set <target>'s <cc>gamemode to <mode>";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<target>", target.getNameNoPrefix());
            format = format.replace("<mode>", mode.toString().toLowerCase());
            return format;
        }
        
        public static String formatTeleport(FirecraftServer server, FirecraftPlayer player, FirecraftPlayer target) {
            String format = formatBase(server, player) + "teleported to <target>";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<target>", target.getNameNoPrefix());
            return format;
        }
        
        public static String formatTeleportHere(FirecraftServer server, FirecraftPlayer player, FirecraftPlayer target) {
            String format = formatBase(server, player) + "teleported <target> <cc>to them";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<target>", target.getNameNoPrefix());
            return format;
        }
        
        public static String formatTeleportOthers(FirecraftServer server, FirecraftPlayer player, FirecraftPlayer target1, FirecraftPlayer target2) {
            String format = formatBase(server, player) + "teleported <target1> <cc>to <target2>";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<target1>", target1.getNameNoPrefix());
            format = format.replace("<target2>", target2.getNameNoPrefix());
            return format;
        }
        
        public static String formatStaffModeToggle(FirecraftServer server, FirecraftPlayer player, boolean value) {
            String format = formatBase(server, player) + "<action> staffmode";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = value ? format.replace("<action>", "enabled") : format.replace("<action>", "disabled");
            return format;
        }
        
        public static String formatServerConnect(FirecraftServer server) {
            String format = "<cc>&l[<cp>] <server> <cc>has started.";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<cp>", Channel.STAFF.getChannelPrefix());
            format = format.replace("<sc>", server.getColor() + "");
            format = format.replace("<server>", server.toString());
            return format;
        }
        
        public static String formatServerDisconnect(FirecraftServer server) {
            String format = "<cc>&l[<cp>] <server> <cc>has stopped.";
            format = format.replace("<cc>", Channel.STAFF.getColor());
            format = format.replace("<cp>", Channel.STAFF.getChannelPrefix());
            format = format.replace("<sc>", server.getColor() + "");
            format = format.replace("<server>", server.toString());
            return format;
        }
        
        public static String formatAckWarning(String server, String name) {
            String format = "&6(<server>) &b<name> &fhas acknowledged their warning.";
            format = format.replace("<server>", server.toUpperCase());
            format = format.replace("<name>", name);
            return format;
        }
        
        public static String formatReportAssignSelf(String server, String name, int reportId) {
            String format = "&4[REPORT ADMIN] &a(<server>) &d<staff> &fhas self-assigned the report with the id &c<id>&f.";
            format = format.replace("<server>", server.toUpperCase());
            format = format.replace("<staff>", name);
            format = format.replace("<id>", reportId + "");
            return format;
        }
        
        public static String formatReportAssignOthers(String server, String name, String assignee, int reportId) {
            String format = "&4[REPORT ADMIN] &a(<server>) &d<staff> &fhas assigned the report with the id &c<id>&f to &d<assignee>";
            format = format.replace("<server>", server.toUpperCase());
            format = format.replace("<staff>", name);
            format = format.replace("<id>", reportId + "");
            format = format.replace("<assignee>", assignee);
            return format;
        }
        
        public static String formatReportSetStatus(String server, String name, int reportId, Report.Status status) {
            String format = "&4[REPORT ADMIN] &a(<server>) &d<staff> &fhas set the status of the report with the id &4<id> &fto <status>";
            format = format.replace("<server>", server.toUpperCase());
            format = format.replace("<staff>", name);
            format = format.replace("<id>", reportId + "");
            format = format.replace("<status>", status.getColor() + status.toString());
            return format;
        }
        
        public static String formatReportSetOutcome(String server, String name, int reportId, Report.Outcome outcome) {
            String format = "&4[REPORT ADMIN] &a(<server>) &d<staff> &fhas set the outcome of the report with the id &4<id> &fto <status>";
            format = format.replace("<server>", server.toUpperCase());
            format = format.replace("<staff>", name);
            format = format.replace("<id>", reportId + "");
            format = format.replace("<status>", outcome.getColor() + outcome.toString());
            return format;
        }
        
        public static String formatPrivateMessage(String p1, String p2, String message) {
            String format = "&8[&eP&8] &d&l(i) &d(&a{p1} &d-> &a{p2}&d) &e{message}";
            format = format.replace("{p1}", p1);
            format = format.replace("{p2}", p2);
            format = format.replace("{message}", ChatColor.stripColor(message));
            return format;
        }
        
        public static void sendStaffChatMessage(Collection<FirecraftPlayer> players, FirecraftPlayer staff, String message) {
            if (players.isEmpty()) return;
            players.forEach(p -> {
                if (Rank.isStaff(p.getMainRank())) {
                    if (p.getMainRank().isEqualToOrHigher(staff.getMainRank())) {
                        if (!p.getToggleValue(Toggle.RECORDING)) {
                            p.sendMessage(message);
                        }
                    }
                }
            });
        }
        
        public static void sendCenteredMessage(FirecraftPlayer player, String message) {
            if(message == null || message.equals("")) {
                player.sendMessage("");
                return;
            }
            message = ChatColor.translateAlternateColorCodes('&', message);
    
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;
    
            for(char c : message.toCharArray()){
                if(c == '§'){
                    previousCode = true;
                }else if(previousCode){
                    previousCode = false;
                    isBold = c == 'l' || c == 'L';
                }else{
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                    messagePxSize++;
                }
            }
            int CENTER_PX = 154;
            int halvedMessageSize = messagePxSize / 2;
            int toCompensate = CENTER_PX - halvedMessageSize;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while(compensated < toCompensate){
                sb.append(" ");
                compensated += spaceLength;
            }
            player.sendMessage(sb.toString() + message);
        }
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
        
        public static Skin getSkin(UUID uuid) {
            try {
                String profileURL = profileUrlString.replace("{uuid}", uuid.toString().replace("-", ""));
                URL url = new URL(profileURL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                int read;
                char[] chars = new char[256];
                while ((read = reader.read(chars)) != -1) {
                    buffer.append(chars, 0, read);
                }
                
                JSONObject json = (JSONObject) new JSONParser().parse(buffer.toString());
                JSONArray properties = (JSONArray) json.get("properties");
                
                JSONObject property = (JSONObject) properties.get(0);
                String sN = (String) property.get("name");
                String sV = (String) property.get("value");
                String sS = (String) property.get("signature");
                return new Skin(uuid, sN, sS, sV);
            } catch (Exception e) {
            }
            
            return null;
        }
    }
    
    /**
     * Utility class for Reflection based code.
     * All methods are self explanatory and do not have direct documentation
     */
    public static final class Reflection {
        private Reflection() {
        }
        
        public static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
            String version = getVersion();
            String name = "net.minecraft.server." + version + "." + nmsClassString;
            return Class.forName(name);
        }
        
        public static Class<?> getCraftClass(String craftClassString) {
            try {
                String version = getVersion();
                String name = "org.bukkit.craftbukkit." + version + "." + craftClassString;
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            return null;
        }
        
        public static String getVersion() {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        }
        
        public static void setField(Object target, String field, Object value) {
            try {
                Field f = target.getClass().getDeclaredField(field);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception e) {
            }
        }
        
        public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
            for (final Field field : target.getDeclaredFields()) {
                if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                    field.setAccessible(true);
                    return field;
                }
            }
            
            // Search in parent classes
            if (target.getSuperclass() != null) return getField(target.getSuperclass(), name, fieldType, index);
            throw new IllegalArgumentException("Cannot find field with type " + fieldType);
        }
        
        public static void sendPacket(Object packet, Player player) {
            try {
                Class<?> craftPlayerClass = getCraftClass("entity.CraftPlayer");
                Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
                Object craftPlayer = Objects.requireNonNull(craftPlayerClass).cast(player);
                Object handle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
                Object playerConnection = entityPlayerClass.getField("playerConnection").get(handle);
                Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
                sendPacket.invoke(playerConnection, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            } else if (punishment.getType().equals(Type.BAN) || punishment.getType().equals(Type.TEMP_BAN) || punishment.getType().equals(Type.KICK)) {
                if (FirecraftAPI.isCore()) {
                    Bukkit.getScheduler().runTask(FirecraftAPI.getFirecraftCore(), () -> {
                        FirecraftPlayer p = FirecraftAPI.getPlayer(punishment.getTarget());
                        if (punishment.getType().equals(Type.BAN))
                            p.kickPlayer(Utils.color(Messages.banMessage(punishment, "Permanent")));
                        else if (punishment.getType().equals(Type.KICK))
                            p.kickPlayer(Utils.color(Messages.kickMessage(punishment.getPunisherName(), punishment.getReason())));
                        else if (punishment.getType().equals(Type.TEMP_BAN)) {
                            TemporaryPunishment tempPunishment = ((TemporaryPunishment) punishment);
                            String expireTime = tempPunishment.formatExpireTime();
                            p.kickPlayer(Utils.color(Messages.banMessage(punishment, expireTime)));
                        }
                    });
                }
            } else if (punishment.getType().equals(Type.WARN)) {
                if (FirecraftAPI.isCore()) {
                    if (Bukkit.getPlayer(target.getUniqueId()) != null) {
                        String code = Utils.generateAckCode(Utils.codeCharacters);
                        FirecraftAPI.getFirecraftCore().addAckCode(target.getUniqueId(), code);
                        target.sendMessage(Messages.warnMessage(punishment.getPunisherName(), punishment.getReason(), code));
                    }
                }
            }
            
            if (FirecraftAPI.isCore()) {
                if (target.getScoreboard() != null) target.getScoreboard().updateScoreboard(target);
            }
        }
        
        public static void handleRemovePunish(FirecraftPacket packet, Database database, Collection<FirecraftPlayer> players) {
            FPacketPunishRemove punishRemove = ((FPacketPunishRemove) packet);
            Punishment punishment = database.getPunishment(punishRemove.getPunishmentId());
            String format = punishment.formatRemoveMessage(FirecraftAPI.getServer(packet.getServerId()).getName().toUpperCase());
            for (FirecraftPlayer player : players) {
                if (Rank.isStaff(player.getMainRank())) {
                    player.sendMessage(format);
                }
            }
            
            FirecraftPlayer target = FirecraftAPI.getPlayer(punishment.getTarget());
            if (punishment.getType().equals(Type.MUTE) || punishment.getType().equals(Type.TEMP_MUTE)) {
                if (Bukkit.getPlayer(punishment.getTarget()) != null) {
                    target.sendMessage("<nc>Your mute has been lifted by <vc>" + punishment.getRemoverName());
                }
            } else if (punishment.getType().equals(Type.JAIL)) {
                if (Bukkit.getPlayer(punishment.getTarget()) != null) {
                    if (FirecraftAPI.isCore()) {
                        Bukkit.getScheduler().runTask(FirecraftAPI.getFirecraftCore(), () -> {
                            target.sendMessage("<nc>You have been removed from jail by <vc>" + punishment.getRemoverName());
                            target.teleport(FirecraftAPI.getFirecraftCore().getSpawn());
                        });
                    }
                }
            }
            
            if (FirecraftAPI.isCore()) {
                if (target.getScoreboard() != null) target.getScoreboard().updateScoreboard(target);
            }
        }
        
        public static void handleReport(FirecraftPacket packet, FirecraftServer server, Database database, Collection<FirecraftPlayer> players) {
            FPacketReport packetReport = ((FPacketReport) packet);
            Report report = database.getReport(packetReport.getReportId());
            if (report == null) return;
            String reporterName = database.getPlayerName(report.getReporter());
            String targetName = database.getPlayerName(report.getTarget());
            for (FirecraftPlayer player : players) {
                if (player.getMainRank().isEqualToOrHigher(Rank.TRIAL_MOD)) {
                    if (!player.getToggleValue(Toggle.RECORDING)) {
                        player.sendMessage(Messages.reportBcFormat(server, report.getId(), reporterName, targetName, report.getReason()));
                    }
                }
            }
        }
    }
    
    public static final class Time {
        public static String formatTime(long time) {
            Duration remainingTime = Duration.ofMillis(time);
            long days = remainingTime.toDays();
            remainingTime = remainingTime.minusDays(days);
            long hours = remainingTime.toHours();
            remainingTime = remainingTime.minusHours(hours);
            long minutes = remainingTime.toMinutes();
            remainingTime = remainingTime.minusMinutes(minutes);
            long seconds = remainingTime.getSeconds();
            
            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append("d");
            if (hours > 0) sb.append(hours).append("h");
            if (minutes > 0) sb.append(minutes).append("m");
            if (seconds > 0) sb.append(seconds).append("s");
            return sb.toString();
        }
        
        private Time() {
        }
    }
    
    public static final class Item {
        public static ItemStack addNBTData(IFirecraftCore plugin, ItemStack stack, HashMap<String, String> data) {
            for (String s : data.keySet()) {
                plugin.getNbtWrapper().addNBTString(stack, s, data.get(s));
            }
            return stack;
        }
        
        public static String getNBTData(IFirecraftCore plugin, ItemStack stack, String key) {
            return plugin.getNbtWrapper().getNBTString(stack, key);
        }
    }
}