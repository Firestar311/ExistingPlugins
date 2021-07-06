package com.kingrealms.realms.profile;

import com.google.common.collect.Sets;
import com.kingrealms.realms.IOwner;
import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.Channel;
import com.kingrealms.realms.channel.ChannelManager;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.chat.NameFormat;
import com.kingrealms.realms.chat.groups.Group;
import com.kingrealms.realms.chat.prefixes.Prefix;
import com.kingrealms.realms.economy.account.Account;
import com.kingrealms.realms.home.*;
import com.kingrealms.realms.kits.Kit;
import com.kingrealms.realms.kits.KitUseInfo;
import com.kingrealms.realms.limits.LimitBoost;
import com.kingrealms.realms.limits.limit.Limit;
import com.kingrealms.realms.profile.board.RealmsBoard;
import com.kingrealms.realms.questing.lines.QuestLine;
import com.kingrealms.realms.questing.quests.Quest;
import com.kingrealms.realms.questing.tasks.Task;
import com.kingrealms.realms.questing.tasks.TaskProgress;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.staffmode.StaffMode;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.util.Constants;
import com.kingrealms.realms.warps.type.Warp;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.VaultIntegration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

@SerializableAs("RealmProfile")
public class RealmProfile implements ConfigurationSerializable, IOwner {
    
    public static final Set<String> ADMIN_GROUPS = Set.of("owner", "manager", "headadmin", "admin", "trialadmin");
    
    protected Account account; //Cached value
    protected long accountNumber; //mysql
    protected Set<ID> activeQuestLines = new HashSet<>(); //myusql
    protected Channel channelFocus; //Cached value
    protected int channelFocusId = ChannelManager.GLOBAL_ID; //mysql
    protected Set<ID> completedQuests = new HashSet<>(); //mysql 
    protected RealmsBoard displayBoard; //Generated on load
    protected Set<Home> homes = new HashSet<>(); //mysql
    protected EnumMap<EntityType, Integer> killedMobs = new EnumMap<>(EntityType.class); //Mysql
    protected Map<Integer, KitUseInfo> kitUses = new HashMap<>(); //mysql
    protected String lastMessage; //mysql
    protected Map<String, LimitBoost> limitBoosts = new HashMap<>(); //mysql
    protected EnumMap<Material, Integer> minedBlocks = new EnumMap<>(Material.class); //mysql
    protected EnumMap<Material, Integer> placedBlocks = new EnumMap<>(Material.class); //mysql
    protected ID questGuiDefault; //mysql
    protected Map<ID, Set<ID>> questProgress = new HashMap<>();
    protected EnumMap<SkillType, Double> skillExperience = new EnumMap<>(SkillType.class);
    protected StaffMode staffmode; //mysql
    protected Map<ID, TaskProgress> taskProgress = new HashMap<>(); //mysql
    protected User user; //mysql
    protected Mailbox mailbox;
    protected SocialSpy socialSpy;
    protected String prefixId;
    protected Prefix prefix;
    protected String customColor;
    
    public RealmProfile(User user) {
        this.user = user;
        this.mailbox = new Mailbox(user.getUniqueId());
    }
    
