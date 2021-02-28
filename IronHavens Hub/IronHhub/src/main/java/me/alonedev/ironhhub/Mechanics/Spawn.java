package me.alonedev.ironhhub.Mechanics;

import jdk.javadoc.internal.tool.Main;
import me.alonedev.ironhhub.IronHhub;
import me.alonedev.ironhhub.Utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Spawn implements Listener, CommandExecutor {

    private IronHhub main;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
            player.teleport(spawn);



        }
        return true;
    }

    public Spawn (IronHhub main) {
        this.main = main;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        final Player player = e.getEntity();

        Bukkit.getScheduler().scheduleSyncDelayedTask(IronHhub.plugin, () -> {
            player.spigot().respawn();
            Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
            player.teleport(spawn);
            Util.sendMsg("You Died", player);
        }, 1L);
            }






    }

