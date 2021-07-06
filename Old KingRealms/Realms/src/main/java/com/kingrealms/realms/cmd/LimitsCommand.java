package com.kingrealms.realms.cmd;

import com.kingrealms.realms.limits.group.LimitGroup;
import com.kingrealms.realms.limits.limit.Limit;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LimitsCommand extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
        for (LimitGroup limitGroup : plugin.getLimitsManager().getLimitGroups()) {
            profile.sendMessage(Utils.blankLine("&6", 5) + "&r&e" + limitGroup.getName() + Utils.blankLine("&6", 5));
            for (Limit limit : limitGroup.getLimits()) {
                profile.sendMessage(" &8- &e" + limit.getName() + "&8: &b" + limit.getValue().toString());
            }
        }
        
        return true;
    }
}