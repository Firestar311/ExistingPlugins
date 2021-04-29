package net.firecraftmc.maniacore.spigot.user;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.spigot.events.UserActionBarUpdateEvent;
import net.firecraftmc.maniacore.spigot.events.UserJoinEvent;
import net.firecraftmc.maniacore.spigot.updater.UpdateEvent;
import net.firecraftmc.maniacore.spigot.updater.UpdateType;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.chat.ChatHandler;
import net.firecraftmc.maniacore.api.logging.entry.ChatEntry;
import net.firecraftmc.maniacore.api.logging.entry.CmdEntry;
import net.firecraftmc.maniacore.api.nickname.Nickname;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.records.ChatEntryRecord;
import net.firecraftmc.maniacore.api.records.CmdEntryRecord;
import net.firecraftmc.maniacore.api.records.NicknameRecord;
import net.firecraftmc.maniacore.api.records.UserRecord;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.UserManager;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.api.util.ReflectionUtils;
import net.firecraftmc.maniacore.plugin.CenturionsPlugin;
import net.firecraftmc.manialib.sql.IRecord;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Constructor;
import java.util.*;

public class SpigotUserManager extends UserManager implements Listener {

    private CenturionsPlugin plugin;

    @Getter private Map<UUID, User> users = new HashMap<>();

