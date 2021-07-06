package net.firecraftmc.api.model.player;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.enums.Channel;
import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.exceptions.NicknameException;
import net.firecraftmc.api.model.Transaction;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.plugin.IFirecraftCore;
import net.firecraftmc.api.toggles.Toggle;
import net.firecraftmc.api.util.Messages;
import net.firecraftmc.api.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * The Class that represents the FirecraftPlayer profile and stores information about them in memory
 */
public class FirecraftPlayer {
    private final FirecraftProfile profile;

    private Skin skin;
    private final List<Home> homes = new ArrayList<>();
    private UUID lastMessage;

    private Player player;
    private ActionBar actionBar;
    private FirecraftScoreboard scoreboard;
    private boolean afk = false;

    public FirecraftPlayer(FirecraftProfile profile) {
        this.profile = profile;
    }

    public void loadPlayer() {
        updatePlayer();
        if (FirecraftAPI.isCore()) {
            IFirecraftCore plugin = FirecraftAPI.getFirecraftCore();
            scoreboard = new FirecraftScoreboard(this, FirecraftAPI.getFirecraftCore().getFCServer());
            scoreboard.sendScoreboard(player);
            
            if (getMainRank().equals(Rank.FIRECRAFT_TEAM)) {
                String prefix = FirecraftAPI.getDatabase().getFTPrefix(player.getUniqueId());
                if (prefix != null) this.setFctPrefix(prefix);
            }
    
            if (FirecraftAPI.getDatabase().hasActiveJail(player.getUniqueId())) {
                player.teleport(FirecraftAPI.getFirecraftCore().getJailLocation());
            }
    
            if (FirecraftAPI.getDatabase().hasUnacknowledgedWarnings(player.getUniqueId())) {
                String code = Utils.generateAckCode(Utils.codeCharacters);
                FirecraftAPI.getFirecraftCore().addAckCode(player.getUniqueId(), code);
                player.sendMessage(Messages.joinUnAckWarning(code));
            }
    
            setHomes(FirecraftAPI.getFirecraftCore().getHomeManager().loadHomes(player.getUniqueId()));
    
            if (getFirstJoined() == 0) {
                setFirstJoined(System.currentTimeMillis());
            }
            setLastSeen(System.currentTimeMillis());
    
            List<Transaction> transactions = FirecraftAPI.getDatabase().getTransactions(player.getUniqueId());
            transactions.forEach(transaction -> getProfile().addTransaction(transaction));
            
            Bukkit.getScheduler().runTaskLater(FirecraftAPI.getFirecraftCore(), () -> {
                NickInfo nick = plugin.getFCDatabase().getNickname(player.getUniqueId());
                if (nick != null) {
                    nick.getProfile().setSkin(plugin.getFCDatabase().getSkin(nick.getProfile().getUniqueId()));
                    setNick(FirecraftAPI.getFirecraftCore(), nick.getProfile(), nick.getRank());
                }
    
                if (profile.getToggleValue(Toggle.VANISH)) {
                    vanish();
                    for (FirecraftPlayer p : plugin.getPlayerManager().getPlayers()) {
                        if (!isNicked()) {
                            player.getPlayer().setPlayerListName(player.getName() + " §7§l[V]");
                        } else {
                            player.getPlayer().setPlayerListName(getNick().getProfile().getName() + "§7§l[V]");
                        }
            
                        if (!p.getMainRank().isEqualToOrHigher(getMainRank())) {
                            p.getPlayer().hidePlayer(player.getPlayer());
                        }
                    }
        
                    setActionBar(new ActionBar(Messages.actionBar_Vanished));
                    getVanishSettings().setAllowFlightBeforeVanish(player.getPlayer().getAllowFlight());
                    player.getPlayer().setAllowFlight(true);
                }
                updatePlayerListName();
    
                for (FirecraftPlayer p : plugin.getPlayerManager().getPlayers()) {
                    if (p.isVanished()) {
                        if (!getMainRank().isEqualToOrHigher(p.getMainRank())) {
                            player.getPlayer().hidePlayer(p.getPlayer());
                        }
                    }
                }
                getScoreboard().updateScoreboard(this);
    
                List<Mail> mail = plugin.getFCDatabase().getMailByReceiver(player.getUniqueId());
                int unreadAmount = 0;
                for (Mail m : mail) {
                    if (!m.isRead()) unreadAmount++;
                }
    
                if (unreadAmount != 0) {
                    player.sendMessage("<nc>You have <vc>" + unreadAmount + " <nc>unread mail messages.");
                }
            }, 20L);
        }
    }

