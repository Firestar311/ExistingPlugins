package net.firecraftmc.api.wrapper;

import com.mojang.authlib.properties.Property;
import net.firecraftmc.api.interfaces.NickWrapper;
import net.firecraftmc.api.model.player.*;
import net.firecraftmc.api.plugin.IFirecraftCore;
import net.minecraft.server.v1_13_R1.*;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class NickWrapper1_13_R1 implements NickWrapper {
    private final MinecraftServer minecraftServer;

    private final EnumPlayerInfoAction action_remove = EnumPlayerInfoAction.REMOVE_PLAYER;
    private final EnumPlayerInfoAction action_add = EnumPlayerInfoAction.ADD_PLAYER;

    public NickWrapper1_13_R1() {
        this.minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
    }

    public void refreshOthers(IFirecraftCore plugin, Player player) {
        List<Player> canSee = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.canSee(player)) {
                canSee.add(p);
                p.hidePlayer(player);
            }
        }

        for (Player p : canSee) {
            p.showPlayer(player);
        }
    }

    public void setProfileName(Player player, String name) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        try {
            Field nameField = craftPlayer.getProfile().getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(craftPlayer.getProfile(), name);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setSkinProperties(Player player, Skin skin) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getProfile().getProperties().clear();
        craftPlayer.getProfile().getProperties().put(skin.getName(), new Property(skin.getName(), skin.getValue(), skin.getSignature()));
    }

    public void refreshSelf(IFirecraftCore plugin, Player nicked, String name) {
        CraftPlayer craftPlayer = (CraftPlayer) nicked;
        PacketPlayOutPlayerInfo removePlayer = new PacketPlayOutPlayerInfo(action_remove, craftPlayer.getHandle());
        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(action_add, craftPlayer.getHandle());
        PacketPlayOutRespawn respawnPlayer = new PacketPlayOutRespawn(0, minecraftServer.getDifficulty(), WorldType.types[0], craftPlayer.getHandle().playerInteractManager.getGameMode());

        PlayerConnection connection = craftPlayer.getHandle().playerConnection;
        connection.sendPacket(removePlayer);

        new BukkitRunnable() {
            public void run() {
                boolean flying = nicked.isFlying();
                Location location = nicked.getLocation();
                int level = nicked.getLevel();
                float xp = nicked.getExp();
                double health = nicked.getHealth();

                connection.sendPacket(respawnPlayer);

                nicked.setFlying(flying);
                nicked.teleport(location);
                nicked.updateInventory();
                nicked.setLevel(level);
                nicked.setExp(xp);
                nicked.setHealth(health);

                connection.sendPacket(addPlayer);
            }
        }.runTaskLater(plugin, 10L);
    }

    public NickInfo setNick(IFirecraftCore plugin, FirecraftPlayer player, FirecraftPlayer nickProfile) {
        setSkinProperties(player.getPlayer(), nickProfile.getSkin());
        setProfileName(player.getPlayer(), nickProfile.getName());
        refreshOthers(plugin, player.getPlayer());
        refreshSelf(plugin, player.getPlayer(), nickProfile.getName());

        return new NickInfo(nickProfile);
    }
}
