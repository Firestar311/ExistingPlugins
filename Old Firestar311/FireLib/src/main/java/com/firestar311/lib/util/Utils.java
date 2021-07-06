package com.firestar311.lib.util;

import com.firestar311.lib.player.User;
import com.firestar311.lib.player.PlayerManager;
import com.google.gson.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public final class Utils {
    private static final String profileUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    
    private Utils() {
    }
    
    public static String color(String uncolored) {
        return ChatColor.translateAlternateColorCodes('&', uncolored);
    }
    
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void registerConfigClasses(Class<? extends ConfigurationSerializable>... classes) {
        for (Class<? extends ConfigurationSerializable> serializable : classes) {
            ConfigurationSerialization.registerClass(serializable);
        }
    }
    
    public static List<String> convertUUIDListToStringList(Collection<UUID> uuidList) {
        List<String> uuidStrings = new ArrayList<>();
        for (UUID uuid : uuidList) {
            uuidStrings.add(uuid.toString());
        }
        return uuidStrings;
    }
    
    public static List<UUID> getUUIDListFromStringList(List<String> stringUUIDList) {
        List<UUID> uuidList = new ArrayList<>();
        for (String u : stringUUIDList) {
            try {
                UUID uuid = UUID.fromString(u);
                uuidList.add(uuid);
            } catch (Exception e) {}
        }
        return uuidList;
    }
    
    @Deprecated
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
    
    public static long parseTime(String rawTime) {
        Entry<Long, String> years = extractRawTime(rawTime, Unit.YEARS);
        Entry<Long, String> months = extractRawTime(years.getValue(), Unit.MONTHS);
        Entry<Long, String> weeks = extractRawTime(months.getValue(), Unit.WEEKS);
        Entry<Long, String> days = extractRawTime(weeks.getValue(), Unit.DAYS);
        Entry<Long, String> hours = extractRawTime(days.getValue(), Unit.HOURS);
        Entry<Long, String> minutes = extractRawTime(hours.getValue(), Unit.MINUTES);
        Entry<Long, String> seconds = extractRawTime(minutes.getValue(), Unit.SECONDS);
        return years.getKey() + months.getKey() + weeks.getKey() + days.getKey() + hours.getKey() + minutes.getKey() + seconds.getKey();
    }
    
    private static Entry<Long, String> extractRawTime(String rawTime, Unit unit) {
        rawTime = rawTime.toLowerCase();
        String[] rawArray;
        for (String alias : unit.getAliases()) {
            alias = alias.toLowerCase();
            if (rawTime.contains(alias)) {
                rawArray = rawTime.split(alias);
                String fh = rawArray[0];
                long rawLength;
                try {
                    rawLength = Integer.parseInt(fh);
                } catch (NumberFormatException e) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = fh.length() - 1; i > 0; i--) {
                        char c = fh.charAt(i);
                        if (Character.isDigit(c)) {
                            sb.insert(0, c);
                        } else {
                            break;
                        }
                    }
                    rawLength = Integer.parseInt(sb.toString());
                }
                rawTime = rawTime.replace(rawLength + alias, "");
                
                return new SimpleEntry<>(unit.convertTime(rawLength), rawTime);
            }
        }
        
        return new SimpleEntry<>(0L, rawTime);
    }
    
    public static void purgeDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) { purgeDirectory(file); }
                file.delete();
            }
        }
    }
    
    public static String convertUUIDToName(UUID uuid, String nullText) {
        if (uuid == null) {
            return nullText;
        }
    
        RegisteredServiceProvider<PlayerManager> playerRsp = Bukkit.getServer().getServicesManager().getRegistration(PlayerManager.class);
        PlayerManager playerManager = playerRsp.getProvider();
        if (playerManager == null) {
            return nullText;
        }
        
        User user = playerManager.getUser(uuid);
        if (user == null) {
            return nullText;
        }
        
        return user.getLastName();
    }
    
    public static void openBook(ItemStack book, Player p) {
        int slot = p.getInventory().getHeldItemSlot();
        ItemStack old = p.getInventory().getItem(slot);
        p.getInventory().setItem(slot, book);
        
        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);
        
        try {
            Constructor<?> serializerConstructor = ReflectionUtils.getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class);
            Object packetDataSerializer = serializerConstructor.newInstance(buf);
            
            Constructor<?> keyConstructor = ReflectionUtils.getNMSClass("MinecraftKey").getConstructor(String.class);
            Object bookKey = keyConstructor.newInstance("minecraft:book_open");
            
            Constructor<?> titleConstructor = ReflectionUtils.getNMSClass("PacketPlayOutCustomPayload").getConstructor(bookKey.getClass(), ReflectionUtils.getNMSClass("PacketDataSerializer"));
            Object payload = titleConstructor.newInstance(bookKey, packetDataSerializer);
            
            ReflectionUtils.sendPacket(payload, p.getPlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        p.getInventory().setItem(slot, old);
    }
    
    public static void sendCenteredMessage(Player player, String message) {
        player.sendMessage(getCenteredMessage(message));
    }
    
    public static boolean checkCmdAliases(String[] args, int index, String... aliases) {
        if (aliases == null) { return false; }
        for (String s : aliases) {
            if (args[index].equalsIgnoreCase(s)) { return true; }
        }
        
        return false;
    }
    
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
        if (days > 0) { sb.append(days).append("d"); }
        if (hours > 0) { sb.append(hours).append("h"); }
        if (minutes > 0) { sb.append(minutes).append("m"); }
        if (seconds > 0) { sb.append(seconds).append("s"); }
        return sb.toString();
    }
    
    public static List<String> wrapLore(int maxPerLine, String loreText) {
        String[] loreWords = loreText.split(" ");
        List<String> lore = new LinkedList<>();
        lore.add("");
        
        StringBuilder lineBuilder = new StringBuilder();
        Iterator<String> wordIterator = new LinkedList<>(Arrays.asList(loreWords)).iterator();
        
        while (wordIterator.hasNext()) {
            String word = wordIterator.next();
            if (word.length() >= maxPerLine - 1) {
                lore.add("&7" + lineBuilder.toString());
                lineBuilder = new StringBuilder();
                lore.add(word);
            } else if (word.length() + lineBuilder.length() <= maxPerLine) {
                lineBuilder.append(word).append(" ");
            } else {
                lore.add("&7" + lineBuilder.toString());
                lineBuilder = new StringBuilder();
                lineBuilder.append(word).append(" ");
            }
            if (!wordIterator.hasNext()) {
                lore.add("&7" + lineBuilder.toString());
                lineBuilder = new StringBuilder();
            }
        }
        return lore;
    }
    
    public static boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String blankLine(int length) {
        StringBuilder sb = new StringBuilder("&l&m");
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
        return Utils.color(sb.toString());
    }
    
    public static boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    
    private static StringBuilder getJsonBuffer(String urlString) throws IOException {
        URL url = new URL(urlString);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder buffer = new StringBuilder();
        int read;
        char[] chars = new char[256];
        while ((read = reader.read(chars)) != -1) {
            buffer.append(chars, 0, read);
        }
        
        reader.close();
        return buffer;
    }
    
    private static UUID formatUUID(String uuidString) {
        String finalUUIDString = uuidString.substring(0, 8) + "-";
        finalUUIDString += uuidString.substring(8, 12) + "-";
        finalUUIDString += uuidString.substring(12, 16) + "-";
        finalUUIDString += uuidString.substring(16, 20) + "-";
        finalUUIDString += uuidString.substring(20, 32);
        return UUID.fromString(finalUUIDString);
    }
    
    private static void searchSuperClass(Class<?> clazz, List<Field> fields) {
        if (clazz.getSuperclass() != null) {
            Class<?> superClass = clazz.getSuperclass();
            searchSuperClass(superClass, fields);
        }
        
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    }
    
    public static List<Chunk> getFacingChunks(Chunk base) {
        World world = base.getWorld();
        List<Chunk> chunks = new ArrayList<>();
        Block center = base.getBlock(8, 128, 8);
        
        Block xPos = world.getBlockAt(center.getX() + 16, center.getY(), center.getZ());
        Block xNeg = world.getBlockAt(center.getX() - 16, center.getY(), center.getZ());
        Block zPos = world.getBlockAt(center.getX(), center.getY(), center.getZ() + 16);
        Block zNeg = world.getBlockAt(center.getX(), center.getY(), center.getZ() - 16);
        
        chunks.add(xPos.getChunk());
        chunks.add(xNeg.getChunk());
        chunks.add(zPos.getChunk());
        chunks.add(zNeg.getChunk());
        
        return chunks;
    }
    
    public static List<Chunk> getSurroundingChunks(Chunk base) {
        World world = base.getWorld();
        Block center = base.getBlock(8, 128, 8);
        Block xPos = world.getBlockAt(center.getX() + 16, center.getY(), center.getZ());
        Block xNeg = world.getBlockAt(center.getX() - 16, center.getY(), center.getZ());
        Block zPos = world.getBlockAt(center.getX(), center.getY(), center.getZ() + 16);
        Block zNeg = world.getBlockAt(center.getX(), center.getY(), center.getZ() - 16);
        
        Block xpzp = world.getBlockAt(center.getX() + 16, center.getY(), center.getZ() + 16);
        Block xpzn = world.getBlockAt(center.getX() + 16, center.getY(), center.getZ() - 16);
        Block xnzn = world.getBlockAt(center.getX() - 16, center.getY(), center.getZ() - 16);
        Block xnzp = world.getBlockAt(center.getX() - 16, center.getY(), center.getZ() + 16);
    
        return new ArrayList<>(Arrays.asList(xPos.getChunk(), xNeg.getChunk(), zPos.getChunk(), zNeg.getChunk(), xpzp.getChunk(),
                xpzn.getChunk(), xnzn.getChunk(), xnzp.getChunk()));
    }
    
    public static class Async {
        public static void getJsonObject(String urlString, Consumer<JsonObject> success, Consumer<Exception> fail) {
            new Thread(() -> {
                try {
                    StringBuilder buffer = getJsonBuffer(urlString);
                    success.accept((JsonObject) new JsonParser().parse(buffer.toString()));
                } catch (Exception e) {
                    if (fail != null) { fail.accept(e); }
                }
            });
        }
        
        public static void getUUIDFromName(String username, Consumer<UUID> success, Consumer<Exception> fail) {
            String s = "https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + (System.currentTimeMillis() / 1000);
            
            getJsonObject(s, json -> {
                try {
                    success.accept(formatUUID(json.get("id").getAsString()));
                } catch (Exception e) {
                    if (fail != null) { fail.accept(e); }
                }
            }, fail);
        }
        
        public static void getNameFromUUID(UUID uuid, Consumer<String> success, Consumer<Exception> fail) {
            String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "");
            getJsonObject(profileURL, json -> success.accept(json.get("name").getAsString()), fail);
        }
        
        public static void getSkin(UUID uuid, Consumer<Skin> success, Consumer<Exception> fail) {
            String profileURL = profileUrlString.replace("{uuid}", uuid.toString().replace("-", ""));
            getJsonObject(profileURL, json -> {
                JsonArray properties = (JsonArray) json.get("properties");
                
                JsonObject property = (JsonObject) properties.get(0);
                String sN = property.get("name").getAsString();
                String sV = property.get("value").getAsString();
                String sS = property.get("signature").getAsString();
                success.accept(new Skin(uuid, sN, sS, sV));
            }, fail);
        }
    }
    
    @Deprecated
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
    
    public static String getCenteredMessage(String message) {
        if (message == null || message.equals("")) {
            return "";
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        
        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
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
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString() + message;
    }
    
    public static JsonObject getJsonObject(String urlString) {
        try {
            StringBuilder buffer = getJsonBuffer(urlString);
            return (JsonObject) new JsonParser().parse(buffer.toString());
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public static UUID getUUIDFromName(String username) {
        String s = "https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + (System.currentTimeMillis() / 1000);
        
        JsonObject json = getJsonObject(s);
        if (json == null) {
            return null;
        }
        JsonElement idObject = json.get("id");
        if (idObject == null) {
            return null;
        }
        
        return formatUUID(idObject.getAsString());
    }
    
    public static String getNameFromUUID(UUID uuid) {
        String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "");
        JsonObject json = getJsonObject(profileURL);
        if (json == null) { return null; }
        JsonElement element = json.get("name");
        if (element == null) { return null; }
        return element.getAsString();
    }
    
    public static List<Field> getClassFields(Object object, boolean searchSuperclasses) {
        List<Field> fields = new ArrayList<>(Arrays.asList(object.getClass().getDeclaredFields()));
        if (searchSuperclasses) { searchSuperClass(object.getClass(), fields); }
        return fields;
    }
}