    public RealmProfile(Map<String, Object> serialized) {
        user = Realms.getInstance().getUserManager().getUser(UUID.fromString((String) serialized.get("uuid")));
        for (SkillType type : SkillType.values()) {
            if (serialized.containsKey(type.name())) {
                skillExperience.put(type, Double.parseDouble((String) serialized.get(type.name())));
            }
        }
        
        if (serialized.containsKey("accountNumber")) {
            accountNumber = Long.parseLong((String) serialized.get("accountNumber"));
        }
        
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().startsWith("minedblock-")) {
                Material material = Material.valueOf(entry.getKey().split("-")[1]);
                int value = Integer.parseInt((String) entry.getValue());
                setMinedBlocks(material, value);
            }
            if (entry.getKey().startsWith("placedblock-")) {
                Material material = Material.valueOf(entry.getKey().split("-")[1]);
                int value = Integer.parseInt((String) entry.getValue());
                setPlacedBlocks(material, value);
            }
            if (entry.getKey().startsWith("killedmob-")) {
                EntityType entityType = EntityType.valueOf(entry.getKey().split("-")[1]);
                int value = Integer.parseInt((String) entry.getValue());
                setKilledMobs(entityType, value);
            }
            if (entry.getKey().startsWith("home-")) {
                Home home = (Home) entry.getValue();
                addHome(home);
            }
            if (entry.getKey().startsWith("limitboost-")) {
                LimitBoost limitBoost = (LimitBoost) entry.getValue();
                limitBoosts.put(limitBoost.getLimitId(), limitBoost);
            }
            if (entry.getKey().startsWith("kituse-")) {
                KitUseInfo kitUseInfo = (KitUseInfo) entry.getValue();
                kitUses.put(kitUseInfo.getKitId(), kitUseInfo);
            }
            if (entry.getKey().startsWith("questProgress-")) {
                String qi = entry.getKey().split("-")[1];
                List<String> taskStrings = (List<String>) entry.getValue();
                
                ID questId = new ID(qi);
                List<ID> taskIds = new ArrayList<>();
                for (String t : taskStrings) {
                    taskIds.add(new ID(t));
                }
                questProgress.put(questId, new HashSet<>(taskIds));
            }
            if (entry.getKey().startsWith("taskProgress-")) {
                ID id = new ID(entry.getKey().split("-")[1]);
                TaskProgress taskProgress = (TaskProgress) entry.getValue();
                this.taskProgress.put(id, taskProgress);
            }
        }
        
        if (serialized.containsKey("channelFocus")) {
            channelFocusId = Integer.parseInt((String) serialized.get("channelFocus"));
        }
        
        if (serialized.containsKey("staffmode")) {
            staffmode = (StaffMode) serialized.get("staffmode");
        }
        
        if (serialized.containsKey("completedQuests")) {
            List<String> cQ = (List<String>) serialized.get("completedQuests");
            for (String q : cQ) {
                completedQuests.add(new ID(q));
            }
        }
        
        if (serialized.containsKey("activeQuestLines")) {
            List<String> aQL = (List<String>) serialized.get("activeQuestLines");
            for (String q : aQL) {
                activeQuestLines.add(new ID(q));
            }
        }
        
        if (serialized.containsKey("questGuiDefault")) {
            questGuiDefault = (ID) serialized.get("questGuiDefault");
        }
        
        if (serialized.containsKey("mailbox")) {
            this.mailbox = (Mailbox) serialized.get("mailbox");
        }
        
        if (serialized.containsKey("socialspy")) {
            this.socialSpy = (SocialSpy) serialized.get("socialspy");
        }
        
        if (serialized.containsKey("prefix")) {
            this.prefixId = (String) serialized.get("serialized");
        }
        
        if (serialized.containsKey("customColor")) {
            this.customColor = (String) serialized.get("customColor");
        }
    }
    
    public void setMinedBlocks(Material material, int value) {
        this.minedBlocks.put(material, value);
    }
    
    public void setPlacedBlocks(Material material, int value) {
        this.placedBlocks.put(material, value);
    }
    
    public void setKilledMobs(EntityType type, int value) {
        this.killedMobs.put(type, value);
    }
    
    public void addHome(Home home) {
        this.homes.add(home);
    }
    
    public Map<ID, TaskProgress> getTaskProgress() {
        return taskProgress;
    }
    
    public TaskProgress getTaskProgress(ID taskId) {
        return this.taskProgress.get(taskId);
    }
    
    public void addActiveQuestLine(QuestLine questLine) {
        this.activeQuestLines.add(questLine.getId());
    }
    
    public void addCompletedTask(Quest quest, Task task) {
        if (questProgress.containsKey(quest.getId())) {
            questProgress.get(quest.getId()).add(task.getId());
        } else {
            questProgress.put(quest.getId(), Sets.newHashSet(task.getId())); //Might need to wrap this in hashset constructor call
        }
    }
    
    public Set<ID> getCompletedTasks(Quest quest) {
        if (this.questProgress.containsKey(quest.getId())) {
            return questProgress.get(quest.getId());
        } else {
            return new HashSet<>();
        }
    }
    
    public void addMinedBlock(Material material) {
        if (this.minedBlocks.containsKey(material)) {
            this.minedBlocks.put(material, this.minedBlocks.get(material) + 1);
        } else {
            this.minedBlocks.put(material, 1);
        }
    }
    
    public void setCustomColor(String customColor) {
        this.customColor = customColor;
    }
    
    public ChatColor getCustomColor() {
        return ChatColor.of(customColor);
    }
    
    //    public void updateDisplayName() {
