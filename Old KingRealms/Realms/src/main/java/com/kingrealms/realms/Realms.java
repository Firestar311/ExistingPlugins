package com.kingrealms.realms;

import com.kingrealms.realms.channel.*;
import com.kingrealms.realms.channel.channels.GlobalChannel;
import com.kingrealms.realms.channel.channels.StaffChannel;
import com.kingrealms.realms.channel.channels.territory.HamletChannel;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.chat.ChatManager;
import com.kingrealms.realms.cmd.*;
import com.kingrealms.realms.crafting.CraftingManager;
import com.kingrealms.realms.economy.EconomyManager;
import com.kingrealms.realms.economy.account.*;
import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.economy.shop.types.impl.ServerSignShop;
import com.kingrealms.realms.economy.shop.types.impl.gui.ServerGUIShop;
import com.kingrealms.realms.economy.shop.types.impl.gui.ShopCategory;
import com.kingrealms.realms.economy.tickets.EcoTicket;
import com.kingrealms.realms.economy.transaction.Transaction;
import com.kingrealms.realms.entities.CustomEntities;
import com.kingrealms.realms.entities.type.CustomWitherSkeleton;
import com.kingrealms.realms.flight.FlightInfo;
import com.kingrealms.realms.graves.Grave;
import com.kingrealms.realms.graves.GraveManager;
import com.kingrealms.realms.home.*;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.kits.*;
import com.kingrealms.realms.limits.LimitBoost;
import com.kingrealms.realms.limits.LimitsManager;
import com.kingrealms.realms.limits.group.*;
import com.kingrealms.realms.limits.limit.*;
import com.kingrealms.realms.listener.*;
import com.kingrealms.realms.loot.LootManager;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.plot.PlotManager;
import com.kingrealms.realms.plot.claimed.ClaimedPlot;
import com.kingrealms.realms.plot.claimed.HamletPlot;
import com.kingrealms.realms.profile.*;
import com.kingrealms.realms.profile.board.PrimaryBoard;
import com.kingrealms.realms.questing.QuestManager;
import com.kingrealms.realms.questing.tasks.TaskProgress;
import com.kingrealms.realms.questing.tasks.progress.IntegerTaskProgress;
import com.kingrealms.realms.repair.RepairUseInfo;
import com.kingrealms.realms.season.Season;
import com.kingrealms.realms.season.Season.Type;
import com.kingrealms.realms.serverclaim.spawn.Spawn;
import com.kingrealms.realms.serverclaim.warzone.Warzone;
import com.kingrealms.realms.settings.SettingsManager;
import com.kingrealms.realms.skills.SkillManager;
import com.kingrealms.realms.skills.farming.CropBlock;
import com.kingrealms.realms.skills.farming.FarmingManager;
import com.kingrealms.realms.skills.farming.blocks.*;
import com.kingrealms.realms.skills.woodcutting.ArcaneTree;
import com.kingrealms.realms.skills.woodcutting.WoodcuttingManager;
import com.kingrealms.realms.skills.mining.MiningManager;
import com.kingrealms.realms.skills.mining.MysticalBlock;
import com.kingrealms.realms.spawners.*;
import com.kingrealms.realms.staffmode.StaffMode;
import com.kingrealms.realms.staffmode.StaffmodeManager;
import com.kingrealms.realms.storage.StorageManager;
import com.kingrealms.realms.supplydrops.SupplyCrate;
import com.kingrealms.realms.supplydrops.SupplyDropManager;
import com.kingrealms.realms.tasks.BoardUpdate;
import com.kingrealms.realms.territory.TerritoryManager;
import com.kingrealms.realms.territory.base.*;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.medievil.*;
import com.kingrealms.realms.territory.middle.*;
import com.kingrealms.realms.territory.modern.*;
import com.kingrealms.realms.trash.TrashUse;
import com.kingrealms.realms.warps.Visit;
import com.kingrealms.realms.warps.WarpManager;
import com.kingrealms.realms.warps.type.*;
import com.kingrealms.realms.whitelist.Whitelist;
import com.kingrealms.realms.whitelist.WhitelistManager;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.region.SelectionManager;
import com.starmediadev.lib.user.UserManager;
import com.starmediadev.lib.util.*;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftWitherSkeleton;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map.Entry;
import java.util.*;

