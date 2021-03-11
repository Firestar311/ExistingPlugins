package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.spigot.perks.gui.PerkMainGui;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.util.ManiaUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PerkCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ManiaUtils.color("&cOnly players may use that command."));
            return true;
        }
        net.firecraftmc.maniacore.spigot.user.SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
        new PerkMainGui(user).openGUI((Player) sender);
        return true;
    }
}
