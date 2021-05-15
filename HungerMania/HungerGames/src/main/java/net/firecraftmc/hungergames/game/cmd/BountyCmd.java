package net.firecraftmc.hungergames.game.cmd;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GamePlayer;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BountyCmd implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CenturionsUtils.color("&cOnly players may use this command."));
            return true;
        }

        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game == null) {
            sender.sendMessage(CenturionsUtils.color("&cThere is no active game."));
            return true;
        }

        if (!(args.length > 1)) {
            sender.sendMessage(CenturionsUtils.color("&cUsage: /bounty <name> <amount>"));
            return true;
        }

        User targetUser = CenturionsCore.getInstance().getUserManager().getUser(args[0]);
        if (targetUser == null) {
            sender.sendMessage(CenturionsUtils.color("&cCould not find a profile with that name."));
            return true;
        }
        
        GamePlayer targetPlayer = game.getPlayer(targetUser.getUniqueId());
        if (targetPlayer == null) {
            sender.sendMessage(CenturionsUtils.color("&cThe name you provided is not a part of the game."));
            return true;
        }
        
        int amount = Integer.parseInt(args[1]);
        
        game.addBounty(((Player) sender).getUniqueId(), targetPlayer.getUniqueId(), amount);
        sender.sendMessage("");
        sender.sendMessage(CenturionsUtils.color("&4&l>> &cPlease note, if you leave the game, your points will not be refunded!"));
        sender.sendMessage("");
        return true;
    }
}