public final class Realms extends JavaPlugin implements Listener {
    
    private static Realms INSTANCE;
    
    static {
        Utils.registerConfigClasses(FlightInfo.class, ArcaneTree.class, SupplyCrate.class, HamletPlot.class, SocialSpy.class, EcoTicket.class, Whitelist.class, Mailbox.class, TrashUse.class, AgeableCropBlock.class, StackableCropBlock.class, StaticCropBlock.class, CropBlock.class, TaskProgress.class, IntegerTaskProgress.class, MysticalBlock.class, RepairUseInfo.class, ID.class, ServerGUIShop.class, ShopCategory.class, Kit.class, KitTier.class, KitUseInfo.class, LimitBoost.class, LimitGroup.class, Limit.class, PlayerLimits.class, TerritoryLimits.class, DoubleLimit.class, IntegerLimit.class, StaffMode.class, Home.class, DeathHome.class, HamletChannel.class, StaffChannel.class, GlobalChannel.class, Channel.class, Participant.class, ServerWarp.class, TerritoryWarp.class, Warp.class, Visit.class, Shop.class, ServerSignShop.class, ShopItem.class, Account.class, ServerAccount.class, TerritoryAccount.class, PlayerAccount.class, Transaction.class, CustomSpawner.class, MobStack.class, Grave.class, StaffHome.class, Home.class, Spawn.class, Warzone.class, Territory.class, Government.class, Settlement.class, Outpost.class, Colony.class, Kingdom.class, Hamlet.class, Nation.class, Protectorate.class, Town.class, City.class, Country.class, Province.class, Plot.class, ClaimedPlot.class, Member.class, Invite.class, RealmProfile.class);
    }
    
    private SortedMap<Long, String> adminAuditLog = new TreeMap<>();
    private ConfigManager auditConfigManager = new ConfigManager(this, "audit");
    private ChannelManager channelManager;
    private ChatManager chatManager;
    private CraftingManager craftingManager;
    private EconomyManager economyManager;
    private FarmingManager farmingManager;
    private GraveManager graveManager;
    private KitManager kitManager;
    private LimitsManager limitsManager;
    private LootManager lootManager;
    private LuckPerms luckPerms;
    private WoodcuttingManager woodcuttingManager;
    private MiningManager miningManager;
    private PlotManager plotManager;
    private ProfileManager profileManager;
    private QuestManager questManager;
    private Season season;
    private SelectionManager selectionManager;
    private ServerMode serverMode;
    private SettingsManager settingsManager;
    private SkillManager skillManager = new SkillManager();
    private Spawn spawn;
    private SpawnerManager spawnerManager;
    private StaffmodeManager staffmodeManager;
    private StorageManager storageManager;
    private SupplyDropManager supplyDropManager;
    private TerritoryManager territoryManager;
    private UserManager userManager;
    private VaultIntegration vault;
    private WarpManager warpManager;
    private Warzone warzone;
    private WhitelistManager whitelistManager;
    
    public static void updateChannels() {
        GlobalChannel channel = getInstance().getChannelManager().getGlobalChannel();
        StaffChannel staffChannel = getInstance().getChannelManager().getStaffChannel();
        for (Player player : Bukkit.getOnlinePlayers()) {
            RealmProfile profile = getInstance().getProfileManager().getProfile(player);
            if (!channel.isParticipant(player.getUniqueId())) {
                channel.addParticipant(player.getUniqueId(), Role.MEMBER);
            }
            
            if (staffChannel.hasPermission(profile)) {
                staffChannel.addParticipant(player.getUniqueId(), Role.MEMBER);
            }
            
            Hamlet hamlet = (Hamlet) getInstance().getTerritoryManager().getTerritory(profile);
            if (hamlet != null) {
                Channel hamletChannel = hamlet.getChannel();
                if (!hamletChannel.isParticipant(profile)) {
                    hamletChannel.addParticipant(profile.getUniqueId(), Role.MEMBER);
                }
            }
        }
    }
    
