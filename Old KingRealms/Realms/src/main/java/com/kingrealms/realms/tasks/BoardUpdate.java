package com.kingrealms.realms.tasks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.profile.board.PrimaryBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BoardUpdate extends BukkitRunnable {
    
    private final Realms plugin;
    
    public BoardUpdate(Realms plugin) {
        this.plugin = plugin;
    }
    
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            RealmProfile profile = plugin.getProfileManager().getProfile(player);
            if (profile.getDisplayBoard() != null) {
                profile.getDisplayBoard().updateLines();
            } else {
                profile.setDisplayBoard(new PrimaryBoard(player));
            }
        }
    }
}
