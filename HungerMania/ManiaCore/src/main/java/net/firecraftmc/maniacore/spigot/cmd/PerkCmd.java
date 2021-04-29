package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.spigot.perks.gui.PerkMainGui;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PerkCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CenturionsUtils.color("&cOnly players may use that command."));
            return true;
        }
        net.firecraftmc.maniacore.spigot.user.SpigotUser user = (SpigotUser) CenturionsCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
        new PerkMainGui(user).openGUI((Player) sender);
        return true;
    }
}
