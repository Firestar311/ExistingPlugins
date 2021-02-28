package me.alonedev.ironhhub.Mechanics;

import me.alonedev.ironhhub.IronHhub;
import me.alonedev.ironhhub.Utils.Util;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class Spawn implements Listener, CommandExecutor {

    int PlayersJoined = IronHhub.PlayersJoined;
    String sound = IronHhub.sound;

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

    //
    //Death
    //


    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        final Player player = e.getEntity();

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            player.spigot().respawn();
            Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
            player.teleport(spawn);
            player.playSound(player.getLocation(), sound, 10, 29);
        }, 1L);
            }

    //
    //Join
    //

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {

        Player player = (Player) e.getPlayer();

        Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
        player.teleport(spawn);

        if(!player.hasPlayedBefore()) {
            int PlayersJoined = main.getDataConfig().getInt("PlayersJoined");
            PlayersJoined++;
            e.setJoinMessage(Util.replaceColors("&e&l Welcome!  &8>>&e "+e.getPlayer().getDisplayName()+" &fjoined for the first time!"+" &f(&a#"+(PlayersJoined)+"&f)"));
            main.getDataConfig().set("PlayersJoined", PlayersJoined);
            try {
                main.getDataConfig().save(main.getDataFile());
            } catch(IOException error) {
                error.printStackTrace();
                Util.consoleMsg("Error saving data.yml, Make sure to report this to AloneMusk immediately.");
            }

        }
        else {
            e.setJoinMessage(Util.replaceColors("&e&l Hello!  &8>> &e"+e.getPlayer().getDisplayName()+" &fjoined"));
        }




    }





    }