    public ChannelManager getChannelManager() {
        return channelManager;
    }
    
    public static Realms getInstance() {
        return INSTANCE;
    }
    
    public ProfileManager getProfileManager() {
        return profileManager;
    }
    
    public TerritoryManager getTerritoryManager() {
        return territoryManager;
    }

//    @Override
//    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//        if (cmd.getName().equalsIgnoreCase("test")) {
//            long start = System.currentTimeMillis();
//            getSupplyDropManager().generateSupplyDrop();
//            long end = System.currentTimeMillis();
//            long totalTime = end - start;
//            sender.sendMessage(Utils.color("&aSupply Drop took " + totalTime + " milliseconds to generate and created a total of " + getSupplyDropManager().getSupplyCrates().size() + " Supply Crates"));
//        }
//
//        return true;
//    }
    
    @Override
    public void onLoad() {
        CustomEntities.REGISTRY.forEach((type, entity) -> entity.register());
    }
    
    @Override
    public void onDisable() {
        this.saveData();
    }
    
    public void saveData() {
        this.getConfig().set("spawn", this.spawn);
        this.getConfig().set("warzone", this.warzone);
        this.getConfig().set("seasonActive", this.season.isActive());
        this.getConfig().set("servermode", this.serverMode.name());
        this.saveConfig();
        
        if (!this.adminAuditLog.isEmpty()) {
            this.adminAuditLog.forEach((id, text) -> this.auditConfigManager.getConfig().set("audit." + id, text));
        }
        
        this.channelManager.saveData();
        this.warpManager.saveData();
        this.economyManager.saveData();
        this.auditConfigManager.saveConfig();
        this.spawnerManager.saveData();
        this.graveManager.saveData();
        this.territoryManager.saveData();
        this.plotManager.saveData();
        this.profileManager.saveData();
        this.limitsManager.saveData();
        this.kitManager.saveData();
        this.settingsManager.saveData();
        this.miningManager.saveData();
        this.farmingManager.saveData();
        this.craftingManager.saveData();
        this.whitelistManager.saveData();
        this.supplyDropManager.saveData();
        this.chatManager.saveData();
        this.woodcuttingManager.saveData();
    }
    
    @Override
    public void onEnable() {
        INSTANCE = this;
        PaginatedGUI.prepare(INSTANCE);
        this.saveDefaultConfig();
        this.storageManager = new StorageManager();
        
        this.season = new Season(2, Type.BETA);
        if (this.getConfig().contains("servermode")) {
            this.serverMode = ServerMode.valueOf(this.getConfig().getString("servermode"));
        } else {
            this.serverMode = ServerMode.MAINTENANCE;
        }
    
        getLogger().info("Loaded " + CustomItemRegistry.REGISTRY.size() + " Custom Items");
        
        this.auditConfigManager.setup();
        ConfigurationSection auditSection = this.auditConfigManager.getConfig().getConfigurationSection("audit");
        if (auditSection != null) {
            for (String i : auditSection.getKeys(false)) {
                long id = Long.parseLong(i);
                String text = auditSection.getString(i);
                this.adminAuditLog.put(id, text);
            }
        }
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListener(), this);
        pm.registerEvents(new EntityListener(), this);
        pm.registerEvents(new PlayerListener(), this);
        
        this.selectionManager = new SelectionManager();
        this.vault = new VaultIntegration();
        
        ServicesManager serviceManager = getServer().getServicesManager();
        RegisteredServiceProvider<UserManager> playerRsp = serviceManager.getRegistration(UserManager.class);
        if (playerRsp != null) {
            this.userManager = playerRsp.getProvider();
        } else {
            pm.disablePlugin(this);
            return;
        }
        
        RegisteredServiceProvider<LuckPerms> luckPermsRsp = serviceManager.getRegistration(LuckPerms.class);
        if (luckPermsRsp != null) {
            this.luckPerms = luckPermsRsp.getProvider();
        } else {
            pm.disablePlugin(this);
            return;
        }
        
