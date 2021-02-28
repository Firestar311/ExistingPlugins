package me.alonedev.ironhhub.Mechanics;

import jdk.javadoc.internal.tool.Main;
import me.alonedev.ironhhub.IronHhub;
import me.alonedev.ironhhub.Utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;


public class PlayerJoin implements Listener {

    int PlayersJoined = IronHhub.PlayersJoined;
    private IronHhub main;

    public PlayerJoin (IronHhub main) {
        this.main = main;
    }

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


