package com.kingrealms.realms.cmd;

import com.kingrealms.realms.entities.type.CustomWitherSkeleton;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.gui.QuestGui;
import com.kingrealms.realms.questing.gui.QuestLinesGui;
import com.kingrealms.realms.questing.lines.QuestLine;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.region.Selection;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.entity.*;

import java.util.UUID;

public class QuestCommands extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        RealmProfile profile = plugin.getProfileManager().getProfile(sender);
    
        if (cmd.getName().equalsIgnoreCase("quest")) {
            if (!checkSeason(sender)) return true;
            ID guiDefault = profile.getQuestGuiDefault();
            if (guiDefault != null) {
                QuestLine line = plugin.getQuestManager().getQuestLine(guiDefault);
                new QuestGui(line, profile).openGUI(profile.getBukkitPlayer());
            } else {
                new QuestLinesGui(profile).openGUI(profile.getBukkitPlayer());
            }
        } else if (cmd.getName().equalsIgnoreCase("questadmin")) {
            if (!profile.hasPermission("realms.questadmin")) {
                profile.sendMessage("&cYou do not have permission to use that command.");
                return true;
            }
            
            if (!(args.length > 0)) {
                profile.sendMessage("&cYou must provide a sub command.");
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "createportalkeeper", "cpk")) {
                if (!profile.hasPermission("realms.questadmin.portalkeeper")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                UUID existing = plugin.getSettingsManager().getNetherPortalKeeper();
                if (existing != null) {
                    Entity entity = Bukkit.getEntity(existing);
                    try {
                        entity.remove();
                    } catch (Exception e) {}
                }
                
                Entity entity = profile.getWorld().spawnEntity(profile.getLocation(), EntityType.WITHER_SKELETON);
                CustomWitherSkeleton nmsEntity = (CustomWitherSkeleton) ((CraftEntity) entity).getHandle();
                nmsEntity.setPortalKeeper(true);
                nmsEntity.setCustom(true);

                plugin.getSettingsManager().setNetherPortalKeeper(entity.getUniqueId());
            } else if (Utils.checkCmdAliases(args, 0, "netherstart", "ns")) {
                if (!profile.hasPermission("realms.questadmin.netherstart")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide more arguments."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "pos1", "pos2")) {
                    Location location = profile.getLocation();
                    Selection selection = plugin.getSelectionManager().getSelection(profile.getUniqueId());
                    if (Utils.checkCmdAliases(args, 1, "pos1")) {
                        selection.setPointA(location);
                        profile.sendMessage("&iSet pos 1 to your current location");
                    } else {
                        selection.setPointB(location);
                        profile.sendMessage("&iSet pos 2 to your current location");
                    }
                } else if (Utils.checkCmdAliases(args, 1, "set")) {
                    Selection selection = plugin.getSelectionManager().getSelection(profile.getUniqueId());
                    if (!selection.hasMaximum() || !selection.hasMinimum()) {
                        profile.sendMessage("&cYou do not have a valid selection.");
                        return true;
                    }
    
                    Cuboid cuboid = new Cuboid(selection.getPointA(), selection.getPointB());
                    plugin.getSettingsManager().setNetherStartCuboid(cuboid);
                    profile.sendMessage("&iSet the Nether Start Region to the current selection.");
                }
            }
        }
        return true;
    }
}