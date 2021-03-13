package me.alonedev.ironhhub.Events;

import me.alonedev.ironhhub.IronHhub;
import me.alonedev.ironhhub.Utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class JoinEvent implements Listener {


    int PlayersJoined = IronHhub.PlayersJoined;
    private IronHhub main;


    public JoinEvent (IronHhub main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {

        Player player = (Player) e.getPlayer();
        String username = e.getPlayer().getDisplayName();

        Location spawn = new Location(Bukkit.getWorld(IronHhub.spawnworld), IronHhub.x, IronHhub.y, IronHhub.z, IronHhub.yaw, IronHhub.pitch);
        player.teleport(spawn);


        if(!player.hasPlayedBefore()) {
            int PlayersJoined = main.getDataConfig().getInt("PlayersJoined");
            PlayersJoined++;
            String joinPlacement = "#" + PlayersJoined;
            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', IronHhub.FirstJoinMessage)
                    .replace("%player%", username).
                            replace("%joinplacement%", joinPlacement).
                            replace("%line%", "\n"));
            main.getDataConfig().set("PlayersJoined", PlayersJoined);
            try {
                main.getDataConfig().save(main.getDataFile());
            } catch(IOException error) {
                error.printStackTrace();
                Util.consoleMsg("Error saving data.yml, Make sure to report this to AloneMusk immediately.");
            }
            Util.sendMsg(ChatColor.translateAlternateColorCodes('&', IronHhub.FirstMessageMOTD)
                    .replace("%player%", username).
                            replace("%line%", "\n"),player);

        }
        else {
            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', IronHhub.JoinMessage)
                    .replace("%player%", username).
                            replace("%line%", "\n"));;

            Util.sendMsg(ChatColor.translateAlternateColorCodes('&', IronHhub.MessageMOTD)
                    .replace("%player%", username).
                            replace("%line%", "\n"),player);
        }




    }
}
