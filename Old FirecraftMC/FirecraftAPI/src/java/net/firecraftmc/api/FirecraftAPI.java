package net.firecraftmc.api;

import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.Database;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.plugin.*;
import net.firecraftmc.api.toggles.Toggle;
import net.firecraftmc.api.toggles.types.*;
import net.firecraftmc.api.vanish.VanishSetting;
import org.bukkit.Material;

import java.util.*;

import static net.firecraftmc.api.toggles.Toggle.*;
import static net.firecraftmc.api.vanish.VanishSetting.*;

public final class FirecraftAPI {
    
    private FirecraftAPI() {
    
    }
    
    private static final FirecraftAPI instance = new FirecraftAPI();
    
    public static final UUID firestar311 = UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc");
    
    private static IFirecraftCore firecraftCore;
    private static IFirecraftProxy firecraftProxy;
    private static IFirecraftBungee firecraftBungee;
    
    private static IFirecraftBase firecraftImplementation;
    
    public static IFirecraftCore getFirecraftCore() {
        return firecraftCore;
    }
    
    public static IFirecraftProxy getFirecraftProxy() {
        return firecraftProxy;
    }
    
    public static IFirecraftBungee getFirecraftBungee() {
        return firecraftBungee;
    }
    
    public static void setFirecraftCore(IFirecraftCore firecraftCore) {
        FirecraftAPI.firecraftCore = firecraftCore;
        loadToggles();
        loadVanishToggles();
    }
    
    public static void setFirecraftProxy(IFirecraftProxy firecraftProxy) {
        FirecraftAPI.firecraftProxy = firecraftProxy;
        loadToggles();
        loadVanishToggles();
    }
    
    public static void setFirecraftBungee(IFirecraftBungee firecraftBungee) {
        FirecraftAPI.firecraftBungee = firecraftBungee;
        loadToggles();
        loadVanishToggles();
    }
    
    public static Collection<FirecraftPlayer> getPlayers() {
        if (isCore()) {
            return firecraftCore.getPlayerManager().getPlayers();
        } else if (isProxy()) {
            return firecraftProxy.getPlayers();
        }
        return new ArrayList<>();
    }
    
    public static boolean isCore() {
        return firecraftCore != null;
    }
    
    public static boolean isProxy() {
        return firecraftProxy != null;
    }
    
    public static boolean isBungee() {
        return firecraftBungee != null;
    }
    
    public static FirecraftPlayer getPlayer(UUID uuid) {
        if (isCore()) {
            return firecraftCore.getPlayerManager().getPlayer(uuid);
        } else if (isProxy()) {
            return firecraftProxy.getPlayer(uuid);
        }
        return null;
    }
    
    public static FirecraftPlayer getPlayer(String name) {
        if (isCore()) {
            return firecraftCore.getPlayerManager().getPlayer(name);
        } else if (isProxy()) {
            return firecraftProxy.getPlayer(name);
        }
        return null;
    }
    
    public static FirecraftServer getServer() {
        if (isCore()) {
            return firecraftCore.getFCServer();
        } else if (isProxy()) {
            return firecraftProxy.getFCServer();
        }
        return null;
    }
    
    public static FirecraftServer getServer(String id) {
        if (isCore()) {
            return firecraftCore.getServerManager().getServer(id);
        } else if (isProxy()) {
            return firecraftProxy.getServer(id);
        }
        return null;
    }
    
    public static Database getDatabase() {
        if (isCore()) {
            return firecraftCore.getFCDatabase();
        } else if (isProxy()) {
            return firecraftProxy.getFCDatabase();
        }
        return null;
    }
    
    public static FirecraftAPI getInstance() {
        return instance;
    }
    
    public static IFirecraftBase getFirecraftImplementation() {
        return firecraftImplementation;
    }
    
    public static void setFirecraftImplementation(IFirecraftBase firecraftImplementation) {
        FirecraftAPI.firecraftImplementation = firecraftImplementation;
    }
    
    public static void loadToggles() {
        GOD_MODE = new GodToggle(0);
        FLIGHT = new FlightToggle(1);
        SOCIAL_SPY = new SocialSpyToggle(3);
        RECORDING = new RecordingToggle(4);
        FRIEND_REQUESTS = new FriendToggle(6);
        TELEPORT_REQUESTS = new TeleportToggle(7);
        MAIL = new MailToggle(8);
        VANISH = new VanishToggle(29);
        INCOGNITO = new IncognitoToggle(30);
        MESSAGES = new MessageToggle(32);
        AFK = new AFKToggle(33);
        
        Toggle.TOGGLES.addAll(Arrays.asList(VANISH, MAIL, TELEPORT_REQUESTS, FRIEND_REQUESTS, RECORDING, SOCIAL_SPY, FLIGHT, GOD_MODE, MESSAGES, INCOGNITO, AFK));
    }
    
    public static void loadVanishToggles() {
        BREAK = new VanishSetting("Block Break", "Toggles the ability to break blocks", Material.REDSTONE_BLOCK, 0, false, Rank.TRIAL_ADMIN);
        PLACE = new VanishSetting("Block Place", "Toggles the ability to place blocks", Material.STONE, 1, false, Rank.TRIAL_ADMIN);
        SILENT = new VanishSetting("Silent Inventories", "Toggles silent inventories", Material.CHEST, 3, false, Rank.VIP);
        DESTROY_VEHICLE = new VanishSetting("Destroy Vehicle", "Toggles the ability to destroy vehicles", Material.MINECART, 4, false, Rank.TRIAL_ADMIN);
        ENTITY_TARGET = new VanishSetting("Entity Target", "Toggles the targeting of entities", Material.TOTEM_OF_UNDYING, 6, false, Rank.VIP);
        COLLISION = new VanishSetting("Collisions", "Toggles the ability to collide with entities", Material.CACTUS, 7, false, Rank.VIP);
        DAMAGE = new VanishSetting("Damage", "Toggles the ability to take and give damage", Material.DIAMOND_SWORD, 8, false, Rank.ADMIN);
        CHAT = new VanishSetting("Chat", "Toggles the ability to chat", Material.WRITABLE_BOOK, 19, true, Rank.VIP);
        INTERACT = new VanishSetting("Interact", "Toggles the ability to interact with inventories", Material.LEVER, 21, false, Rank.VIP);
        DROP = new VanishSetting("Item Drop", "Toggles the ability to drop items", Material.DISPENSER, 23, false, Rank.VIP);
        PICKUP = new VanishSetting("Item Pickup", "Toggles the ability to pickup items", Material.HOPPER, 25, false, Rank.VIP);
        
        VanishSetting.TOGGLES.addAll(Arrays.asList(BREAK, PLACE, SILENT, DESTROY_VEHICLE, ENTITY_TARGET, COLLISION, CHAT, DAMAGE, INTERACT, DROP, PICKUP));
        
    }
}




























