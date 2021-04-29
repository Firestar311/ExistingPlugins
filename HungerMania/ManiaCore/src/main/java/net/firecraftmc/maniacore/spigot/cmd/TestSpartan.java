package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class TestSpartan implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            String server = CenturionsCore.getInstance().getServerManager().getCurrentServer().getName();
            int ping = ((CraftPlayer) sender).getHandle().ping;
            double tps = ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
            CenturionsCore.getInstance().getMessageHandler().sendSpartanMessage(server, sender.getName(), args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), tps, ping);
            //SpartanUtils.sendSpartanMessage(server, sender.getName(), args[0], Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), tps, ping);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(CenturionsUtils.color("&cError handling that command."));
        }
        
        return true;
    }
}