        this.settingsManager = new SettingsManager();
        this.warpManager = new WarpManager();
        this.profileManager = new ProfileManager();
        this.economyManager = new EconomyManager();
        this.plotManager = new PlotManager();
        this.territoryManager = new TerritoryManager();
        this.graveManager = new GraveManager();
        this.spawnerManager = new SpawnerManager();
        this.channelManager = new ChannelManager();
        this.craftingManager = new CraftingManager();
        this.staffmodeManager = new StaffmodeManager();
        this.limitsManager = new LimitsManager();
        this.kitManager = new KitManager();
        this.questManager = new QuestManager();
        this.miningManager = new MiningManager();
        this.farmingManager = new FarmingManager();
        this.whitelistManager = new WhitelistManager();
        this.supplyDropManager = new SupplyDropManager();
        this.lootManager = new LootManager();
        this.chatManager = new ChatManager();
        this.woodcuttingManager = new WoodcuttingManager();
        
        this.settingsManager.loadData();
        this.warpManager.loadData();
        this.economyManager.loadData();
        this.plotManager.loadData();
        this.territoryManager.loadData();
        this.graveManager.loadData();
        this.spawnerManager.loadData();
        this.channelManager.loadData();
        this.profileManager.loadData();
        this.limitsManager.loadData();
        this.kitManager.loadData();
        this.miningManager.loadData();
        this.farmingManager.loadData();
        this.craftingManager.loadData();
        this.whitelistManager.loadData();
        this.supplyDropManager.loadData();
        this.chatManager.loadData();
        this.woodcuttingManager.loadData();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            RealmProfile profile = getProfileManager().getProfile(player);
            profile.setDisplayBoard(new PrimaryBoard(player));
        }
        
        this.getCommand("realms").setExecutor(new RealmsCommand());
        HamletCommands hamletCommands = new HamletCommands();
        this.getCommand("hamlets").setExecutor(hamletCommands);
        this.getCommand("ally").setExecutor(hamletCommands);
        SpawnCommands spawnCommands = new SpawnCommands();
        this.getCommand("spawn").setExecutor(spawnCommands);
        this.getCommand("setspawn").setExecutor(spawnCommands);
        HomeCommands homeCommands = new HomeCommands();
        this.getCommand("home").setExecutor(homeCommands);
        this.getCommand("sethome").setExecutor(homeCommands);
        this.getCommand("delhome").setExecutor(homeCommands);
        this.getCommand("staffhome").setExecutor(homeCommands);
        this.getCommand("renamehome").setExecutor(homeCommands);
        TeleportCommands teleportCommands = new TeleportCommands();
        this.getCommand("teleport").setExecutor(teleportCommands);
        this.getCommand("tpa").setExecutor(teleportCommands);
        this.getCommand("tpaccept").setExecutor(teleportCommands);
        this.getCommand("tpdeny").setExecutor(teleportCommands);
        this.getCommand("tpacancel").setExecutor(teleportCommands);
        this.getCommand("tpall").setExecutor(teleportCommands);
        this.getCommand("tpahere").setExecutor(teleportCommands);
        this.getCommand("tphere").setExecutor(teleportCommands);
        ListCommands listCommands = new ListCommands();
        this.getCommand("list").setExecutor(listCommands);
        this.getCommand("stafflist").setExecutor(listCommands);
        PlaytimeCommands playtimeCommands = new PlaytimeCommands();
        this.getCommand("playtime").setExecutor(playtimeCommands);
        this.getCommand("playtimetop").setExecutor(playtimeCommands);
        EconomyCommands economyCommands = new EconomyCommands();
        this.getCommand("economy").setExecutor(economyCommands);
        this.getCommand("balance").setExecutor(economyCommands);
        this.getCommand("balancetop").setExecutor(economyCommands);
        this.getCommand("pay").setExecutor(economyCommands);
        this.getCommand("withdraw").setExecutor(economyCommands);
        WarpCommands warpCommands = new WarpCommands();
        this.getCommand("warp").setExecutor(warpCommands);
        this.getCommand("setwarp").setExecutor(warpCommands);
        this.getCommand("delwarp").setExecutor(warpCommands);
        ChatCommands chatCommands = new ChatCommands();
        this.getCommand("global").setExecutor(chatCommands);
        this.getCommand("staff").setExecutor(chatCommands);
        this.getCommand("channel").setExecutor(chatCommands);
        this.getCommand("message").setExecutor(chatCommands);
        this.getCommand("reply").setExecutor(chatCommands);
        UtilityCommands utilityCommands = new UtilityCommands();
        this.getCommand("seen").setExecutor(utilityCommands);
        this.getCommand("craft").setExecutor(utilityCommands);
        this.getCommand("mailbox").setExecutor(utilityCommands);
        GamemodeCommands gamemodeCommands = new GamemodeCommands();
        this.getCommand("gamemode").setExecutor(gamemodeCommands);
        this.getCommand("gmc").setExecutor(gamemodeCommands);
        this.getCommand("gms").setExecutor(gamemodeCommands);
        this.getCommand("gmsp").setExecutor(gamemodeCommands);
        this.getCommand("gma").setExecutor(gamemodeCommands);
        this.getCommand("staffmode").setExecutor(new StaffmodeCommand());
        KitCommands kitCommands = new KitCommands();
        this.getCommand("kit").setExecutor(kitCommands);
        this.getCommand("createkit").setExecutor(kitCommands);
        this.getCommand("deletekit").setExecutor(kitCommands);
        this.getCommand("limits").setExecutor(new LimitsCommand());
        ShopCommands shopCommands = new ShopCommands();
        this.getCommand("shop").setExecutor(shopCommands);
        this.getCommand("guishop").setExecutor(shopCommands);
        this.getCommand("shopitem").setExecutor(shopCommands);
        this.getCommand("signshop").setExecutor(shopCommands);
        QuestCommands questCommands = new QuestCommands();
        this.getCommand("quest").setExecutor(questCommands);
        this.getCommand("questadmin").setExecutor(questCommands);
        RepairCommands repairCommands = new RepairCommands();
        this.getCommand("repair").setExecutor(repairCommands);
        this.getCommand("repairall").setExecutor(repairCommands);
        SkillCommands skillCommands = new SkillCommands();
        this.getCommand("skills").setExecutor(skillCommands);
        this.getCommand("farming").setExecutor(skillCommands);
        this.getCommand("mining").setExecutor(skillCommands);
        this.getCommand("slayer").setExecutor(skillCommands);
        this.getCommand("woodcutting").setExecutor(skillCommands);
        this.getCommand("profile").setExecutor(new ProfileCommand());
        ModerationCommands moderationCommands = new ModerationCommands();
        this.getCommand("clearchat").setExecutor(moderationCommands);
        this.getCommand("socialspy").setExecutor(moderationCommands);
        this.getCommand("invsee").setExecutor(moderationCommands);
        ProfileCommand profileCommand = new ProfileCommand();
        this.getCommand("setcolor").setExecutor(profileCommand);
        
        if (this.getConfig().contains("spawn")) {
            this.spawn = (Spawn) this.getConfig().get("spawn");
        } else {
            this.spawn = new Spawn(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        
        if (this.getConfig().contains("warzone")) {
            this.warzone = (Warzone) this.getConfig().get("warzone");
        } else {
            this.warzone = new Warzone();
        }
        
        new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage(Utils.color("&b&l[&6&lRealms&b&l] &aSaving Realms Data..."));
                saveData();
                Bukkit.broadcastMessage(Utils.color("&b&l[&6&lRealms&b&l] &aSave complete."));
            }
        }.runTaskTimerAsynchronously(this, 6000L, 12000L);
        
        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    contents:
                    for (ItemStack itemStack : p.getInventory().getContents()) {
                        if (itemStack != null && itemStack.getItemMeta() != null) {
                            try {
                                String id = NBTWrapper.getNBTString(itemStack, "itemid");
                                if (id.equalsIgnoreCase("excalibur")) {
                                    p.getInventory().removeItem(itemStack);
                                    getLogger().info("Removed contraband item from " + p.getName());
                                    continue;
                                }
                            } catch (Exception e) { }
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            if (itemMeta.getAttributeModifiers() != null) {
                                if (!itemMeta.getAttributeModifiers().isEmpty()) {
                                    for (Entry<Attribute, AttributeModifier> entry : itemMeta.getAttributeModifiers().entries()) {
                                        if (entry.getValue().getAmount() >= 50) {
                                            p.getInventory().removeItem(itemStack);
                                            getLogger().info("Removed contraband item from " + p.getName());
                                            continue contents;
                                        }
                                    }
                                }
                            }
                            
                            if (itemMeta.getEnchants() != null) {
                                for (Entry<Enchantment, Integer> entry : itemMeta.getEnchants().entrySet()) {
                                    if (entry.getValue() > 10) {
                                        p.getInventory().removeItem(itemStack);
                                        getLogger().info("Removed contraband item from " + p.getName());
                                        continue contents;
                                    }
                                }
                            }
                            
                            if (itemMeta instanceof PotionMeta) {
                                PotionMeta potionMeta = (PotionMeta) itemMeta;
                                if (potionMeta.hasCustomEffects()) {
                                    for (PotionEffect effect : potionMeta.getCustomEffects()) {
                                        if (effect.getAmplifier() > 5 || effect.getDuration() > TickUtils.asMinutes(8)) {
                                            p.getInventory().removeItem(itemStack);
                                            getLogger().info("Removed contraband item from " + p.getName());
                                            continue contents;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L);
        
        this.craftingManager.registerVanillaRecipes();
        this.craftingManager.registerTableRecipes();
        new BoardUpdate(this).runTaskTimer(this, 5L, 5L);
        if (this.getConfig().contains("seasonActive")) {
            this.season.setActive(this.getConfig().getBoolean("seasonActive"));
        }
        
        new BukkitRunnable() {
            public void run() {
                try {
                    CustomWitherSkeleton skeleton = (CustomWitherSkeleton) ((CraftWitherSkeleton) Bukkit.getEntity(getSettingsManager().getNetherPortalKeeper())).getHandle();
                    if (skeleton.isPortalKeeper()) {
                        if (!skeleton.getBukkitEntity().getLocation().equals(CustomWitherSkeleton.location)) {
                            skeleton.getBukkitEntity().teleport(CustomWitherSkeleton.location);
                        }
                    }
                } catch (Exception e) {}
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 2L);
    }
    
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
    
    public VaultIntegration getVault() {
        return vault;
    }
    
    public UserManager getUserManager() {
        return userManager;
    }
    
    public Spawn getSpawn() {
        return spawn;
    }
    
    public Warzone getWarzone() {
        return warzone;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public PlotManager getPlotManager() {
        return plotManager;
    }
    
    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
    
    public Season getSeason() {
        return season;
    }
    
    public void addAuditEntry(String text) {
        long index;
        try {
            index = adminAuditLog.lastKey() + 1;
        } catch (NoSuchElementException e) {
            index = 0;
        }
        
        this.adminAuditLog.put(index, text);
    }
    
    public GraveManager getGraveManager() {
        return graveManager;
    }
    
    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public WarpManager getWarpManager() {
        return warpManager;
    }
    
    public boolean isMaintenance() {
        return getServerMode() == ServerMode.MAINTENANCE;
    }
    
    public ServerMode getServerMode() {
        return serverMode;
    }
    
    public void setServerMode(ServerMode serverMode) {
        this.serverMode = serverMode;
    }
    
    public CraftingManager getCraftingManager() {
        return craftingManager;
    }
    
    public StaffmodeManager getStaffmodeManager() {
        return staffmodeManager;
    }
    
    public LimitsManager getLimitsManager() {
        return limitsManager;
    }
    
    public KitManager getKitManager() {
        return kitManager;
    }
    
    public QuestManager getQuestManager() {
        return questManager;
    }
    
    public MiningManager getMiningManager() {
        return miningManager;
    }
    
    public FarmingManager getFarmingManager() {
        return farmingManager;
    }
    
    public StorageManager getStorageManager() {
        return storageManager;
    }
    
    public WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
    
    public SupplyDropManager getSupplyDropManager() {
        return supplyDropManager;
    }
    
    public LootManager getLootManager() {
        return lootManager;
    }
    
    public ChatManager getChatManager() {
        return chatManager;
    }
    
    public SkillManager getSkillManager() {
        return skillManager;
    }
}