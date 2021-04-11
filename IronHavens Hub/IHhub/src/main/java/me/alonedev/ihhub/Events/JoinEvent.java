package me.alonedev.ihhub.Events;

import me.alonedev.ihhub.IHhub;
import me.alonedev.ihhub.Utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class JoinEvent implements Listener {

    private static IHhub main;
    public JoinEvent(IHhub main) {
        this.main = main;
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String username = player.getDisplayName();

        //First Join Messages
        if (!player.hasPlayedBefore()) {
            String joinPlacement =  calculateFirstJoinPlacement();
            event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Messages.Join.FirstJoinMessage")).
                    replace("%player%", username).
                    replace("%joinplace%",  joinPlacement));
            List<String> firstJoinMOTD = main.getConfig().getStringList("Messages.Join.FirstJoinMOTD");
            for (String message : firstJoinMOTD) {
                Util.sendMsg(ChatColor.translateAlternateColorCodes('&', message).
                        replace("%joinplace%", joinPlacement).
                        replace("%player%", username), player);
            }
            return;
        }

        //Join Messages
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Messages.Join.JoinMessage")).
                replace("%player%", username));
        List<String> joinMOTD = main.getConfig().getStringList("Messages.Join.JoinMOTD");
        for (String message : joinMOTD) {
            Util.sendMsg(ChatColor.translateAlternateColorCodes('&', message).
                    replace("%player%", username), player);
        }

    }









    public String calculateFirstJoinPlacement() {
        int placement = main.getDataConfig().getInt("Data.PlayersJoined");
        placement++;
        main.getDataConfig().set("Data.PlayersJoined", placement);
        Util.saveDataYml(main.getDataConfig(), main.getDataFile());

        String joinPlacement = "" + placement;
        return joinPlacement;
    }
}