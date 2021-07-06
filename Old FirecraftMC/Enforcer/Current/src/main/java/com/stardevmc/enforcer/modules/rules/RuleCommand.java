package com.stardevmc.enforcer.modules.rules;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.rules.Rule;
import com.stardevmc.enforcer.util.Messages;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class RuleCommand implements CommandExecutor {
    
    private Enforcer plugin;
    private Map<UUID, Paginator<StringElement>> paginators = new HashMap<>();
    
    public RuleCommand(Enforcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_PLAYERS_CMD);
            return true;
        }
        
        Player player = ((Player) sender);
    
        if (args.length > 1) {
            if (Utils.checkCmdAliases(args, 0, "page", "p")) {
                if (this.paginators.containsKey(player.getUniqueId())) {
                    this.paginators.get(player.getUniqueId()).display(player, args[1]);
                } else {
                    player.sendMessage(Utils.color("&cPlease use /rules first."));
                    return true;
                }
            }
        }
        
        List<String> rules = new ArrayList<>();
        for (Rule rule : plugin.getRuleModule().getManager().getRules()) {
            if (!StringUtils.isEmpty(rule.getPlayerDescription())) {
                rules.add(" &8- &f" + rule.getPlayerDescription());
            }
        }
        
        if (rules.isEmpty()) {
            player.sendMessage(Utils.color("&cNo rules to display."));
            return true;
        }
    
        Paginator<StringElement> paginator = PaginatorFactory.generateStringPaginator(7, rules, new HashMap<>() {{
            put(DefaultVariables.COMMAND, "/rules");
            put(DefaultVariables.TYPE, "Server Rules");
        }});
    
        this.paginators.put(player.getUniqueId(), paginator);
        paginator.display(player, 1);
        return true;
    }
}