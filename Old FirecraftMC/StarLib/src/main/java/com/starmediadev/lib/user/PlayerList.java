package com.starmediadev.lib.user;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R1.*;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("unused")
public class PlayerList {
    protected String header = "", footer = "";
    protected Map<Integer, String> slots = new HashMap<>();
    //todo Get an account with an all black skin
    
    
    
    /**
     * This is just the code that I used to test to see how to add a line to the tablist.
     * @param player The player
     */
    private void addLine(Player player) {
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "Test");
            MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer worldServer = ((CraftWorld) player.getLocation().getWorld()).getHandle();
            EntityPlayer fakePlayer = new EntityPlayer(server, worldServer, profile, new PlayerInteractManager(worldServer));
            PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, fakePlayer);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playerInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}