    public void updatePlayerListName() {
        updatePlayer();
        if (!profile.getToggleValue(Toggle.RECORDING)) {
            if (profile.isNicked()) {
                if (player != null) {
                    player.setPlayerListName(profile.getNick().getRank().getBaseColor() + profile.getNick().getProfile().getName());
                }
            } else {
                if (player != null)
                    player.setPlayerListName(getNameNoPrefix());
            }
        } else {
            player.setPlayerListName(Rank.DEFAULT.getBaseColor() + getName());
        }
    }

    public void kickPlayer(String message) {
        player.kickPlayer(Utils.color(message));
    }

    public String getDisplayName() {
        updatePlayer();
        String displayName = generateDisplayName();
        setDisplayName(displayName);
        return displayName;
    }

    public void setDisplayName(String name) {
        if (player != null) player.setDisplayName(name);
    }

    public String getNameNoPrefix() {
        updatePlayer();
        String name = profile.getMainRank().getBaseColor();
        if (Rank.isStaff(profile.getMainRank())) {
            name += ChatColor.BOLD;
            if (profile.getMainRank().equals(Rank.FIRECRAFT_TEAM)) {
                name += "FT ";
            } else if (profile.getMainRank().equals(Rank.HEAD_ADMIN)) {
                name += "HA ";
            } else if (profile.getMainRank().equals(Rank.TRIAL_ADMIN)) {
                name += "TA ";
            } else if (profile.getMainRank().equals(Rank.ADMIN)) {
                name += "A ";
            } else if (profile.getMainRank().equals(Rank.MOD)) {
                name += "M ";
            } else if (profile.getMainRank().equals(Rank.TRIAL_MOD)) {
                name += "TM ";
            }
        }
        name += profile.getName();
        return name;
    }

    public String generateDisplayName() {
        updatePlayer();
        if (profile.nickInfo != null) {
            if (!profile.channel.equals(Channel.STAFF)) {
                NickInfo nick = profile.nickInfo;
                if (Rank.isStaff(nick.getRank())) {
                    return nick.getRank().getPrefix() + " " + nick.getProfile().getName();
                } else {
                    return nick.getRank().equals(Rank.DEFAULT) ? nick.getRank().getPrefix() + nick.getRank().getBaseColor() + nick.getProfile().getName() : nick.getRank().getPrefix() + nick.getRank().getBaseColor() + " " + nick.getProfile().getName();
                }
            }
        }

        if (profile.getToggleValue(Toggle.RECORDING)) {
            return Rank.DEFAULT.getBaseColor() + profile.name;
        }

        if (profile.mainRank.equals(Rank.FIRECRAFT_TEAM) && !profile.fctPrefix.equals("")) {
            return profile.fctPrefix + " " + profile.name;
        }

        if (Rank.isStaff(profile.mainRank)) {
            return profile.mainRank.getPrefix() + " " + profile.name;
        } else {
            return profile.mainRank.equals(Rank.DEFAULT) ? profile.mainRank.getBaseColor() + profile.name : profile.mainRank.getPrefix() + profile.mainRank.getBaseColor() + " " + profile.name;
        }
    }

