package net.firecraftmc.api.model.player;

import net.firecraftmc.api.enums.*;
import net.firecraftmc.api.model.Transaction;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.toggles.Toggle;

import java.util.*;

public class FirecraftProfile {
    
    protected final UUID uniqueId;
    protected String name = "", fctPrefix = "", streamUrl = "";
    protected Rank mainRank;
    protected final List<Rank> subRanks = new ArrayList<>();
    protected Channel channel = Channel.GLOBAL;
    protected boolean online = false;
    protected long firstJoined = 0L, timePlayed = 0L, lastSeen = 0L;
    protected NickInfo nickInfo;
    protected VanishSettings vanishSettings = new VanishSettings();
    protected final List<UUID> friends = new ArrayList<>();
    protected final List<UUID> ignored = new ArrayList<>();
    protected final SortedSet<Integer> unseenReportActions = new TreeSet<>();
    protected FirecraftServer server;
    protected final SortedSet<Transaction> transactions = new TreeSet<>();
    protected Map<Toggle, Boolean> toggles = new HashMap<>();
    
    public FirecraftProfile(UUID uuid, Rank rank) {
        this.uniqueId = uuid;
        this.mainRank = rank;
        
        for (Toggle toggle : Toggle.TOGGLES) {
            this.toggles.put(toggle, toggle.getDefaultValue());
        }
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public String getName() {
        return name;
    }
    
    public Rank getMainRank() {
        return mainRank;
    }
    
    public Channel getChannel() {
        if (channel == null) channel = Channel.GLOBAL;
        return channel;
    }
    
    public boolean isOnline() {
        return online;
    }
    
    public long getFirstJoined() {
        return firstJoined;
    }
    
    public long getTimePlayed() {
        return timePlayed;
    }
    
    public long getLastSeen() {
        return lastSeen;
    }
    
    public NickInfo getNick() {
        return nickInfo;
    }
    
    public VanishSettings getVanishSettings() {
        return vanishSettings;
    }
    
    @Deprecated
    public boolean isVanished() {
        return this.getToggleValue(Toggle.VANISH);
    }
    
    public boolean isNicked() {
        return nickInfo != null;
    }
    
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    public void setOnline(boolean online) {
        this.online = online;
    }
    
    public void setFirstJoined(long firstJoined) {
        this.firstJoined = firstJoined;
    }
    
    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
    }
    
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public void setNickInfo(NickInfo nickInfo) {
        this.nickInfo = nickInfo;
    }
    
    public String getFctPrefix() {
        return fctPrefix;
    }
    
    public void setFctPrefix(String fctPrefix) {
        this.fctPrefix = fctPrefix;
    }
    
    public FirecraftServer getServer() {
        return server;
    }
    
    public void setServer(FirecraftServer server) {
        this.server = server;
    }
    
    public void addUnseenReportAction(int id) {
        this.unseenReportActions.add(id);
    }
    
    public SortedSet<Integer> getUnseenReportActions() {
        return unseenReportActions;
    }
    
    public void removeUnseenReportAction(int id) {
        this.unseenReportActions.remove(id);
    }
    
    public void addFriend(UUID friend) {
        this.friends.add(friend);
    }
    
    public void removeFriend(UUID friend) {
        this.friends.remove(friend);
    }
    
    public void addIgnored(UUID uuid) {
        this.ignored.add(uuid);
    }
    
    public void removeIgnored(UUID uuid) {
        this.ignored.remove(uuid);
    }
    
    public List<UUID> getIgnored() {
        return ignored;
    }
    
    public boolean isIgnoring(UUID uuid) {
        return ignored.contains(uuid);
    }
    
    public boolean hasRank(Rank... ranks) {
        if (ranks == null) return false;
        for (Rank rank : ranks) {
            if (mainRank.equals(rank) || subRanks.contains(rank)) {
                return true;
            }
        }
        return false;
    }
    
    public void addSubRank(Rank subRank) {
        if (!this.subRanks.contains(subRank)) {
            this.subRanks.add(subRank);
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getStreamUrl() {
        return streamUrl;
    }
    
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
    
    public Set<Transaction> getTransactions() {
        return transactions;
    }
    
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
    
    public void removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
    }
    
    public double getBalance() {
        double amount = 0;
        if (transactions == null || transactions.isEmpty()) return 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(TransactionType.DEPOSIT)) {
                amount += transaction.getAmount();
            } else {
                amount -= transaction.getAmount();
            }
        }
        
        return amount;
    }
    
    public Map<Toggle, Boolean> getToggles() {
        return this.toggles;
    }
    
    public boolean getToggleValue(Toggle toggle) {
        return this.toggles.get(toggle);
    }
    
    public void toggle(Toggle toggle) {
        this.toggles.put(toggle, !this.toggles.get(toggle));
    }
    
    public void setToggle(Toggle toggle, boolean value) {
        this.toggles.put(toggle, value);
    }
    
    public void setVanishSettings(VanishSettings vanish) {
        if (vanish != null) this.vanishSettings = vanish;
    }
}