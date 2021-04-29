package net.firecraftmc.maniacore.spigot.anticheat;

import me.vagdedes.spartan.api.PlayerViolationEvent;
import net.firecraftmc.maniacore.api.CenturionsCore;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpartanManager implements Listener {
    
    @EventHandler
    public void onSpartanViolation(PlayerViolationEvent e) {
        String server = CenturionsCore.getInstance().getServerManager().getCurrentServer().getName();
        int ping = ((CraftPlayer) e.getPlayer()).getHandle().ping;
        double tps = ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
        if (e.getViolation() >= 5) {
            CenturionsCore.getInstance().getMessageHandler().sendSpartanMessage(server, e.getPlayer().getName(), e.getHackType().name(), e.getViolation(), e.isFalsePositive(), tps, ping);
        }
    }
}
