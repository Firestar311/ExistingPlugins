package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.spigot.reset.ResetAction;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ResetCmd implements CommandExecutor {
    
    private Map<String, ResetAction> resetConfirmation = new HashMap<>();
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank;
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            User user = CenturionsCore.getInstance().getUserManager().getUser(player.getUniqueId());
            senderRank = user.getRank();
        } else {
            senderRank = Rank.DEFAULT;
        }
    
        // Stats, Toggles, Perks, Mutations, User (Owner), Friends, Games (Owner)
        // All (no args)
    
        if (cmd.getName().equals("reset")) {
            if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
                sender.sendMessage(CenturionsUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
        
        
        } else if (cmd.getName().equals("resetall")) {
            if (senderRank.ordinal() > Rank.OWNER.ordinal()) {
                sender.sendMessage(CenturionsUtils.color("&cYou do not have permission to use that command."));
                return true;
            }
        } else if (cmd.getName().equals("resetconfirm")) {
            
        } else if (cmd.getName().equals("resetallconfirm")) {
            
        }
        
        
        return true;
    }
}
