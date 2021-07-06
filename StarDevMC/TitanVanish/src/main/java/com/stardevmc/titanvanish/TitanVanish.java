package com.stardevmc.titanvanish;

import com.firestar311.lib.player.PlayerManager;
import com.firestar311.lib.util.ReflectionUtils;
import io.netty.channel.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class TitanVanish extends JavaPlugin implements Listener {
    
    private PlayerManager playerManager;
    private VanishManager vanishManager;
    private Permission vaultPermission;
    
    public void onEnable() {
        this.saveDefaultConfig();
        
        RegisteredServiceProvider<PlayerManager> playerProvider = Bukkit.getServicesManager().getRegistration(PlayerManager.class);
        if (playerProvider != null) {
            this.playerManager = playerProvider.getProvider();
        } else {
            getLogger().severe("Could not find a player provider.");
            getServer().getPluginManager().disablePlugin(this);
        }
        
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            this.vaultPermission = permissionProvider.getProvider();
        } else {
            getLogger().severe("Could not find a Vault Permission provider");
            getServer().getPluginManager().disablePlugin(this);
        }
        
        this.vanishManager = new VanishManager(this);
        getCommand("vanish").setExecutor(new VanishCommand(this));
        
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public void onDisable() {
    
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        //injectPlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        //removePlayer(e.getPlayer());
    }
    
    private void injectPlayer(final Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }
            
            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                //Class<?> packetPlayOutChat = ReflectionUtils.getNMSClass("PacketPlayOutChat");
//                if (packetPlayOutChat.isInstance(packet)) {
////                    if (getVanishManager().isVanished(player)) {
////                        return;
////                    }
//                }
                super.write(channelHandlerContext, packet, channelPromise);
            }
        };
        
        Channel channel = getChannelFromPlayer(player);
        if (channel != null) { channel.pipeline().addBefore("packet_handler", player.getName(), channelDuplexHandler); }
    }
    
    private void removePlayer(final Player player) {
        Channel channel = getChannelFromPlayer(player);
        if (channel != null) {
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(player.getName());
                return null;
            });
        }
        
    }
    
    private Channel getChannelFromPlayer(Player player) {
        try {
            Class<?> craftPlayer = ReflectionUtils.getCraftClass("entity.CraftPlayer");
            Class<?> entityPlayer = ReflectionUtils.getNMSClass("EntityPlayer");
            Class<?> playerConnectionClass = ReflectionUtils.getNMSClass("PlayerConnection");
            Class<?> networkManagerClass = ReflectionUtils.getNMSClass("NetworkManager");
            Method getHandle = craftPlayer.getMethod("getHandle");
            Field entityPlayerConnection = entityPlayer.getField("playerConnection");
            Field connectionNetworkManager = playerConnectionClass.getField("networkManager");
            Field networkChannel = networkManagerClass.getField("channel");
            Object nmsPlayer = getHandle.invoke(craftPlayer.cast(player));
            Object playerConnection = entityPlayerConnection.get(nmsPlayer);
            Object networkManager = connectionNetworkManager.get(playerConnection);
            return (Channel) networkChannel.get(networkManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public Permission getVaultPermission() {
        return vaultPermission;
    }
    
    public VanishManager getVanishManager() {
        return vanishManager;
    }
}