    public SpigotUserManager(CenturionsPlugin plugin) {
        this.plugin = plugin;

        plugin.runTaskTimer(() -> {
            Map<UUID, User> users = new HashMap<>(SpigotUserManager.this.users);
            Set<UUID> offlinePlayers = new HashSet<>();
            for (User value : users.values()) {
                if (!value.isOnline()) {
                    Redis.pushUser(value);
                    Redis.sendCommand("saveUserData " + value.getUniqueId());
                    offlinePlayers.add(value.getUniqueId());
                }
            }

            for (UUID offlinePlayer : offlinePlayers) {
                SpigotUserManager.this.users.remove(offlinePlayer);
            }

            net.firecraftmc.maniacore.spigot.events.UserActionBarUpdateEvent event = new UserActionBarUpdateEvent();
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                for (User value : SpigotUserManager.this.users.values()) {
                    if (value.generateActionBar() == null || value.generateActionBar().equals("")) {
                        continue;
                    }
                    Player player = Bukkit.getPlayer(value.getUniqueId());
                    String message = CenturionsUtils.color(value.generateActionBar());
                    String jsonText = "{\"text\":\"" + message + "\"}";

                    try {
                        Class<?> chatSerializer = ReflectionUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
                        Object chat = chatSerializer.getMethod("a", String.class).invoke(null, jsonText);
                        Constructor<?> chatConstructor = ReflectionUtils.getNMSClass("PacketPlayOutChat")
                                .getConstructor(ReflectionUtils.getNMSClass("IChatBaseComponent"), byte.class);
                        Object packetPlayOutChat = chatConstructor.newInstance(chat, (byte) 2);
                        Object handle = player.getClass().getMethod("getHandle").invoke(player);
                        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                        playerConnection.getClass().getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet")).invoke(playerConnection, packetPlayOutChat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 20L, 20L);
    }

    @SuppressWarnings("DuplicatedCode")
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        net.firecraftmc.maniacore.spigot.user.SpigotUser user = (net.firecraftmc.maniacore.spigot.user.SpigotUser) getUser(player.getUniqueId());
        e.setCancelled(true);

        if (e.getMessage().startsWith("@") || e.getMessage().startsWith("$")) {
            char channelChar = e.getMessage().charAt(0);
            e.setCancelled(true);
            e.setMessage(e.getMessage().substring(1));
            Channel channel = Channel.GLOBAL;

            if (channelChar == '@') {
                channel = Channel.STAFF;
            } else if (channelChar == '$') {
                channel = Channel.ADMIN;
            }

            String message = e.getMessage();
            user.setChannel(channel);
            if ((channel == Channel.STAFF && user.hasPermission(Rank.HELPER)) || (channel == Channel.ADMIN && user.hasPermission(Rank.ADMIN))) {
                CenturionsCore.getInstance().getMessageHandler().sendChannelMessage(player.getUniqueId(), channel, message);
            }
            user.setChannel(Channel.GLOBAL);
            return;
        }

        ChatHandler handler = CenturionsCore.getInstance().getChatManager().getHandler();
        handler.sendChatMessage(e.getPlayer().getUniqueId(), Channel.GLOBAL, e.getMessage());

        ChatEntry chatEntry = new ChatEntry(System.currentTimeMillis(), CenturionsCore.getInstance().getServerManager().getCurrentServer().getName(), user.getId(), e.getMessage().replace("'", "\\'"), user.getChannel().name());
        ChatEntryRecord chatEntryRecord = new ChatEntryRecord(chatEntry);
        CenturionsCore.getInstance().getDatabase().addRecordToQueue(chatEntryRecord);
    }

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        User user = this.users.get(player.getUniqueId());
        CmdEntry cmdEntry = new CmdEntry(System.currentTimeMillis(), "test", user.getId(), e.getMessage());
        CmdEntryRecord cmdEntryRecord = new CmdEntryRecord(cmdEntry);
        CenturionsCore.getInstance().getDatabase().addRecordToQueue(cmdEntryRecord);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        User user = this.users.get(e.getPlayer().getUniqueId());
        Redis.pushUser(user);
        this.users.remove(user.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        Player player = e.getPlayer();
        GameProfile gameProfile = ((CraftPlayer) player).getProfile();
        String value = null, signature = null;
        for (Property property : gameProfile.getProperties().get("textures")) {
            value = property.getValue();
            signature = property.getSignature();
        }

        Skin skin = new Skin(player.getUniqueId(), player.getName(), value, signature);
        CenturionsCore.getInstance().getSkinManager().addSkin(skin);

        plugin.runTaskLaterAsynchronously(() -> {
            User user = getUser(player.getUniqueId());

            plugin.runTaskLater(() -> {
                users.put(user.getUniqueId(), user);
                if (user.getToggle(Toggles.INCOGNITO).getAsBoolean()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!p.hasPermission("mania.incognito.see")) {
                            p.hidePlayer(player);
                        }
                    }
                }
                if (user.hasPermission(Rank.OWNER)) {
                    if (!e.getPlayer().isOp()) {
                        e.getPlayer().setOp(true);
                    }
                }

                if (user.hasPermission(Rank.HELPER)) {
                    if (!e.getPlayer().isWhitelisted()) {
                        e.getPlayer().setWhitelisted(true);
                    }
                }

                List<IRecord> nickRecords = CenturionsCore.getInstance().getDatabase().getRecords(NicknameRecord.class, "player", user.getUniqueId().toString());
                if (nickRecords.size() >= 1) {
                    plugin.runTask(() -> {
                        user.setNickname((Nickname) nickRecords.get(0).toObject());
                        user.applyNickname();
                    });
                }
                
                if (!user.getName().equals(player.getName())) {
                    user.setName(player.getName());
                    new UserRecord(user).push(CenturionsCore.getInstance().getDatabase());
                }

                net.firecraftmc.maniacore.spigot.events.UserJoinEvent event = new UserJoinEvent((net.firecraftmc.maniacore.spigot.user.SpigotUser) user);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }, 1L);
        }, 5L);
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (e.getType() != UpdateType.SECOND) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = getUser(player.getUniqueId());
            user.incrementOnlineTime();
        }
    }

    public User constructUser(UUID uuid, String name) {
        return new net.firecraftmc.maniacore.spigot.user.SpigotUser(uuid, name);
    }

    public User constructUser(Map<String, String> data) {
        return new net.firecraftmc.maniacore.spigot.user.SpigotUser(data);
    }

    public User constructUser(User user) {
        return new net.firecraftmc.maniacore.spigot.user.SpigotUser(user);
    }

    public User getUser(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        if (this.users.containsKey(uuid)) {
            return this.users.get(uuid);
        }

        User user = super.getUser(uuid);
        ((SpigotUser) user).loadPerks();
        this.users.put(user.getUniqueId(), user);
        return user;
    }

    public User getUser(int userId) {
        for (User value : this.users.values()) {
            if (value.getId() == userId) {
                return value;
            }
        }
        User user = super.getUser(userId);
        this.users.put(user.getUniqueId(), user);
        return user;
    }

    public User getUser(String name) {
        for (User value : this.users.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        User user = super.getUser(name);
        this.users.put(user.getUniqueId(), user);
        return user;
    }
}