//        Group group = getGroup();
//        Prefix prefix = getPrefix();
//        if (prefix == null) {
//            prefix = group.getPrefix();
//        }
//    
//        TextComponent prefixComponent = new TextComponent(prefix.getText());
//        prefixComponent.setColor(ChatColor.of(prefix.getColor()));
//        prefixComponent.setBold(prefix.isBold());
//    
//        NameFormat nameFormat = group.getNameFormat();
//    }
    
    public void addPlacedBlock(Material material) {
        if (this.placedBlocks.containsKey(material)) {
            this.placedBlocks.put(material, this.placedBlocks.get(material) + 1);
        } else {
            this.placedBlocks.put(material, 1);
        }
    }
    
    public void addKilledMob(EntityType killedMob) {
        if (!killedMob.isAlive()) {
            return;
        }
        
        if (this.killedMobs.containsKey(killedMob)) {
            this.killedMobs.put(killedMob, this.killedMobs.get(killedMob) + 1);
        } else {
            this.killedMobs.put(killedMob, 1);
        }
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.user.getUniqueId().toString());
        serialized.put("accountNumber", this.accountNumber + "");
        if (!skillExperience.isEmpty()) {
            for (Entry<SkillType, Double> entry : this.skillExperience.entrySet()) {
                serialized.put(entry.getKey().name(), entry.getValue() + "");
            }
        }
        
        if (!minedBlocks.isEmpty()) {
            for (Entry<Material, Integer> entry : this.minedBlocks.entrySet()) {
                serialized.put("minedblock-" + entry.getKey().name(), entry.getValue() + "");
            }
        }
        
        if (!placedBlocks.isEmpty()) {
            for (Entry<Material, Integer> entry : this.placedBlocks.entrySet()) {
                serialized.put("placedblock-" + entry.getKey().name(), entry.getValue() + "");
            }
        }
        
        if (!killedMobs.isEmpty()) {
            for (Entry<EntityType, Integer> entry : this.killedMobs.entrySet()) {
                serialized.put("killedmob-" + entry.getKey().name(), entry.getValue() + "");
            }
        }
        
        if (!homes.isEmpty()) {
            for (Home home : this.homes) {
                serialized.put("home-" + home.getName().toLowerCase(), home);
            }
        }
        
        serialized.put("channelFocus", this.channelFocusId + "");
        serialized.put("staffmode", this.staffmode);
        
        if (!limitBoosts.isEmpty()) {
            for (LimitBoost limitBoost : this.limitBoosts.values()) {
                serialized.put("limitboost-" + limitBoost.getLimit().getId(), limitBoost);
            }
        }
        
        if (!kitUses.isEmpty()) {
            this.kitUses.forEach((kitId, useInfo) -> serialized.put("kituse-" + kitId, useInfo));
        }
        
        if (!this.questProgress.isEmpty()) {
            this.questProgress.forEach((quest, tasks) -> {
                List<String> taskStrings = new ArrayList<>();
                tasks.forEach(task -> taskStrings.add(task.toString()));
                serialized.put("questProgress-" + quest.toString(), taskStrings);
            });
        }
        
        if (!this.completedQuests.isEmpty()) {
            List<String> questStrings = new ArrayList<>();
            this.completedQuests.forEach(quest -> questStrings.add(quest.toString()));
            serialized.put("completedQuests", questStrings);
        }
        
        if (!this.activeQuestLines.isEmpty()) {
            List<String> lineStrings = new ArrayList<>();
            this.activeQuestLines.forEach(line -> lineStrings.add(line.toString()));
            serialized.put("activeQuestLines", lineStrings);
        }
        
        if (questGuiDefault != null) {
            serialized.put("questGuiDefault", this.questGuiDefault);
        }
        
        if (!this.taskProgress.isEmpty()) {
            this.taskProgress.forEach((id, progress) -> serialized.put("taskProgress-" + id.toString(), progress));
        }
        
        serialized.put("mailbox", getMailbox());
        serialized.put("socialspy", getSocialSpy());
        serialized.put("prefix", prefixId);
        serialized.put("customColor", this.customColor);
        
        return serialized;
    }
    
    public User getUser() {
        return user;
    }
    
    public void sendMessage(BaseComponent... components) {
        user.sendMessage(components);
    }
    
    public void sendMessage(String... messages) {
        for (String message : messages) {
            this.sendMessage(message);
        }
    }
    
    public void sendMessage(String message) {
        user.sendMessage(replaceColorVariables(message));
    }
    
    protected String replaceColorVariables(String message) {
        message = message.replace("&g", Constants.PLAYER_BASE_COLOR);
        message = message.replace("&h", Constants.PLAYER_VARIABLE_COLOR);
        message = message.replace("&i", Constants.ADMIN_BASE_COLOR);
        message = message.replace("&j", Constants.ADMIN_VARIABLE_COLOR);
        message = message.replace("&s", Constants.HAMLET_BASE_COLOR);
        return message.replace("&t", Constants.HAMLET_VARIABLE_COLOR);
    }
    
    public String getName() {
        return this.user.getLastName();
    }
    
    @Override
    public String getIdentifier() {
        return getUniqueId().toString();
    }
    
    @Override
    public Number getLimitValue(Limit limit) {
        LimitBoost boost = getLimitBoost(limit);
        if (boost != null) {
            return boost.getOperator().calculate(limit.getValue(), boost.getValue());
        }
        return limit.getValue();
    }
    
    @Override
    public void addLimitBoost(LimitBoost boost) {
        this.limitBoosts.put(boost.getLimit().getId(), boost);
    }
    
    @Override
    public LimitBoost getLimitBoost(Limit limit) {
        return this.limitBoosts.get(limit.getId());
    }
    
    public UUID getUniqueId() {
        return this.user.getUniqueId();
    }
    
    public void sendDelayedMessage(String message, long tickDelay) {
        new BukkitRunnable() {
            public void run() {
                sendMessage(message);
            }
        }.runTaskLater(Realms.getInstance(), tickDelay);
    }
    
    public EnumMap<Material, Integer> getMinedBlocks() {
        return minedBlocks;
    }
    
    public EnumMap<EntityType, Integer> getKilledMobs() {
        return killedMobs;
    }
    
    public EnumMap<Material, Integer> getPlacedBlocks() {
        return placedBlocks;
    }
    
    public void addSkillExperience(SkillType type, double value) {
        if (skillExperience.containsKey(type)) {
            skillExperience.put(type, skillExperience.get(type) + value);
        } else {
            skillExperience.put(type, value);
        }
    }
    
    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }
    
    public Set<Home> getHomes() {
        return this.homes;
    }
    
    public RealmsBoard getDisplayBoard() {
        return displayBoard;
    }
    
    public void setDisplayBoard(RealmsBoard displayBoard) {
        this.displayBoard = displayBoard;
    }
    
    public boolean removeHome(String home) {
        return this.homes.removeIf(h -> h.getName().equalsIgnoreCase(home));
    }
    
    public DeathHome getDeathHome() {
        Home dh = getHome(DeathHome.NAME);
        if (dh instanceof DeathHome) {
            return (DeathHome) dh;
        }
        
        return null;
    }
    
    public void setDeathHome(Location location) {
        Home deathHome = getHome(DeathHome.NAME);
        if (deathHome == null) {
            DeathHome dH = new DeathHome(this.getUniqueId(), location, System.currentTimeMillis());
            this.addHome(dH);
        } else {
            if (deathHome instanceof DeathHome) {
                deathHome.setLocation(location);
            } else {
                this.removeHome(deathHome);
                DeathHome dH = new DeathHome(this.getUniqueId(), location, System.currentTimeMillis());
                this.addHome(dH);
            }
        }
    }
    
    public void removeHome(Home home) {
        this.homes.remove(home);
    }
    
    public Home getHome(String name) {
        for (Home home : this.homes) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        
        return null;
    }
    
    public void teleport(Entity entity) {
        this.teleport(entity.getLocation());
    }
    
    public void teleport(Location location) {
        Player player = Bukkit.getPlayer(this.getUniqueId());
        if (player != null && location != null) {
            player.teleport(location);
        }
    }
    
    public void teleport(Home home) {
        this.teleport(home.getLocation());
    }
    
    public void teleport(Territory territory) {
        this.teleport(territory.getSpawnpoint());
    }
    
    public boolean hasPermission(String permission) {
        if (StringUtils.isEmpty(permission)) return true;
        if (isOnline()) {
            Player player = Bukkit.getPlayer(this.getUniqueId());
            return player.hasPermission(permission);
        } else {
            return Realms.getInstance().getVault().getPermission().playerHas(null, Bukkit.getOfflinePlayer(this.getUniqueId()), permission);
        }
    }
    
    public boolean isOnline() {
        return user.isOnline();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RealmProfile profile = (RealmProfile) o;
        return Objects.equals(user, profile.user);
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    public World getWorld() {
        return getLocation().getWorld();
    }
    
    public Location getLocation() {
        return user.getLocation();
    }
    
    public Inventory getInventory() {
        if (user.isOnline()) {
            return Bukkit.getPlayer(getUniqueId()).getInventory();
        }
        return Bukkit.createInventory(null, 9);
    }
    
    public Block getTargetBlock(int i) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            return player.getTargetBlock(null, i);
        }
        return null;
    }
    
    public Account getAccount() {
        if (this.account == null) {
            this.account = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(accountNumber);
            if (this.account == null) {
                this.createDefaultAccount();
            }
        }
        
        if (accountNumber != this.account.getAccountNumber()) {
            this.account = Realms.getInstance().getEconomyManager().getAccountHandler().getAccount(accountNumber);
            if (this.account == null) {
                this.createDefaultAccount();
            }
        }
        
        return account;
    }
    
    protected void createDefaultAccount() {
        if (this.account == null) {
            Account account = Realms.getInstance().getEconomyManager().getAccountHandler().createAccount(this);
            this.accountNumber = account.getAccountNumber();
            this.account = account;
            Realms.getInstance().getEconomyManager().getTransactionHandler().initialDeposit(account);
        }
    }
    
    public void teleport(Warp warp) {
        warp.addVisit(this.getUniqueId());
        this.teleport(warp.getLocation());
    }
    
    public boolean setChannelFocus(Channel channel) {
        if (channel == null) {
            return false;
        }
        channel.addParticipant(getUniqueId(), Role.MEMBER);
        this.channelFocus = channel;
        this.channelFocusId = channel.getId();
        return true;
    }
    
    public Channel getChannelFocus() {
        if (channelFocus == null) {
            this.channelFocus = Realms.getInstance().getChannelManager().getChannel(channelFocusId);
            if (channelFocus == null) {
                this.channelFocus = Realms.getInstance().getChannelManager().getGlobalChannel();
                this.channelFocusId = Realms.getInstance().getChannelManager().getGlobalChannel().getId();
            }
        }
        
        return channelFocus;
    }
    
    public CommandSender getLastMessage() {
        if (StringUtils.isEmpty(lastMessage)) { return null; }
        if (lastMessage.equalsIgnoreCase("Console") || lastMessage.equalsIgnoreCase("Server")) {
            return Bukkit.getConsoleSender();
        } else {
            UUID uuid;
            try {
                uuid = UUID.fromString(this.lastMessage);
            } catch (Exception e) {
                return null;
            }
            return Bukkit.getPlayer(uuid);
        }
    }
    
    public void setLastMessage(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            this.lastMessage = "Console";
        } else if (sender instanceof Player) {
            this.lastMessage = ((Player) sender).getUniqueId().toString();
        } else {
            this.lastMessage = "";
        }
    }
    
    public void setGamemode(GameMode gameMode) {
        Player player = getBukkitPlayer();
        if (player != null) {
            player.setGameMode(gameMode);
        }
    }
    
    public StaffMode getStaffMode() {
        if (staffmode == null) {
            this.staffmode = new StaffMode();
        }
        return staffmode;
    }
    
    public StaffHome getStaffHome(String name) {
        for (Home home : this.homes) {
            if (home instanceof StaffHome) {
                if (home.getName().equalsIgnoreCase(name)) {
                    return (StaffHome) home;
                }
            }
        }
        
        return null;
    }
    
    public void removeStaffHome(StaffHome staffHome) {
        Iterator<Home> iterator = this.homes.iterator();
        while (iterator.hasNext()) {
            Home home = iterator.next();
            if (home instanceof StaffHome) {
                if (staffHome.getName().equalsIgnoreCase(home.getName())) {
                    iterator.remove();
                }
            }
        }
    }
    
    public int getKitUses(Kit kit) {
        if (kit == null) { return 0; }
        KitUseInfo info = this.kitUses.get(kit.getId());
        if (info != null) {
            return info.getUses();
        }
        return 0;
    }
    
    public void setKitUses(Kit kit, int value) {
        if (this.kitUses.containsKey(kit.getId())) {
            this.kitUses.get(kit.getId()).setUses(value);
        }
    }
    
    public void addKitUse(Kit kit, int value) {
        if (this.kitUses.containsKey(kit.getId())) {
            this.kitUses.get(kit.getId()).addUse(value);
        } else {
            this.kitUses.put(kit.getId(), new KitUseInfo(kit.getId(), value, System.currentTimeMillis()));
        }
    }
    
    public KitUseInfo getKitUseInfo(Kit kit) {
        return this.kitUses.get(kit.getId());
    }
    
    public TextComponent getDisplayName() {
        Group group = getGroup();
        Prefix prefix = getPrefix();
        if (prefix == null) {
            prefix = group.getPrefix();
        }
        
        ChatColor color;
        if (group.getName().equalsIgnoreCase("owner")) {
            if (StringUtils.isNotEmpty(this.customColor)) {
                color = getCustomColor();
            } else {
                color = ChatColor.of(prefix.getColor());
            }
        } else {
            color = ChatColor.of(prefix.getColor());
        }
        TextComponent prefixComponent = new TextComponent(prefix.getText());
        prefixComponent.setColor(color);
        prefixComponent.setBold(prefix.isBold());
    
        NameFormat nameFormat = group.getNameFormat();
    
        TextComponent name = new TextComponent(getName());
        name.setColor(color);
        name.setBold(nameFormat.isNameBold());
    
        return new TextComponent(new ComponentBuilder().append(prefixComponent).append(" ").append(name).create());
    }
    
    public void addCompletedQuest(Quest quest) {
        this.completedQuests.add(quest.getId());
        this.questProgress.remove(quest.getId());
    }
    
    public boolean isTaskComplete(ID questId, ID taskId) {
        if (this.completedQuests.contains(questId)) { return true; }
        if (this.questProgress.containsKey(questId)) {
            return this.questProgress.get(questId).contains(taskId);
        }
        return false;
    }
    
    public boolean isQuestGuiDefault(QuestLine line) {
        if (this.questGuiDefault == null) { return false; }
        return this.questGuiDefault.equals(line.getId());
    }
    
    public ID getQuestGuiDefault() {
        return questGuiDefault;
    }
    
    public void setQuestGuiDefault(ID questGuiDefault) {
        this.questGuiDefault = questGuiDefault;
    }
    
    public boolean isQuestLocked(Quest quest) {
        if (!isQuestComplete(quest)) {
            for (ID pq : quest.getRequired()) {
                Quest required = Realms.getInstance().getQuestManager().getQuest(pq);
                if (!isQuestComplete(required)) {
                    return true;
                }
            }
            
            return !isActiveQuestLine(quest.getParentLine());
        }
        return false;
    }
    
    public boolean isQuestComplete(Quest quest) {
        return this.completedQuests.contains(quest.getId());
    }
    
    public boolean isActiveQuestLine(QuestLine questLine) {
        return this.activeQuestLines.contains(questLine.getId());
    }
    
    public EnumMap<SkillType, Double> getSkillExperience() {
        return this.skillExperience;
    }
    
    public double getTotalExperience() {
        AtomicReference<Double> totalExperience = new AtomicReference<>((double) 0);
        this.skillExperience.forEach((type, amount) -> totalExperience.updateAndGet(v -> (double) (v + amount)));

//       TODO When the enchanting system is implemented
//        if (isOnline()) {
//            getBukkitPlayer().setTotalExperience(totalExperience.get().intValue());
//        }
        return totalExperience.get();
    }
    
    public Map<Integer, KitUseInfo> getKitUsage() {
        return this.kitUses;
    }
    
    public Set<ID> getCompletedQuests() {
        return this.completedQuests;
    }
    
    public Map<ID, Set<ID>> getQuestProgress() {
        return this.questProgress;
    }
    
    public Set<ID> getActiveQuestLines() {
        return this.activeQuestLines;
    }
    
    public void addTaskProgress(Task task, TaskProgress progress) {
        this.taskProgress.put(task.getId(), progress);
    }
    
    public void resetProgress(QuestLine questLine) {
        for (ID quest : questLine.getQuests().values()) {
            this.completedQuests.remove(quest);
            this.questProgress.remove(quest);
        }
    }
    
    public boolean isQuestComplete(ID id) {
        return this.completedQuests.contains(id);
    }
    
    public String getPermissionGroup() {
        VaultIntegration vault = Realms.getInstance().getVault();
        String group = vault.getPermission().getPrimaryGroup(getBukkitPlayer());
        if (StringUtils.isNotEmpty(group)) {
            return group;
        }
        return "default";
    }
    
    public Mailbox getMailbox() {
        if (mailbox == null) {
            this.mailbox = new Mailbox(this.getUniqueId());
        }
        return mailbox;
    }
    
    public SocialSpy getSocialSpy() {
        if (socialSpy == null) {
            this.socialSpy = new SocialSpy();
        }
        return socialSpy;
    }
    
    public Prefix getPrefix() {
        if (prefix == null) {
            if (StringUtils.isNotEmpty(this.prefixId)) {
                prefix = Realms.getInstance().getChatManager().getPrefix(this.prefixId);
            }
        }
        return prefix;
    }
    
    public Group getGroup() {
        VaultIntegration vault = Realms.getInstance().getVault();
        String permGroup = vault.getPermission().getPrimaryGroup(getBukkitPlayer());
    
        Group group = Realms.getInstance().getChatManager().getGroup(permGroup);
        if (group == null) {
            group = Realms.getInstance().getChatManager().getGroup("default");
        }
        return group;
    }
}