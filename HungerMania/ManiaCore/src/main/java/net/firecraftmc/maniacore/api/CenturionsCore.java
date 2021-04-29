package net.firecraftmc.maniacore.api;

import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.chat.ChatManager;
import net.firecraftmc.maniacore.api.communication.MessageHandler;
import net.firecraftmc.maniacore.api.events.EventManager;
import net.firecraftmc.maniacore.api.friends.FriendNotification;
import net.firecraftmc.maniacore.api.friends.FriendRequest;
import net.firecraftmc.maniacore.api.friends.FriendsManager;
import net.firecraftmc.maniacore.api.friends.Friendship;
import net.firecraftmc.maniacore.api.leveling.Level;
import net.firecraftmc.maniacore.api.leveling.LevelManager;
import net.firecraftmc.maniacore.api.logging.entry.ChatEntry;
import net.firecraftmc.maniacore.api.logging.entry.CmdEntry;
import net.firecraftmc.maniacore.api.nickname.NicknameManager;
import net.firecraftmc.maniacore.api.records.NicknameRecord;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.server.ServerManager;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.maniacore.api.skin.SkinManager;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.user.IgnoreInfo;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.UserManager;
import net.firecraftmc.maniacore.api.user.toggle.Toggle;
import net.firecraftmc.maniacore.api.util.CenturionsProperties;
import net.firecraftmc.maniacore.memory.MemoryManager;
import net.firecraftmc.maniacore.plugin.CenturionsPlugin;
import net.firecraftmc.manialib.CenturionsLib;
import net.firecraftmc.manialib.data.DatabaseManager;
import net.firecraftmc.manialib.data.model.DatabaseHandler;
import net.firecraftmc.manialib.sql.Database;

import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

public class CenturionsCore implements DatabaseHandler {

    public static final Random RANDOM = new Random();

    private Database database;

    private CenturionsLib centurionsLib;
    private static CenturionsCore instance;

    private DatabaseManager databaseManager;

    private CenturionsPlugin plugin;

    private net.firecraftmc.maniacore.api.user.UserManager userManager;
    private net.firecraftmc.maniacore.api.communication.MessageHandler messageHandler;
    private net.firecraftmc.maniacore.api.server.ServerManager serverManager;
    private net.firecraftmc.maniacore.api.skin.SkinManager skinManager;
    private Logger logger;
    private net.firecraftmc.maniacore.api.leveling.LevelManager levelManager;
    private net.firecraftmc.maniacore.memory.MemoryManager memoryManager;
    private net.firecraftmc.maniacore.api.events.EventManager eventManager;
    private net.firecraftmc.maniacore.api.friends.FriendsManager friendsManager;
    private net.firecraftmc.maniacore.api.chat.ChatManager chatManager;
    private net.firecraftmc.maniacore.api.nickname.NicknameManager nicknameManager;

    public void init(Logger logger, CenturionsPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        Properties databaseProperties = new Properties();
        databaseProperties.setProperty("mysql-host", CenturionsProperties.MYSQL_HOST);
        databaseProperties.setProperty("mysql-port", CenturionsProperties.MYSQL_PORT + "");
        databaseProperties.setProperty("mysql-database", CenturionsProperties.MYSQL_DATABASE);
        databaseProperties.setProperty("mysql-username", CenturionsProperties.MYSQL_USERNAME);
        databaseProperties.setProperty("mysql-password", CenturionsProperties.MYSQL_PASSWORD);
        this.centurionsLib = new CenturionsLib(databaseProperties, logger);
        this.database = centurionsLib.getDatabase();
        this.databaseManager = centurionsLib.getDatabaseManager();
        centurionsLib.addDatabaseHandler(this);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.UserRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.ChatEntryRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.CmdEntryRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.SkinRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.LevelRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.EventInfoRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.IgnoreInfoRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.StatRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.FriendRequestRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.FriendshipRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.FriendNotificationRecord.class);
        this.database.registerRecordType(net.firecraftmc.maniacore.api.records.ToggleRecord.class);
        this.database.registerRecordType(NicknameRecord.class);
        plugin.setupDatabaseRecords();
        this.database.generateTables();

