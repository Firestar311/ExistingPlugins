package me.libraryaddict.disguise;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.utilities.DisguiseUtilities;
import me.libraryaddict.disguise.utilities.LibsPremium;
import me.libraryaddict.disguise.utilities.config.ConfigLoader;
import me.libraryaddict.disguise.utilities.modded.ModdedEntity;
import me.libraryaddict.disguise.utilities.modded.ModdedManager;
import me.libraryaddict.disguise.utilities.packets.PacketsManager;
import me.libraryaddict.disguise.utilities.parser.DisguiseParseException;
import me.libraryaddict.disguise.utilities.parser.DisguiseParser;
import me.libraryaddict.disguise.utilities.parser.DisguisePerm;
import me.libraryaddict.disguise.utilities.reflection.NmsVersion;
import me.libraryaddict.disguise.utilities.reflection.ReflectionManager;
import me.libraryaddict.disguise.utilities.translations.LibsMsg;
import me.libraryaddict.disguise.utilities.translations.TranslateType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class DisguiseConfig {
    private static HashMap<DisguisePerm, String> customDisguises = new HashMap<>();
    private static UpdatesBranch updatesBranch = UpdatesBranch.SAME_BUILDS;
    private static boolean addEntityAnimations;
    private static boolean animationPacketsEnabled;
    private static boolean catDyeable;
    private static boolean collectPacketsEnabled;
    private static boolean disableFriendlyInvisibles;
    private static boolean disabledInvisibility;
    private static boolean disguiseBlownWhenAttacked;
    private static boolean disguiseBlownWhenAttacking;
    private static boolean dynamicExpiry;
    private static boolean entityStatusPacketsEnabled;
    private static boolean equipmentPacketsEnabled;
    private static boolean explicitDisguisePermissions;
    private static boolean hideDisguisedPlayers;
    private static boolean hidingArmorFromSelf;
    private static boolean hidingCreativeEquipmentFromSelf;
    private static boolean hidingHeldItemFromSelf;
    private static boolean horseSaddleable;
    private static boolean keepDisguiseOnPlayerDeath;
    private static boolean llamaCarpetable;
    
    
    private static boolean maxHealthDeterminedByDisguisedEntity;
    
    
    private static boolean metaPacketsEnabled;
    
    
    private static boolean miscDisguisesForLivingEnabled;
    
    
    private static boolean modifyBoundingBox;
    
    
    private static boolean modifyCollisions;
    
    
    private static boolean monstersIgnoreDisguises;
    
    
    private static boolean movementPacketsEnabled;
    
    
    private static boolean nameAboveHeadAlwaysVisible;
    
    
    private static boolean nameOfPlayerShownAboveDisguise;
    
    
    private static boolean playerHideArmor;
    
    
    private static boolean saveEntityDisguises;
    
    
    private static boolean saveGameProfiles;
    
    
    private static boolean savePlayerDisguises;
    
    
    private static boolean selfDisguisesSoundsReplaced;
    
    
    private static boolean sheepDyeable;
    
    
    private static boolean showDisguisedPlayersInTab;
    
    
    private static boolean stopShulkerDisguisesFromMoving;
    
    
    private static boolean undisguiseOnWorldChange;
    
    
    private static boolean updateGameProfiles;
    
    
    private static boolean useTranslations;
    
    
    private static boolean velocitySent;
    
    
    private static boolean viewDisguises;
    
    
    private static boolean warnScoreboardConflict;
    
    
    private static boolean witherSkullPacketsEnabled;
    
    
    private static boolean wolfDyeable;
    
    
    private static int disguiseCloneExpire;
    
    
    private static int disguiseEntityExpire;
    
    
    private static int maxClonedDisguises;
    
    
    private static int uuidGeneratedVersion;
    
    
    private static boolean disablePvP;
    
    
    private static boolean disablePvE;
    
    
    private static double pvPTimer;
    
    
    private static boolean retaliationCombat;
    
    
    private static NotifyBar notifyBar = NotifyBar.ACTION_BAR;
    
    
    private static BarStyle bossBarStyle = BarStyle.SOLID;
    
    
    private static BarColor bossBarColor = BarColor.GREEN;
    private static PermissionDefault commandVisibility = PermissionDefault.TRUE;
    
    
    private static int tablistRemoveDelay;
    
    private static boolean usingReleaseBuild = true;
    
    private static boolean bisectHosted = true;
    
    private static String savedServerIp = "";
    
    private static boolean autoUpdate;
    
    private static boolean notifyUpdate;
    private static BukkitTask updaterTask;
    
    
    private static boolean tallSelfDisguises;
    
    
    private static PlayerNameType playerNameType = PlayerNameType.TEAMS;
    
    
    private static boolean overrideCustomNames;
    
    
    private static boolean randomDisguises;
    
    
    private static boolean loginPayloadPackets;
    
    
    private static boolean saveUserPreferences;
    
    private static long lastUpdateRequest;
    
    private static boolean hittingRateLimit;
    
    
    private static boolean copyPlayerTeamInfo;
    
    
    private static String nameAboveDisguise;
    
    
    private static int playerDisguisesSkinExpiresMove;
    
    
    private static boolean viewSelfDisguisesDefault;
    
    
    private static String lastGithubUpdateETag;
    
    
    private static String lastPluginUpdateVersion;
    
    
    private static boolean contactMojangServers;
    
    private static int disguiseRadiusMax;
    
    
    private static String data;

    public static HashMap<DisguisePerm, String> getCustomDisguises() {
        return customDisguises;
    }

    public static void setCustomDisguises(HashMap<DisguisePerm, String> customDisguises) {
        DisguiseConfig.customDisguises = customDisguises;
    }

    public static UpdatesBranch getUpdatesBranch() {
        return updatesBranch;
    }

    public static void setUpdatesBranch(UpdatesBranch updatesBranch) {
        DisguiseConfig.updatesBranch = updatesBranch;
    }

    public static boolean isAddEntityAnimations() {
        return addEntityAnimations;
    }

    public static void setAddEntityAnimations(boolean addEntityAnimations) {
        DisguiseConfig.addEntityAnimations = addEntityAnimations;
    }

    public static boolean isAnimationPacketsEnabled() {
        return animationPacketsEnabled;
    }

    public static boolean isCatDyeable() {
        return catDyeable;
    }

    public static void setCatDyeable(boolean catDyeable) {
        DisguiseConfig.catDyeable = catDyeable;
    }

    public static boolean isCollectPacketsEnabled() {
        return collectPacketsEnabled;
    }

    public static boolean isDisableFriendlyInvisibles() {
        return disableFriendlyInvisibles;
    }

    public static void setDisableFriendlyInvisibles(boolean disableFriendlyInvisibles) {
        DisguiseConfig.disableFriendlyInvisibles = disableFriendlyInvisibles;
    }

    public static boolean isDisabledInvisibility() {
        return disabledInvisibility;
    }

    public static void setDisabledInvisibility(boolean disabledInvisibility) {
        DisguiseConfig.disabledInvisibility = disabledInvisibility;
    }

    public static boolean isDisguiseBlownWhenAttacked() {
        return disguiseBlownWhenAttacked;
    }

    public static void setDisguiseBlownWhenAttacked(boolean disguiseBlownWhenAttacked) {
        DisguiseConfig.disguiseBlownWhenAttacked = disguiseBlownWhenAttacked;
    }

    public static boolean isDisguiseBlownWhenAttacking() {
        return disguiseBlownWhenAttacking;
    }

    public static void setDisguiseBlownWhenAttacking(boolean disguiseBlownWhenAttacking) {
        DisguiseConfig.disguiseBlownWhenAttacking = disguiseBlownWhenAttacking;
    }

    public static boolean isDynamicExpiry() {
        return dynamicExpiry;
    }

    public static void setDynamicExpiry(boolean dynamicExpiry) {
        DisguiseConfig.dynamicExpiry = dynamicExpiry;
    }

    public static boolean isEntityStatusPacketsEnabled() {
        return entityStatusPacketsEnabled;
    }

    public static boolean isEquipmentPacketsEnabled() {
        return equipmentPacketsEnabled;
    }

    public static boolean isExplicitDisguisePermissions() {
        return explicitDisguisePermissions;
    }

    public static void setExplicitDisguisePermissions(boolean explicitDisguisePermissions) {
        DisguiseConfig.explicitDisguisePermissions = explicitDisguisePermissions;
    }

    public static boolean isHideDisguisedPlayers() {
        return hideDisguisedPlayers;
    }

    public static void setHideDisguisedPlayers(boolean hideDisguisedPlayers) {
        DisguiseConfig.hideDisguisedPlayers = hideDisguisedPlayers;
    }

    public static boolean isHidingArmorFromSelf() {
        return hidingArmorFromSelf;
    }

    public static void setHidingArmorFromSelf(boolean hidingArmorFromSelf) {
        DisguiseConfig.hidingArmorFromSelf = hidingArmorFromSelf;
    }

    public static boolean isHidingCreativeEquipmentFromSelf() {
        return hidingCreativeEquipmentFromSelf;
    }

    public static void setHidingCreativeEquipmentFromSelf(boolean hidingCreativeEquipmentFromSelf) {
        DisguiseConfig.hidingCreativeEquipmentFromSelf = hidingCreativeEquipmentFromSelf;
    }

    public static boolean isHidingHeldItemFromSelf() {
        return hidingHeldItemFromSelf;
    }

    public static void setHidingHeldItemFromSelf(boolean hidingHeldItemFromSelf) {
        DisguiseConfig.hidingHeldItemFromSelf = hidingHeldItemFromSelf;
    }

    public static boolean isHorseSaddleable() {
        return horseSaddleable;
    }

    public static void setHorseSaddleable(boolean horseSaddleable) {
        DisguiseConfig.horseSaddleable = horseSaddleable;
    }

    public static boolean isKeepDisguiseOnPlayerDeath() {
        return keepDisguiseOnPlayerDeath;
    }

    public static void setKeepDisguiseOnPlayerDeath(boolean keepDisguiseOnPlayerDeath) {
        DisguiseConfig.keepDisguiseOnPlayerDeath = keepDisguiseOnPlayerDeath;
    }

    public static boolean isLlamaCarpetable() {
        return llamaCarpetable;
    }

    public static void setLlamaCarpetable(boolean llamaCarpetable) {
        DisguiseConfig.llamaCarpetable = llamaCarpetable;
    }

    public static boolean isMaxHealthDeterminedByDisguisedEntity() {
        return maxHealthDeterminedByDisguisedEntity;
    }

    public static void setMaxHealthDeterminedByDisguisedEntity(boolean maxHealthDeterminedByDisguisedEntity) {
        DisguiseConfig.maxHealthDeterminedByDisguisedEntity = maxHealthDeterminedByDisguisedEntity;
    }

    public static boolean isMetaPacketsEnabled() {
        return metaPacketsEnabled;
    }

    public static void setMetaPacketsEnabled(boolean metaPacketsEnabled) {
        DisguiseConfig.metaPacketsEnabled = metaPacketsEnabled;
    }

    public static boolean isMiscDisguisesForLivingEnabled() {
        return miscDisguisesForLivingEnabled;
    }

    public static boolean isModifyBoundingBox() {
        return modifyBoundingBox;
    }

    public static void setModifyBoundingBox(boolean modifyBoundingBox) {
        DisguiseConfig.modifyBoundingBox = modifyBoundingBox;
    }

    public static boolean isModifyCollisions() {
        return modifyCollisions;
    }

    public static void setModifyCollisions(boolean modifyCollisions) {
        DisguiseConfig.modifyCollisions = modifyCollisions;
    }

    public static boolean isMonstersIgnoreDisguises() {
        return monstersIgnoreDisguises;
    }

    public static void setMonstersIgnoreDisguises(boolean monstersIgnoreDisguises) {
        DisguiseConfig.monstersIgnoreDisguises = monstersIgnoreDisguises;
    }

    public static boolean isMovementPacketsEnabled() {
        return movementPacketsEnabled;
    }

    public static boolean isNameAboveHeadAlwaysVisible() {
        return nameAboveHeadAlwaysVisible;
    }

    public static void setNameAboveHeadAlwaysVisible(boolean nameAboveHeadAlwaysVisible) {
        DisguiseConfig.nameAboveHeadAlwaysVisible = nameAboveHeadAlwaysVisible;
    }

    public static boolean isNameOfPlayerShownAboveDisguise() {
        return nameOfPlayerShownAboveDisguise;
    }

    public static void setNameOfPlayerShownAboveDisguise(boolean nameOfPlayerShownAboveDisguise) {
        DisguiseConfig.nameOfPlayerShownAboveDisguise = nameOfPlayerShownAboveDisguise;
    }

    public static boolean isPlayerHideArmor() {
        return playerHideArmor;
    }

    public static void setPlayerHideArmor(boolean playerHideArmor) {
        DisguiseConfig.playerHideArmor = playerHideArmor;
    }

    public static boolean isSaveEntityDisguises() {
        return saveEntityDisguises;
    }

    public static void setSaveEntityDisguises(boolean saveEntityDisguises) {
        DisguiseConfig.saveEntityDisguises = saveEntityDisguises;
    }

    public static boolean isSaveGameProfiles() {
        return saveGameProfiles;
    }

    public static void setSaveGameProfiles(boolean saveGameProfiles) {
        DisguiseConfig.saveGameProfiles = saveGameProfiles;
    }

    public static boolean isSavePlayerDisguises() {
        return savePlayerDisguises;
    }

    public static void setSavePlayerDisguises(boolean savePlayerDisguises) {
        DisguiseConfig.savePlayerDisguises = savePlayerDisguises;
    }

    public static boolean isSelfDisguisesSoundsReplaced() {
        return selfDisguisesSoundsReplaced;
    }

    public static void setSelfDisguisesSoundsReplaced(boolean selfDisguisesSoundsReplaced) {
        DisguiseConfig.selfDisguisesSoundsReplaced = selfDisguisesSoundsReplaced;
    }

    public static boolean isSheepDyeable() {
        return sheepDyeable;
    }

    public static void setSheepDyeable(boolean sheepDyeable) {
        DisguiseConfig.sheepDyeable = sheepDyeable;
    }

    public static boolean isShowDisguisedPlayersInTab() {
        return showDisguisedPlayersInTab;
    }

    public static void setShowDisguisedPlayersInTab(boolean showDisguisedPlayersInTab) {
        DisguiseConfig.showDisguisedPlayersInTab = showDisguisedPlayersInTab;
    }

    public static boolean isStopShulkerDisguisesFromMoving() {
        return stopShulkerDisguisesFromMoving;
    }

    public static void setStopShulkerDisguisesFromMoving(boolean stopShulkerDisguisesFromMoving) {
        DisguiseConfig.stopShulkerDisguisesFromMoving = stopShulkerDisguisesFromMoving;
    }

    public static boolean isUndisguiseOnWorldChange() {
        return undisguiseOnWorldChange;
    }

    public static void setUndisguiseOnWorldChange(boolean undisguiseOnWorldChange) {
        DisguiseConfig.undisguiseOnWorldChange = undisguiseOnWorldChange;
    }

    public static boolean isUpdateGameProfiles() {
        return updateGameProfiles;
    }

    public static void setUpdateGameProfiles(boolean updateGameProfiles) {
        DisguiseConfig.updateGameProfiles = updateGameProfiles;
    }

    public static boolean isUseTranslations() {
        return useTranslations;
    }

    public static boolean isVelocitySent() {
        return velocitySent;
    }

    public static void setVelocitySent(boolean velocitySent) {
        DisguiseConfig.velocitySent = velocitySent;
    }

    public static boolean isViewDisguises() {
        return viewDisguises;
    }

    public static void setViewDisguises(boolean viewDisguises) {
        DisguiseConfig.viewDisguises = viewDisguises;
    }

    public static boolean isWarnScoreboardConflict() {
        return warnScoreboardConflict;
    }

    public static void setWarnScoreboardConflict(boolean warnScoreboardConflict) {
        DisguiseConfig.warnScoreboardConflict = warnScoreboardConflict;
    }

    public static boolean isWitherSkullPacketsEnabled() {
        return witherSkullPacketsEnabled;
    }

    public static void setWitherSkullPacketsEnabled(boolean witherSkullPacketsEnabled) {
        DisguiseConfig.witherSkullPacketsEnabled = witherSkullPacketsEnabled;
    }

    public static boolean isWolfDyeable() {
        return wolfDyeable;
    }

    public static void setWolfDyeable(boolean wolfDyeable) {
        DisguiseConfig.wolfDyeable = wolfDyeable;
    }

    public static int getDisguiseCloneExpire() {
        return disguiseCloneExpire;
    }

    public static void setDisguiseCloneExpire(int disguiseCloneExpire) {
        DisguiseConfig.disguiseCloneExpire = disguiseCloneExpire;
    }

    public static int getDisguiseEntityExpire() {
        return disguiseEntityExpire;
    }

    public static void setDisguiseEntityExpire(int disguiseEntityExpire) {
        DisguiseConfig.disguiseEntityExpire = disguiseEntityExpire;
    }

    public static int getMaxClonedDisguises() {
        return maxClonedDisguises;
    }

    public static void setMaxClonedDisguises(int maxClonedDisguises) {
        DisguiseConfig.maxClonedDisguises = maxClonedDisguises;
    }

    public static int getUuidGeneratedVersion() {
        return uuidGeneratedVersion;
    }

    public static void setUuidGeneratedVersion(int uuidGeneratedVersion) {
        DisguiseConfig.uuidGeneratedVersion = uuidGeneratedVersion;
    }

    public static boolean isDisablePvP() {
        return disablePvP;
    }

    public static void setDisablePvP(boolean disablePvP) {
        DisguiseConfig.disablePvP = disablePvP;
    }

    public static boolean isDisablePvE() {
        return disablePvE;
    }

    public static void setDisablePvE(boolean disablePvE) {
        DisguiseConfig.disablePvE = disablePvE;
    }

    public static double getPvPTimer() {
        return pvPTimer;
    }

    public static void setPvPTimer(double pvPTimer) {
        DisguiseConfig.pvPTimer = pvPTimer;
    }

    public static boolean isRetaliationCombat() {
        return retaliationCombat;
    }

    public static void setRetaliationCombat(boolean retaliationCombat) {
        DisguiseConfig.retaliationCombat = retaliationCombat;
    }

    public static NotifyBar getNotifyBar() {
        return notifyBar;
    }

    public static void setNotifyBar(NotifyBar notifyBar) {
        DisguiseConfig.notifyBar = notifyBar;
    }

    public static BarStyle getBossBarStyle() {
        return bossBarStyle;
    }

    public static void setBossBarStyle(BarStyle bossBarStyle) {
        DisguiseConfig.bossBarStyle = bossBarStyle;
    }

    public static BarColor getBossBarColor() {
        return bossBarColor;
    }

    public static void setBossBarColor(BarColor bossBarColor) {
        DisguiseConfig.bossBarColor = bossBarColor;
    }

    public static int getTablistRemoveDelay() {
        return tablistRemoveDelay;
    }

    public static void setTablistRemoveDelay(int tablistRemoveDelay) {
        DisguiseConfig.tablistRemoveDelay = tablistRemoveDelay;
    }

    public static boolean isUsingReleaseBuild() {
        return usingReleaseBuild;
    }

    public static void setUsingReleaseBuild(boolean usingReleaseBuild) {
        DisguiseConfig.usingReleaseBuild = usingReleaseBuild;
    }

    public static boolean isBisectHosted() {
        return bisectHosted;
    }

    public static void setBisectHosted(boolean bisectHosted) {
        DisguiseConfig.bisectHosted = bisectHosted;
    }

    public static String getSavedServerIp() {
        return savedServerIp;
    }

    public static void setSavedServerIp(String savedServerIp) {
        DisguiseConfig.savedServerIp = savedServerIp;
    }

    public static boolean isAutoUpdate() {
        return autoUpdate;
    }

    public static boolean isNotifyUpdate() {
        return notifyUpdate;
    }

    public static BukkitTask getUpdaterTask() {
        return updaterTask;
    }

    public static void setUpdaterTask(BukkitTask updaterTask) {
        DisguiseConfig.updaterTask = updaterTask;
    }

    public static boolean isTallSelfDisguises() {
        return tallSelfDisguises;
    }

    public static void setTallSelfDisguises(boolean tallSelfDisguises) {
        DisguiseConfig.tallSelfDisguises = tallSelfDisguises;
    }

    public static PlayerNameType getPlayerNameType() {
        return playerNameType;
    }

    public static void setPlayerNameType(PlayerNameType playerNameType) {
        DisguiseConfig.playerNameType = playerNameType;
    }

    public static boolean isOverrideCustomNames() {
        return overrideCustomNames;
    }

    public static void setOverrideCustomNames(boolean overrideCustomNames) {
        DisguiseConfig.overrideCustomNames = overrideCustomNames;
    }

    public static boolean isRandomDisguises() {
        return randomDisguises;
    }

    public static void setRandomDisguises(boolean randomDisguises) {
        DisguiseConfig.randomDisguises = randomDisguises;
    }

    public static boolean isLoginPayloadPackets() {
        return loginPayloadPackets;
    }

    public static void setLoginPayloadPackets(boolean loginPayloadPackets) {
        DisguiseConfig.loginPayloadPackets = loginPayloadPackets;
    }

    public static boolean isSaveUserPreferences() {
        return saveUserPreferences;
    }

    public static void setSaveUserPreferences(boolean saveUserPreferences) {
        DisguiseConfig.saveUserPreferences = saveUserPreferences;
    }

    public static long getLastUpdateRequest() {
        return lastUpdateRequest;
    }

    public static boolean isHittingRateLimit() {
        return hittingRateLimit;
    }

    public static boolean isCopyPlayerTeamInfo() {
        return copyPlayerTeamInfo;
    }

    public static void setCopyPlayerTeamInfo(boolean copyPlayerTeamInfo) {
        DisguiseConfig.copyPlayerTeamInfo = copyPlayerTeamInfo;
    }

    public static String getNameAboveDisguise() {
        return nameAboveDisguise;
    }

    public static void setNameAboveDisguise(String nameAboveDisguise) {
        DisguiseConfig.nameAboveDisguise = nameAboveDisguise;
    }

    public static int getPlayerDisguisesSkinExpiresMove() {
        return playerDisguisesSkinExpiresMove;
    }

    public static void setPlayerDisguisesSkinExpiresMove(int playerDisguisesSkinExpiresMove) {
        DisguiseConfig.playerDisguisesSkinExpiresMove = playerDisguisesSkinExpiresMove;
    }

    public static boolean isViewSelfDisguisesDefault() {
        return viewSelfDisguisesDefault;
    }

    public static void setViewSelfDisguisesDefault(boolean viewSelfDisguisesDefault) {
        DisguiseConfig.viewSelfDisguisesDefault = viewSelfDisguisesDefault;
    }

    public static String getLastGithubUpdateETag() {
        return lastGithubUpdateETag;
    }

    public static void setLastGithubUpdateETag(String lastGithubUpdateETag) {
        DisguiseConfig.lastGithubUpdateETag = lastGithubUpdateETag;
    }

    public static String getLastPluginUpdateVersion() {
        return lastPluginUpdateVersion;
    }

    public static void setLastPluginUpdateVersion(String lastPluginUpdateVersion) {
        DisguiseConfig.lastPluginUpdateVersion = lastPluginUpdateVersion;
    }

    public static boolean isContactMojangServers() {
        return contactMojangServers;
    }

    public static void setContactMojangServers(boolean contactMojangServers) {
        DisguiseConfig.contactMojangServers = contactMojangServers;
    }

    public static int getDisguiseRadiusMax() {
        return disguiseRadiusMax;
    }

    public static void setDisguiseRadiusMax(int disguiseRadiusMax) {
        DisguiseConfig.disguiseRadiusMax = disguiseRadiusMax;
    }

    public static String getData() {
        return data;
    }

    public static void setData(String data) {
        DisguiseConfig.data = data;
    }

    public static boolean isArmorstandsName() {
        return getPlayerNameType() == PlayerNameType.ARMORSTANDS;
    }

    public static boolean isExtendedNames() {
        return getPlayerNameType() == PlayerNameType.EXTENDED;
    }

    public static void setAutoUpdate(boolean update) {
        if (isAutoUpdate() == update) {
            return;
        }

        autoUpdate = update;
        doUpdaterTask();
    }

    public static void setNotifyUpdate(boolean update) {
        if (isNotifyUpdate() == update) {
            return;
        }

        notifyUpdate = update;
        doUpdaterTask();
    }

    public static void setLastUpdateRequest(long lastRequest) {
        if (lastRequest <= getLastUpdateRequest()) {
            return;
        }

        lastUpdateRequest = lastRequest;
        saveInternalConfig();
    }

    private static void doUpdaterTask() {
        boolean startTask = isAutoUpdate() || isNotifyUpdate() ||
                "1592".equals((LibsPremium.getPaidInformation() == null ? LibsPremium.getPluginInformation() : LibsPremium.getPaidInformation()).getUserID());

        // Don't ever run the auto updater on a custom build..
        if (!LibsDisguises.getInstance().isNumberedBuild()) {
            return;
        }

        if (updaterTask == null != startTask) {
            return;
        }

        if (!startTask) {
            updaterTask.cancel();
            updaterTask = null;
            return;
        }

        int timer = (int) (TimeUnit.HOURS.toSeconds(isHittingRateLimit() ? 36 : 12) * 20);

        // Get the ticks since last update
        long timeSinceLast = (System.currentTimeMillis() - getLastUpdateRequest()) / 50;

        // Next update check will be in 30 minutes, or the timer - elapsed time. Whatever is greater
        timeSinceLast = Math.max(30 * 60 * 20, timer - timeSinceLast);

        updaterTask = Bukkit.getScheduler().runTaskTimerAsynchronously(LibsDisguises.getInstance(), new Runnable() {
            @Override
            public void run() {
                LibsDisguises.getInstance().getUpdateChecker().doAutoUpdateCheck();
            }
        }, timeSinceLast, timer);
    }

    public static void setUsingReleaseBuilds(boolean useReleaseBuilds) {
        if (useReleaseBuilds == isUsingReleaseBuild()) {
            return;
        }

        usingReleaseBuild = useReleaseBuilds;
        saveInternalConfig();
    }

    public static void setHittingRateLimit(boolean hitRateLimit) {
        if (hitRateLimit == isHittingRateLimit()) {
            return;
        }

        hittingRateLimit = hitRateLimit;
        saveInternalConfig();
        doUpdaterTask();
    }

    public static void setBisectHosted(boolean isBisectHosted, String serverIP) {
        if (isBisectHosted() == isBisectHosted && getSavedServerIp().equals(serverIP)) {
            return;
        }

        bisectHosted = isBisectHosted;
        savedServerIp = serverIP;
        saveInternalConfig();
    }

    public static void loadInternalConfig() {
        File internalFile = new File(LibsDisguises.getInstance().getDataFolder(), "internal.yml");

        if (!internalFile.exists()) {
            saveInternalConfig();
        }

        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(internalFile);

        bisectHosted = configuration.getBoolean("Bisect-Hosted", isBisectHosted());
        savedServerIp = configuration.getString("Server-IP", getSavedServerIp());
        usingReleaseBuild = configuration.getBoolean("ReleaseBuild", isUsingReleaseBuild());
        lastUpdateRequest = configuration.getLong("LastUpdateRequest", 0L);
        hittingRateLimit = configuration.getBoolean("HittingRateLimit", false);
        lastGithubUpdateETag = configuration.getString("LastGithubETag", null);
        lastPluginUpdateVersion = configuration.getString("LastPluginVersion", null);
        data = configuration.getString("Data", null);

        if (!configuration.contains("Bisect-Hosted") || !configuration.contains("Server-IP") || !configuration.contains("ReleaseBuild")) {
            saveInternalConfig();
        }
    }

    public static void saveInternalConfig() {
        File internalFile = new File(LibsDisguises.getInstance().getDataFolder(), "internal.yml");

        String internalConfig = ReflectionManager.getResourceAsString(LibsDisguises.getInstance().getFile(), "internal.yml");

        // Bisect hosted, server ip, release builds
        for (Object s : new Object[]{isBisectHosted(), getSavedServerIp(), isUsingReleaseBuild(), getLastUpdateRequest(), isHittingRateLimit(),
                getLastGithubUpdateETag(), getLastPluginUpdateVersion(), getData()}) {
            internalConfig = internalConfig.replaceFirst("%data%", "" + s);
        }

        internalFile.delete();

        try {
            internalFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(internalFile, "UTF-8")) {
            writer.write(internalConfig);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static PermissionDefault getCommandVisibility() {
        return commandVisibility;
    }

    public static void setCommandVisibility(PermissionDefault permissionDefault) {
        if (permissionDefault == null || getCommandVisibility() == permissionDefault) {
            return;
        }

        commandVisibility = permissionDefault;

        for (Permission perm : LibsDisguises.getInstance().getDescription().getPermissions()) {
            if (!perm.getName().startsWith("libsdisguises.seecmd")) {
                continue;
            }

            perm.setDefault(getCommandVisibility());
        }
    }

    private DisguiseConfig() {
    }

    public static int getUUIDGeneratedVersion() {
        return uuidGeneratedVersion;
    }

    public static void setUUIDGeneratedVersion(int uuidVersion) {
        uuidGeneratedVersion = uuidVersion;
    }

    public static Entry<DisguisePerm, Disguise> getCustomDisguise(String disguise) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Custom Disguises should not be called async!");
        }

        Entry<DisguisePerm, String> entry = getRawCustomDisguise(disguise);

        if (entry == null) {
            return null;
        }

        try {
            return new HashMap.SimpleEntry(entry.getKey(), DisguiseParser.parseDisguise(entry.getValue()));
        } catch (Throwable e) {
            DisguiseUtilities.getLogger().warning("Error when attempting to grab the custom disguise " + disguise);
            e.printStackTrace();
        }

        return null;
    }

    public static Entry<DisguisePerm, Disguise> getCustomDisguise(Entity target, String disguise) throws Throwable {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Custom Disguises should not be called async!");
        }

        Entry<DisguisePerm, String> entry = getRawCustomDisguise(disguise);

        if (entry == null) {
            return null;
        }

        return new HashMap.SimpleEntry(entry.getKey(), DisguiseParser.parseDisguise(Bukkit.getConsoleSender(), target, entry.getValue()));
    }

    public static Entry<DisguisePerm, Disguise> getCustomDisguise(CommandSender invoker, Entity target, String disguise) throws Throwable {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Custom Disguises should not be called async!");
        }

        Entry<DisguisePerm, String> entry = getRawCustomDisguise(disguise);

        if (entry == null) {
            return null;
        }

        return new HashMap.SimpleEntry(entry.getKey(), DisguiseParser.parseDisguise(invoker, target, entry.getValue()));
    }

    public static boolean isScoreboardNames() {
        return getPlayerNameType() != PlayerNameType.VANILLA;
    }

    public static void removeCustomDisguise(String disguise) {
        for (DisguisePerm entry : customDisguises.keySet()) {
            String name = entry.toReadable();

            if (!name.equalsIgnoreCase(disguise) && !name.replaceAll("_", "").equalsIgnoreCase(disguise)) {
                continue;
            }

            customDisguises.remove(entry);
            break;
        }
    }

    public static Entry<DisguisePerm, String> getRawCustomDisguise(String disguise) {
        for (Entry<DisguisePerm, String> entry : customDisguises.entrySet()) {
            String name = entry.getKey().toReadable();

            if (!name.equalsIgnoreCase(disguise) && !name.replaceAll("_", "").equalsIgnoreCase(disguise)) {
                continue;
            }

            return entry;
        }

        return null;
    }

    public static void setUseTranslations(boolean setUseTranslations) {
        useTranslations = setUseTranslations;

        TranslateType.refreshTranslations();
    }

    public static void loadConfig() {
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.saveMissingConfigs();

        loadModdedDisguiseTypes();

        File skinsFolder = new File(LibsDisguises.getInstance().getDataFolder(), "Skins");

        if (!skinsFolder.exists()) {
            skinsFolder.mkdir();

            File explain = new File(skinsFolder, "README");

            try {
                explain.createNewFile();

                try (PrintWriter out = new PrintWriter(explain)) {
                    out.println("This folder is used to store .png files for uploading with the /savedisguise or " + "/grabskin " + "commands");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ConfigurationSection config = configLoader.load();

        PacketsManager.setViewDisguisesListener(true);

        setAddEntityAnimations(config.getBoolean("AddEntityAnimations"));
        setAnimationPacketsEnabled(config.getBoolean("PacketsEnabled.Animation"));
        setCatDyeable(config.getBoolean("DyeableCat"));
        setCollectPacketsEnabled(config.getBoolean("PacketsEnabled.Collect"));
        setDisableFriendlyInvisibles(config.getBoolean("Scoreboard.DisableFriendlyInvisibles"));
        setDisabledInvisibility(config.getBoolean("DisableInvisibility"));
        setDisablePvP(config.getBoolean("DisablePvP"));
        setDisablePvE(config.getBoolean("DisablePvE"));
        setPvPTimer(config.getDouble("PvPTimer"));
        setDisguiseBlownWhenAttacked(config.getBoolean("BlowDisguises", config.getBoolean("BlowDisguisesWhenAttacked")));
        setDisguiseBlownWhenAttacking(config.getBoolean("BlowDisguises", config.getBoolean("BlowDisguisesWhenAttacking")));
        setDisguiseCloneExpire(config.getInt("DisguiseCloneExpire"));
        setDisguiseEntityExpire(config.getInt("DisguiseEntityExpire"));
        setDynamicExpiry(config.getBoolean("DynamicExpiry"));
        setEntityStatusPacketsEnabled(config.getBoolean("PacketsEnabled.EntityStatus"));
        setEquipmentPacketsEnabled(config.getBoolean("PacketsEnabled.Equipment"));
        setExplicitDisguisePermissions(config.getBoolean("Permissions.ExplicitDisguises"));
        setHideArmorFromSelf(config.getBoolean("RemoveArmor"));
        setHidingCreativeEquipmentFromSelf(config.getBoolean("RemoveCreativeEquipment"));
        setHideDisguisedPlayers(config.getBoolean("HideDisguisedPlayersFromTab"));
        setHideHeldItemFromSelf(config.getBoolean("RemoveHeldItem"));
        setHorseSaddleable(config.getBoolean("SaddleableHorse"));
        setKeepDisguiseOnPlayerDeath(config.getBoolean("KeepDisguises.PlayerDeath"));
        setLlamaCarpetable(config.getBoolean("CarpetableLlama"));
        setMaxClonedDisguises(config.getInt("DisguiseCloneSize"));
        setMaxHealthDeterminedByDisguisedEntity(config.getBoolean("MaxHealthDeterminedByEntity"));
        setMetaPacketsEnabled(config.getBoolean("PacketsEnabled.Metadata"));
        setLoginPayloadPackets(config.getBoolean("PacketsEnabled.LoginPayload"));
        setMiscDisguisesForLivingEnabled(config.getBoolean("MiscDisguisesForLiving"));
        setModifyBoundingBox(config.getBoolean("ModifyBoundingBox"));
        setModifyCollisions(config.getBoolean("Scoreboard.Collisions"));
        setMonstersIgnoreDisguises(config.getBoolean("MonstersIgnoreDisguises"));
        setMovementPacketsEnabled(config.getBoolean("PacketsEnabled.Movement"));
        setNameAboveHeadAlwaysVisible(config.getBoolean("NameAboveHeadAlwaysVisible"));
        setNameOfPlayerShownAboveDisguise(config.getBoolean("ShowNamesAboveDisguises"));
        setNameAboveDisguise(config.getString("NameAboveDisguise"));
        setPlayerHideArmor(config.getBoolean("PlayerHideArmor"));
        setRetaliationCombat(config.getBoolean("RetaliationCombat"));
        setSaveGameProfiles(config.getBoolean("SaveGameProfiles"));
        setSavePlayerDisguises(config.getBoolean("SaveDisguises.Players"));
        setSaveEntityDisguises(config.getBoolean("SaveDisguises.Entities"));
        setSelfDisguisesSoundsReplaced(config.getBoolean("HearSelfDisguise"));
        setSheepDyeable(config.getBoolean("DyeableSheep"));
        setShowDisguisedPlayersInTab(config.getBoolean("ShowPlayerDisguisesInTab"));
        setSoundsEnabled(config.getBoolean("DisguiseSounds"));
        setStopShulkerDisguisesFromMoving(config.getBoolean("StopShulkerDisguisesFromMoving", true));
        setUUIDGeneratedVersion(config.getInt("UUIDVersion"));
        setUndisguiseOnWorldChange(config.getBoolean("UndisguiseOnWorldChange"));
        setUpdateGameProfiles(config.getBoolean("UpdateGameProfiles"));
        setUseTranslations(config.getBoolean("Translations"));
        setVelocitySent(config.getBoolean("SendVelocity"));
        setViewDisguises(config.getBoolean("ViewSelfDisguises"));
        setWarnScoreboardConflict(config.getBoolean("Scoreboard.WarnConflict"));
        setCopyPlayerTeamInfo(config.getBoolean("Scoreboard.CopyPlayerTeamInfo"));
        setWitherSkullPacketsEnabled(config.getBoolean("PacketsEnabled.WitherSkull"));
        setWolfDyeable(config.getBoolean("DyeableWolf"));
        setTablistRemoveDelay(config.getInt("TablistRemoveDelay"));
        setAutoUpdate(config.getBoolean("AutoUpdate"));
        setTallSelfDisguises(config.getBoolean("TallSelfDisguises"));
        setOverrideCustomNames(config.getBoolean("OverrideCustomNames"));
        setRandomDisguises(config.getBoolean("RandomDisguiseOptions"));
        setSaveUserPreferences(config.getBoolean("SaveUserPreferences"));
        setPlayerDisguisesSkinExpiresMove(config.getInt("PlayerDisguisesTablistExpiresMove"));
        setViewSelfDisguisesDefault(config.getBoolean("ViewSelfDisguisesDefault"));
        setContactMojangServers(config.getBoolean("ContactMojangServers"));
        setDisguiseRadiusMax(config.getInt("DisguiseRadiusMax"));
        String apiKey = config.getString("MineSkinAPIKey");

        if (apiKey != null && apiKey.matches("[a-zA-Z0-9]{8,}")) {
            DisguiseUtilities.getMineSkinAPI().setApiKey(apiKey);
        } else if (apiKey != null && apiKey.length() > 8) {
            DisguiseUtilities.getLogger().warning("API Key provided for MineSkin does not appear to be in a valid format!");
        }

        if (!LibsPremium.isPremium() && (isSavePlayerDisguises() || isSaveEntityDisguises())) {
            DisguiseUtilities.getLogger().warning("You must purchase the plugin to use saved disguises!");
        }

        try {
            setPlayerNameType(PlayerNameType.valueOf(config.getString("PlayerNames").toUpperCase(Locale.ENGLISH)));
        } catch (Exception ex) {
            DisguiseUtilities.getLogger().warning("Cannot parse '" + config.getString("PlayerNames") + "' to a valid option for PlayerNames");
        }

        try {
            setNotifyBar(NotifyBar.valueOf(config.getString("NotifyBar").toUpperCase(Locale.ENGLISH)));

            if (getNotifyBar() == NotifyBar.BOSS_BAR && !NmsVersion.v1_13.isSupported()) {
                DisguiseUtilities.getLogger()
                        .warning("BossBars hasn't been implemented properly in 1.12 due to api restrictions, falling back to " + "ACTION_BAR");

                setNotifyBar(NotifyBar.ACTION_BAR);
            }
        } catch (Exception ex) {
            DisguiseUtilities.getLogger().warning("Cannot parse '" + config.getString("NotifyBar") + "' to a valid option for NotifyBar");
        }

        try {
            setBossBarColor(BarColor.valueOf(config.getString("BossBarColor").toUpperCase(Locale.ENGLISH)));
        } catch (Exception ex) {
            DisguiseUtilities.getLogger().warning("Cannot parse '" + config.getString("BossBarColor") + "' to a valid option for BossBarColor");
        }

        try {
            setBossBarStyle(BarStyle.valueOf(config.getString("BossBarStyle").toUpperCase(Locale.ENGLISH)));
        } catch (Exception ex) {
            DisguiseUtilities.getLogger().warning("Cannot parse '" + config.getString("BossBarStyle") + "' to a valid option for BossBarStyle");
        }

        try {
            setUpdatesBranch(UpdatesBranch.valueOf(config.getString("UpdatesBranch").toUpperCase(Locale.ENGLISH)));
        } catch (Exception ex) {
            DisguiseUtilities.getLogger().warning("Cannot parse '" + config.getString("UpdatesBranch") + "' to a valid option for UpdatesBranch");
        }

        PermissionDefault commandVisibility = PermissionDefault.getByName(config.getString("Permissions.SeeCommands"));

        if (commandVisibility == null) {
            DisguiseUtilities.getLogger()
                    .warning("Invalid option '" + config.getString("Permissions.SeeCommands") + "' for Permissions.SeeCommands when loading config!");
        } else {
            setCommandVisibility(commandVisibility);
        }

        loadCustomDisguises();

        // Another wee trap for the non-legit
        if ("%%__USER__%%".equals("12345") && getCustomDisguises().size() > 10) {
            setSoundsEnabled(false);

            // Lets remove randomly half the custom disguises hey
            Iterator<Entry<DisguisePerm, String>> itel = getCustomDisguises().entrySet().iterator();

            int i = 0;
            while (itel.hasNext()) {
                itel.next();

                if (new Random().nextBoolean()) {
                    itel.remove();
                }
            }
        }

        boolean verbose;

        if (config.contains("VerboseConfig")) {
            verbose = config.getBoolean("VerboseConfig");
        } else {
            DisguiseUtilities.getLogger().info("As 'VerboseConfig' hasn't been set, it is assumed true. Set it in your config to remove " + "these messages!");
            verbose = true;
        }

        boolean changed = config.getBoolean("ChangedConfig");

        if (verbose || changed) {
            ArrayList<String> returns = doOutput(config, changed, verbose);

            if (!returns.isEmpty()) {
                DisguiseUtilities.getLogger().info("This is not an error! Now outputting " + (verbose ? "missing " : "") +
                        (changed ? (verbose ? "and " : "") + "changed/invalid " : "") + "config values");

                for (String v : returns) {
                    DisguiseUtilities.getLogger().info(v);
                }
            }
        }

        int missingConfigs = doOutput(config, false, true).size();

        if (missingConfigs > 0) {
            if (config.getBoolean("UpdateConfig", true)) {
                configLoader.saveDefaultConfigs();
                DisguiseUtilities.getLogger().info("Config has been auto-updated!");
            } else if (!verbose) {
                DisguiseUtilities.getLogger().warning("Your config is missing " + missingConfigs + " options! Please consider regenerating your config!");
                DisguiseUtilities.getLogger().info("You can also add the missing entries yourself! Try '/libsdisguises config'");
            }
        } else {
            DisguiseUtilities.getLogger().info("Config is up to date!");
        }
    }

    public static void loadModdedDisguiseTypes() {
        File disguisesFile = new File(LibsDisguises.getInstance().getDataFolder(), "configs/disguises.yml");

        if (!disguisesFile.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(disguisesFile);

        if (!config.contains("Custom-Entities")) {
            return;
        }

        ArrayList<String> channels = new ArrayList<>();

        for (String name : config.getConfigurationSection("Custom-Entities").getKeys(false)) {
            try {
                if (!name.matches("[a-zA-Z0-9_]+")) {
                    DisguiseUtilities.getLogger().severe("Invalid modded disguise name '" + name + "'");
                    continue;
                }

                ConfigurationSection section = config.getConfigurationSection("Custom-Entities." + name);

                if (!section.contains("Name")) {
                    DisguiseUtilities.getLogger().severe("No mod:entity 'Name' provided for '" + name + "'");
                    continue;
                }

                String key = section.getString("Name");

                // Lets not do sanity checking and blame it on the config author
                // Well, maybe just a : check...
                if (!key.contains(":") || key.contains(".")) {
                    DisguiseUtilities.getLogger().severe("Invalid modded name '" + key + "' in disguises.yml!");
                    continue;
                }

                boolean register = section.getBoolean("Register", true);
                boolean living = section.getString("Type", "LIVING").equalsIgnoreCase("LIVING");
                String type = section.getString("Type");
                String mod = section.getString("Mod");
                String[] version = mod == null || !section.contains("Version") ? null : section.getString("Version").split(",");
                String requireMessage = mod == null ? null : section.getString("Required");

                if (section.contains("Channels")) {
                    for (String s : section.getString("Channels").split(",")) {
                        if (!s.contains("|")) {
                            s += "|";
                            DisguiseUtilities.getLogger().severe("No channel version declared for " + s);
                        }

                        channels.add(s);
                    }
                }

                if (requireMessage != null) {
                    requireMessage = DisguiseUtilities.translateAlternateColorCodes(requireMessage);
                }

                ModdedEntity entity = new ModdedEntity(null, name, living, mod, version, requireMessage, 0);

                if (ModdedManager.getModdedEntity(name) != null) {
                    DisguiseUtilities.getLogger().info("Modded entity " + name + " has already been " + (register ? "registered" : "added"));
                    continue;
                }

                ModdedManager
                        .registerModdedEntity(new NamespacedKey(key.substring(0, key.indexOf(":")), key.substring(key.indexOf(":") + 1)), entity, register);

                DisguiseUtilities.getLogger().info("Modded entity " + name + " has been " + (register ? "registered" : "added"));
            } catch (Exception ex) {
                DisguiseUtilities.getLogger().severe("Error while trying to register modded entity " + name);
                ex.printStackTrace();
            }
        }

        new ModdedManager(channels);
    }

    public static ArrayList<String> doOutput(boolean informChangedUnknown, boolean informMissing) {
        return doOutput(new ConfigLoader().load(), informChangedUnknown, informMissing);
    }

    public static ArrayList<String> doOutput(ConfigurationSection config, boolean informChangedUnknown, boolean informMissing) {
        HashMap<String, Object> configs = new HashMap<>();
        ConfigurationSection defaultSection = config.getDefaultSection();

        if (defaultSection == null) {
            defaultSection = new ConfigLoader().loadDefaults();
        }

        ArrayList<String> returns = new ArrayList<>();

        for (String key : defaultSection.getKeys(true)) {
            if (defaultSection.isConfigurationSection(key)) {
                continue;
            }

            configs.put(key, defaultSection.get(key));
        }

        for (String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) {
                continue;
            }

            if (!configs.containsKey(key)) {
                if (informChangedUnknown) {
                    returns.add("Unknown config option '" + key + ": " + config.get(key) + "'");
                }
                continue;
            }

            if (!configs.get(key).equals(config.get(key))) {
                if (informChangedUnknown) {
                    returns.add("Modified config: '" + key + ": " + config.get(key) + "'");
                }
            }

            configs.remove(key);
        }

        if (informMissing) {
            for (Entry<String, Object> entry : configs.entrySet()) {
                returns.add("Missing '" + entry.getKey() + ": " + entry.getValue() + "'");
            }
        }

        return returns;
    }

    static void loadCustomDisguises() {
        customDisguises.clear();

        File disguisesFile = new File(LibsDisguises.getInstance().getDataFolder(), "configs/disguises.yml");

        if (!disguisesFile.exists()) {
            return;
        }

        YamlConfiguration disguisesConfig = YamlConfiguration.loadConfiguration(disguisesFile);

        ConfigurationSection section = disguisesConfig.getConfigurationSection("Disguises");

        if (section == null) {
            return;
        }

        int failedCustomDisguises = 0;

        for (String key : section.getKeys(false)) {
            String toParse = section.getString(key);

            if (!NmsVersion.v1_13.isSupported() && key.equals("libraryaddict")) {
                toParse =
                        toParse.replace("GOLDEN_BOOTS,GOLDEN_LEGGINGS,GOLDEN_CHESTPLATE,GOLDEN_HELMET", "GOLD_BOOTS,GOLD_LEGGINGS,GOLD_CHESTPLATE,GOLD_HELMET");
            }

            try {
                addCustomDisguise(key, toParse);
            } catch (Exception e) {
                failedCustomDisguises++;

                if (e instanceof DisguiseParseException) {
                    if (e.getMessage() != null) {
                        DisguiseUtilities.getLogger().severe(e.getMessage());
                    }

                    if (e.getCause() != null) {
                        e.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        }

        if (failedCustomDisguises > 0) {
            DisguiseUtilities.getLogger().warning("Failed to load " + failedCustomDisguises + " custom disguises");
        }

        DisguiseUtilities.getLogger().info("Loaded " + customDisguises.size() + " custom disguise" + (customDisguises.size() == 1 ? "" : "s"));
    }

    public static void addCustomDisguise(String disguiseName, String toParse) throws DisguiseParseException {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("Custom Disguises should not be called async!");
        }

        if (getRawCustomDisguise(toParse) != null) {
            throw new DisguiseParseException(LibsMsg.CUSTOM_DISGUISE_NAME_CONFLICT, disguiseName);
        }

        try {
            String[] disguiseArgs = DisguiseUtilities.split(toParse);

            Disguise disguise = DisguiseParser.parseTestDisguise(Bukkit.getConsoleSender(), "disguise", disguiseArgs,
                    DisguiseParser.getPermissions(Bukkit.getConsoleSender(), "disguise"));

            DisguisePerm perm = new DisguisePerm(disguise.getType(), disguiseName);

            customDisguises.put(perm, toParse);

            DisguiseUtilities.getLogger().info("Loaded custom disguise " + disguiseName);
        } catch (DisguiseParseException e) {
            throw new DisguiseParseException(LibsMsg.ERROR_LOADING_CUSTOM_DISGUISE, disguiseName, (e.getMessage() == null ? "" : ": " + e.getMessage()));
        } catch (Throwable e) {
            e.printStackTrace();
            throw new DisguiseParseException(LibsMsg.ERROR_LOADING_CUSTOM_DISGUISE, disguiseName, "");
        }
    }

    /**
     * Is the sound packets caught and modified
     */
    public static boolean isSoundEnabled() {
        return PacketsManager.isHearDisguisesEnabled();
    }

    public static void setAnimationPacketsEnabled(boolean enabled) {
        if (enabled != isAnimationPacketsEnabled()) {
            animationPacketsEnabled = enabled;

            PacketsManager.setupMainPacketsListener();
        }
    }

    public static void setCollectPacketsEnabled(boolean enabled) {
        if (enabled != isCollectPacketsEnabled()) {
            collectPacketsEnabled = enabled;

            PacketsManager.setupMainPacketsListener();
        }
    }

    public static void setEntityStatusPacketsEnabled(boolean enabled) {
        if (enabled != isEntityStatusPacketsEnabled()) {
            entityStatusPacketsEnabled = enabled;

            PacketsManager.setupMainPacketsListener();
        }
    }

    public static void setEquipmentPacketsEnabled(boolean enabled) {
        if (enabled != isEquipmentPacketsEnabled()) {
            equipmentPacketsEnabled = enabled;

            PacketsManager.setupMainPacketsListener();
        }
    }

    /**
     * Set the plugin to hide self disguises armor from theirselves
     */
    public static void setHideArmorFromSelf(boolean hideArmor) {
        if (hidingArmorFromSelf != hideArmor) {
            hidingArmorFromSelf = hideArmor;

            PacketsManager.setInventoryListenerEnabled(isHidingHeldItemFromSelf() || isHidingArmorFromSelf());
        }
    }

    /**
     * Does the plugin appear to remove the item they are holding, to prevent a floating sword when they are viewing
     * self disguise
     */
    public static void setHideHeldItemFromSelf(boolean hideHelditem) {
        if (hidingHeldItemFromSelf != hideHelditem) {
            hidingHeldItemFromSelf = hideHelditem;

            PacketsManager.setInventoryListenerEnabled(isHidingHeldItemFromSelf() || isHidingArmorFromSelf());
        }
    }

    public static void setMiscDisguisesForLivingEnabled(boolean enabled) {
        if (enabled != isMiscDisguisesForLivingEnabled()) {
            miscDisguisesForLivingEnabled = enabled;

            PacketsManager.setupMainPacketsListener();
        }
    }

    public static void setMovementPacketsEnabled(boolean enabled) {
        if (enabled != isMovementPacketsEnabled()) {
            movementPacketsEnabled = enabled;

            PacketsManager.setupMainPacketsListener();
        }
    }

    /**
     * Set if the disguises play sounds when hurt
     */
    public static void setSoundsEnabled(boolean isSoundsEnabled) {
        PacketsManager.setHearDisguisesListener(isSoundsEnabled);
    }

    public enum DisguisePushing { // This enum has a really bad name..
        MODIFY_SCOREBOARD,
        IGNORE_SCOREBOARD,
        CREATE_SCOREBOARD
    }

    public enum PlayerNameType {
        VANILLA,
        TEAMS,
        EXTENDED,
        ARMORSTANDS;

        public boolean isTeams() {
            return this == TEAMS || this == EXTENDED;
        }
    }

    public enum UpdatesBranch {
        SAME_BUILDS,
        SNAPSHOTS,
        RELEASES
    }

    public enum NotifyBar {
        NONE,

        BOSS_BAR,

        ACTION_BAR
    }
}