    public void setMainRank(Rank mainRank) {
        this.profile.mainRank = mainRank;
        if (this.scoreboard != null) {
            if (!this.profile.mainRank.equals(Rank.DEFAULT)) {
                this.scoreboard.updateScoreboard(this);
            } else {
                this.scoreboard.updateScoreboard(this);
            }
        }
    }

    public void sendMessage(String message) {
        updatePlayer();
        if (player != null) {
            message = message.replace("<nc>", Messages.NORMAL_COLOR);
            message = message.replace("<vc>", Messages.VALUE_COLOR);
            message = message.replace("<ec>", Messages.ERROR_COLOR);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    private void updatePlayer() {
        if (this.player == null)
            this.player = Bukkit.getServer().getPlayer(profile.uniqueId);
        this.profile.online = player != null;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGameMode(GameMode mode) {
        updatePlayer();
        if (this.player != null) {
            player.setGameMode(mode);
        }
    }

    public void teleport(Location location) {
        updatePlayer();
        if (player != null) {
            player.teleport(location);
        }
    }

    public Location getLocation() {
        updatePlayer();
        if (player != null) {
            return player.getLocation();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirecraftPlayer that = (FirecraftPlayer) o;
        return Objects.equals(profile.uniqueId, that.profile.uniqueId);
    }

    public int hashCode() {
        return Objects.hash(profile.uniqueId);
    }

    public Skin getSkin() {
        return skin;
    }

    public void vanish() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        profile.setToggle(Toggle.VANISH, true);
        //player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        this.scoreboard.updateScoreboard(this);
        updatePlayerListName();
    }

    public void unVanish() {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        profile.setToggle(Toggle.VANISH, false);
        //player.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.scoreboard.updateScoreboard(this);
        updatePlayerListName();
    }

    public Player getPlayer() {
        updatePlayer();
        return player;
    }

    public void setNick(final IFirecraftCore plugin, final FirecraftPlayer nick, final Rank rank) {
        updatePlayer();
        try {
            this.profile.nickInfo = plugin.getNickWrapper().setNick(plugin, this, nick);
            this.profile.nickInfo.setRank(rank);
            this.scoreboard.updateScoreboard(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NicknameException("Error in setting nickname");
        }
    }

    public void resetNick(final IFirecraftCore plugin) {
        updatePlayer();
        if (player == null) return;
        try {
            plugin.getNickWrapper().setNick(plugin, this, this);
            this.profile.nickInfo = null;
            this.scoreboard.updateScoreboard(this);
            this.actionBar = null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new NicknameException("Could not reset nickname.");
        }
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public void setActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
    }

    public FirecraftScoreboard getScoreboard() {
        return scoreboard;
    }

    public void refreshOnlineStatus() {
        updatePlayer();
        profile.online = player != null;
        updatePlayerListName();
    }

    public PlayerInventory getInventory() {
        return player.getInventory();
    }

    public Home getHome(String name) {
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public Home getHome(Location location) {
        for (Home home : homes) {
            if (home.getLocation().equals(location)) {
                return home;
            }
        }
        return null;
    }

    public void addHome(Home home) {
        this.homes.remove(home);
        this.homes.add(home);
    }

    public void setHomes(List<Home> homes) {
        this.homes.addAll(homes);
    }

    public void removeHome(Home home) {
        this.homes.remove(home);
    }

    public List<Home> getHomes() {
        return homes;
    }
    
    public String getHomeListAsString() {
        StringBuilder sb = new StringBuilder();
        if (this.homes.isEmpty()) {
            sb.append("none");
        } else {
            for (int i=0; i<homes.size(); i++) {
                Home home = homes.get(i);
                sb.append(home.getName());
                if (i != homes.size()-1) {
                    sb.append(", ");
                }
            }
        }
        
        return sb.toString();
    }

    public UUID getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(UUID lastMessage) {
        this.lastMessage = lastMessage;
    }

    public UUID getUniqueId() {
        return profile.getUniqueId();
    }

    public String getName() {
        return profile.getName();
    }

    public Rank getMainRank() {
        return profile.getMainRank();
    }

    public Channel getChannel() {
        return profile.getChannel();
    }

    public boolean isNicked() {
        return profile.isNicked();
    }

    public NickInfo getNick() {
        return profile.getNick();
    }

    public boolean isVanished() {
        return profile.isVanished();
    }

    public VanishSettings getVanishSettings() {
        return profile.getVanishSettings();
    }

    public long getFirstJoined() {
        return profile.getFirstJoined();
    }

    public boolean isOnline() {
        return profile.isOnline();
    }

    public long getTimePlayed() {
        return profile.getTimePlayed();
    }

    public long getLastSeen() {
        return profile.getLastSeen();
    }

    public void setChannel(Channel channel) {
        profile.setChannel(channel);
        this.scoreboard.updateScoreboard(this);
    }

    public void setOnline(boolean online) {
        profile.setOnline(online);
    }

    public void setFirstJoined(long firstJoined) {
        profile.setFirstJoined(firstJoined);
    }

    public void setTimePlayed(long timePlayed) {
        profile.setTimePlayed(timePlayed);
    }

    public void setLastSeen(long lastSeen) {
        profile.setLastSeen(lastSeen);
    }

    public void setNickInfo(NickInfo nickInfo) {
        profile.setNickInfo(nickInfo);
    }

    public String getFctPrefix() {
        return profile.getFctPrefix();
    }

    public void setFctPrefix(String fctPrefix) {
        profile.setFctPrefix(fctPrefix);
    }

    public FirecraftServer getServer() {
        return profile.getServer();
    }

    public void setServer(FirecraftServer server) {
        profile.setServer(server);
    }

    public void addUnseenReportAction(int id) {
        profile.addUnseenReportAction(id);
    }

    public SortedSet<Integer> getUnseenReportActions() {
        return profile.getUnseenReportActions();
    }

    public void removeUnseenReportAction(int id) {
        profile.removeUnseenReportAction(id);
    }

    public void addFriend(UUID friend) {
        profile.addFriend(friend);
    }

    public void removeFriend(UUID friend) {
        profile.removeFriend(friend);
    }

    public void addIgnored(UUID uuid) {
        profile.addIgnored(uuid);
    }

    public void removeIgnored(UUID uuid) {
        profile.removeIgnored(uuid);
    }

    public List<UUID> getIgnored() {
        return profile.getIgnored();
    }

    public boolean isIgnoring(UUID uuid) {
        return profile.isIgnoring(uuid);
    }

    public boolean hasRank(Rank... ranks) {
        return profile.hasRank(ranks);
    }

    public void addSubRank(Rank subRank) {
        profile.addSubRank(subRank);
    }

    public void setName(String name) {
        profile.setName(name);
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public boolean getAllowFlight() {
        updatePlayer();
        return player.getAllowFlight();
    }

    public void setAllowFlight(boolean flight) {
        updatePlayer();
        player.setAllowFlight(flight);
    }

    public GameMode getGameMode() {
        return player.getGameMode();
    }

    public String getStreamUrl() {
        return profile.getStreamUrl();
    }

    public void setStreamUrl(String url) {
        this.profile.setStreamUrl(url);
    }

    public FirecraftProfile getProfile() {
        return profile;
    }

    public double getBalance() {
        return profile.getBalance();
    }
    
    public Map<Toggle, Boolean> getToggles() {
        return this.profile.getToggles();
    }
    
    public boolean getToggleValue(Toggle toggle) {
        return this.profile.getToggleValue(toggle);
    }
    
    public void toggle(Toggle toggle) {
        this.profile.toggle(toggle);
    }
    
    public void setToggle(Toggle toggle, boolean value) {
        this.profile.setToggle(toggle, value);
    }
    
    public boolean isAfk() {
        return afk;
    }
    
    public void setAfk(boolean afk) {
        this.afk = afk;
    }
}