package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.records.ToggleRecord;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.toggle.Toggle;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CenturionsUtils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!(args.length > 0)) {
            player.sendMessage(CenturionsUtils.color("&cYou must provide a type."));
            return true;
        }
    
        User user = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
    
        if (CenturionsUtils.checkCmdAliases(args, 0, "list")) {
            user.sendMessage("&aList of toggles");
            for (Toggles toggle : Toggles.values()) {
                if (user.hasPermission(toggle.getRank())) {
                    user.sendMessage("&e" + toggle.name().toLowerCase());
                }
            }
            return true;
        }
        
        Toggles type = null;
        Toggle toggle;
        for (Toggles value : Toggles.values()) {
            if (value.getCmdName() != null) {
                if (value.getCmdName().equalsIgnoreCase(args[0])) {
                    type = value;
                }
            }
        }
        
        if (type == null) {
            user.sendMessage("&cCould not find a toggle with that name.");
            return true;
        }
        
        toggle = user.getToggle(type);
        if (!user.hasPermission(type.getRank())) {
            user.sendMessage(CenturionsUtils.color("&cYou do not have permission to use that toggle."));
            return true;
        }
        
        toggle.setValue((!toggle.getAsBoolean()) + "");
        CenturionsCore.getInstance().getDatabase().pushRecord(new ToggleRecord(toggle));
    
        String settingValue;
        if (toggle.getAsBoolean()) {
            settingValue = "&a&lON";
        } else {
            settingValue = "&c&lOFF";
        }
    
        player.sendMessage(CenturionsUtils.color("&6&l>> &fYou have turned " + settingValue + " &fthe toggle &e" + type.name().toLowerCase().replace("_", " ") + "&f."));
        return true;
    }
}
