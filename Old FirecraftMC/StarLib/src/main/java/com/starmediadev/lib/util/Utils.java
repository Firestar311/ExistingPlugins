package com.starmediadev.lib.util;

import com.google.gson.*;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.user.UserManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public final class Utils {
    private static final String profileUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    
    private Utils() {
    }
    
    public static String locationToString(Location location) {
        return "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }
    
    public static char getColorCode(String string) {
        for (char s : string.toCharArray()) {
            if (s != '&' && s != '§') {
                return s;
            }
        }
        
        return '0';
    }
    
    public static ArrayList<Block> getSurroundingBlocks(BlockFace blockFace, Block targetBlock) {
        ArrayList<Block> blocks = new ArrayList<>();
        World world = targetBlock.getWorld();
        blocks.add(targetBlock);
        
        int x, y, z;
        x = targetBlock.getX();
        y = targetBlock.getY();
        z = targetBlock.getZ();
        
        // Check the block face from which the block is being broken in order to get the correct surrounding blocks
        switch (blockFace) {
            case UP:
            case DOWN:
                blocks.add(world.getBlockAt(x + 1, y, z));
                blocks.add(world.getBlockAt(x - 1, y, z));
                blocks.add(world.getBlockAt(x, y, z + 1));
                blocks.add(world.getBlockAt(x, y, z - 1));
                blocks.add(world.getBlockAt(x + 1, y, z + 1));
                blocks.add(world.getBlockAt(x - 1, y, z - 1));
                blocks.add(world.getBlockAt(x + 1, y, z - 1));
                blocks.add(world.getBlockAt(x - 1, y, z + 1));
                break;
            case EAST:
            case WEST:
                blocks.add(world.getBlockAt(x, y, z + 1));
                blocks.add(world.getBlockAt(x, y, z - 1));
                blocks.add(world.getBlockAt(x, y + 1, z));
                blocks.add(world.getBlockAt(x, y - 1, z));
                blocks.add(world.getBlockAt(x, y + 1, z + 1));
                blocks.add(world.getBlockAt(x, y - 1, z - 1));
                blocks.add(world.getBlockAt(x, y - 1, z + 1));
                blocks.add(world.getBlockAt(x, y + 1, z - 1));
                break;
            case NORTH:
            case SOUTH:
                blocks.add(world.getBlockAt(x + 1, y, z));
                blocks.add(world.getBlockAt(x - 1, y, z));
                blocks.add(world.getBlockAt(x, y + 1, z));
                blocks.add(world.getBlockAt(x, y - 1, z));
                blocks.add(world.getBlockAt(x + 1, y + 1, z));
                blocks.add(world.getBlockAt(x - 1, y - 1, z));
                blocks.add(world.getBlockAt(x + 1, y - 1, z));
                blocks.add(world.getBlockAt(x - 1, y + 1, z));
                break;
            default:
                break;
        }
        
        // Trim the nulls from the list
        blocks.removeAll(Collections.singleton(null));
        return blocks;
    }
    
    public static boolean inventoryEmpty(Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return false;
            }
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
    
    public static List<String> convertUUIDListToStringList(Collection<UUID> uuidList) {
        List<String> uuidStrings = new ArrayList<>();
        for (UUID uuid : uuidList) {
            uuidStrings.add(uuid.toString());
        }
        return uuidStrings;
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
    
    public static String convertUUIDToName(UUID uuid, String nullText) {
        if (uuid == null) {
            return nullText;
        }
        
        RegisteredServiceProvider<UserManager> playerRsp = Bukkit.getServer().getServicesManager().getRegistration(UserManager.class);
        UserManager userManager = playerRsp.getProvider();
        if (userManager == null) {
            return nullText;
        }
        
        User user = userManager.getUser(uuid);
        if (user == null) {
            return nullText;
        }
        
        return user.getLastName();
    }
    
    public static void sendCenteredMessage(Player player, String message) {
        player.sendMessage(getCenteredMessage(message));
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
    
    public static boolean checkCmdAliases(String[] args, int index, String... aliases) {
        if (aliases == null) { return false; }
        for (String s : aliases) {
            try {
                if (args[index].equalsIgnoreCase(s)) { return true; }
            } catch (Exception e) {
                return false;
            }
        }
        
        return false;
    }
    
    public static List<String> wrapLore(int maxPerLine, String loreText) {
        if (StringUtils.isEmpty(loreText)) {
            return new ArrayList<>();
        }
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
    
    public static String blankLine(int length) {
        return blankLine("", length);
    }

//    public static void openBook(ItemStack book, Player p) {
//        int slot = p.getInventory().getHeldItemSlot();
//        ItemStack old = p.getInventory().getItem(slot);
//        p.getInventory().setItem(slot, book);
//
//        ByteBuf buf = Unpooled.buffer(256);
//        buf.setByte(0, (byte) 0);
//        buf.writerIndex(1);
//
//        try {
//            Constructor<?> serializerConstructor = ReflectionUtils.getNMSClass("PacketDataSerializer").getConstructor(ByteBuf.class);
//            Object packetDataSerializer = serializerConstructor.newInstance(buf);
//
//            Constructor<?> keyConstructor = ReflectionUtils.getNMSClass("MinecraftKey").getConstructor(String.class);
//            Object bookKey = keyConstructor.newInstance("minecraft:book_open");
//
//            Constructor<?> titleConstructor = ReflectionUtils.getNMSClass("PacketPlayOutCustomPayload").getConstructor(bookKey.getClass(), ReflectionUtils.getNMSClass("PacketDataSerializer"));
//            Object payload = titleConstructor.newInstance(bookKey, packetDataSerializer);
//
//            ReflectionUtils.sendPacket(payload, p.getPlayer());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        p.getInventory().setItem(slot, old);
//    }
    
    public static String blankLine(String color, int length) {
        String sb = color + "&l&m" + " ".repeat(Math.max(0, length));
        return Utils.color(sb);
    }
    
    public static boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    
    private static void searchSuperClass(Class<?> clazz, List<Field> fields) {
        if (clazz.getSuperclass() != null) {
            Class<?> superClass = clazz.getSuperclass();
            searchSuperClass(superClass, fields);
        }
        
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    }
    
    public static Pair<Long, Long> getDayStartEnd(short[] dateValues) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate today = LocalDate.of(dateValues[2], dateValues[0], dateValues[1]);
        
        ZonedDateTime zdtStart = today.atStartOfDay(zoneId);
        ZonedDateTime zdtStop = today.plusDays(1).atStartOfDay(zoneId);
        
        Instant start = zdtStart.toInstant();
        Instant stop = zdtStop.toInstant();
        
        long startSeconds = start.getEpochSecond();
        long endSeconds = stop.getEpochSecond();
        return new Pair<>(startSeconds, endSeconds);
    }
    
    public static Player getTargetPlayer(final Player player) {
        return getTarget(player, player.getWorld().getPlayers());
    }

//    public static String getCallerCallerClassName() {
//        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
//        String callerClassName = null;
//        for (int i = 1; i < stElements.length; i++) {
//            StackTraceElement ste = stElements[i];
//            if (!ste.getClassName().equals(Utils.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
//                if (callerClassName == null) {
//                    callerClassName = ste.getClassName();
//                } else if (!callerClassName.equals(ste.getClassName())) {
//                    return ste.getClassName();
//                }
//            }
//        }
//        return null;
//    }

//    public static String getCallingPlugin() {
//        for (StackTraceElement s : Thread.currentThread().getStackTrace()) {
//            String className = s.getClassName();
//            System.out.println(className);
//            try {
//                Class<?> clazz = Class.forName(className);
//                if (JavaPlugin.class.isAssignableFrom(clazz)) {
//                    return clazz.getSimpleName();
//                }
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }
    
    public static <T extends Entity> T getTarget(final Entity entity, final Iterable<T> entities) {
        if (entity == null) { return null; }
        T target = null;
        final double threshold = 1;
        for (final T other : entities) {
            final Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0) {
                if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation().distanceSquared(entity.getLocation())) {
                    target = other;
                }
            }
        }
        return target;
    }
    
    public static Entity getTargetEntity(final Entity entity) {
        return getTarget(entity, entity.getWorld().getEntities());
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
    
    public static List<String> getResults(String arg, List<String> possibleResults) {
        List<String> results = new ArrayList<>();
        if (!possibleResults.isEmpty()) {
            if (!StringUtils.isEmpty(arg)) {
                for (String r : possibleResults) {
                    if (r.toLowerCase().startsWith(arg.toLowerCase())) {
                        results.add(r);
                    }
                }
            } else {
                results.addAll(possibleResults);
            }
        }
        return results;
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
        
        return new ArrayList<>(Arrays.asList(xPos.getChunk(), xNeg.getChunk(), zPos.getChunk(), zNeg.getChunk(), xpzp.getChunk(), xpzn.getChunk(), xnzn.getChunk(), xnzp.getChunk()));
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
    
    public static JsonObject getJsonObject(String urlString) {
        try {
            StringBuilder buffer = getJsonBuffer(urlString);
            return (JsonObject) new JsonParser().parse(buffer.toString());
        } catch (Exception e) {
        }
        
        return null;
    }
    
    private static UUID formatUUID(String uuidString) {
        String finalUUIDString = uuidString.substring(0, 8) + "-";
        finalUUIDString += uuidString.substring(8, 12) + "-";
        finalUUIDString += uuidString.substring(12, 16) + "-";
        finalUUIDString += uuidString.substring(16, 20) + "-";
        finalUUIDString += uuidString.substring(20, 32);
        return UUID.fromString(finalUUIDString);
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