        plugin.setupUserManager();
        if (this.userManager == null) {
            getLogger().severe("No UserManager found!");
        }

        plugin.setupServerManager();

        net.firecraftmc.maniacore.api.redis.Redis.startRedis();
        plugin.setupRedisListeners();

        this.skinManager = new net.firecraftmc.maniacore.api.skin.SkinManager(this);
        skinManager.loadFromDatabase();
        this.levelManager = new net.firecraftmc.maniacore.api.leveling.LevelManager(this);
        this.levelManager.loadFromDatabase();
        this.levelManager.generateDefaults();

        this.memoryManager = new net.firecraftmc.maniacore.memory.MemoryManager();
        this.eventManager = new net.firecraftmc.maniacore.api.events.EventManager(this);
        this.eventManager.loadData();

        this.friendsManager = new net.firecraftmc.maniacore.api.friends.FriendsManager();
        plugin.runTaskLater(() -> centurionsLib.init(), 1L);

        this.chatManager = new net.firecraftmc.maniacore.api.chat.ChatManager();
        this.chatManager.setFormatter(net.firecraftmc.maniacore.api.channel.Channel.GLOBAL, new net.firecraftmc.maniacore.api.chat.ChatFormatter(net.firecraftmc.maniacore.api.chat.ChatFormatter.LEVEL_FORMAT + " " + net.firecraftmc.maniacore.api.chat.ChatFormatter.PLAYER_NAME_FORMAT + "&8: &r" + net.firecraftmc.maniacore.api.chat.ChatFormatter.MESSAGE_FORMAT));
        net.firecraftmc.maniacore.api.chat.ChatFormatter otherFormatter = new net.firecraftmc.maniacore.api.chat.ChatFormatter(net.firecraftmc.maniacore.api.chat.ChatFormatter.CHANNEL_HEADER + " " + "{truePrefix} {truerankbasecolor}{trueName}" + "&8: {truechatcolor}{message}");
        this.chatManager.setFormatter(net.firecraftmc.maniacore.api.channel.Channel.STAFF, otherFormatter);
        this.chatManager.setFormatter(Channel.ADMIN, otherFormatter);

        this.nicknameManager = new net.firecraftmc.maniacore.api.nickname.NicknameManager();
    }

    public void registerRecordTypes() {
        this.databaseManager.registerRecordClasses(centurionsLib.getMysqlDatabase(), User.class, ChatEntry.class, CmdEntry.class, Skin.class, Level.class, IgnoreInfo.class, Statistic.class,
                FriendRequest.class, Friendship.class, FriendNotification.class, Toggle.class);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public Database getDatabase() {
        return database;
    }

    public net.firecraftmc.maniacore.api.communication.MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        net.firecraftmc.maniacore.api.redis.Redis.registerListener(messageHandler);
    }

    public void setUserManager(net.firecraftmc.maniacore.api.user.UserManager userManager) {
        this.userManager = userManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public static CenturionsCore getInstance() {
        return instance;
    }

    public static void setInstance(CenturionsCore instance) {
        CenturionsCore.instance = instance;
    }

    public net.firecraftmc.maniacore.api.server.ServerManager getServerManager() {
        return serverManager;
    }

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
        Redis.registerListener(serverManager);
    }

    public Logger getLogger() {
        return logger;
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setPlugin(CenturionsPlugin plugin) {
        this.plugin = plugin;
    }

    public CenturionsPlugin getPlugin() {
        return plugin;
    }

    public FriendsManager getFriendsManager() {
        return friendsManager;
    }

    public ChatManager getChatManager() {
        return this.chatManager;
    }

    public NicknameManager getNicknameManager() {
        return nicknameManager;
    }
}