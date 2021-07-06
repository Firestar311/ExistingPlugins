package com.stardevmc.enforcer.modules.rules;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.RuleManager;
import com.stardevmc.enforcer.objects.Variables;
import com.stardevmc.enforcer.objects.punishment.Punishment.Type;
import com.stardevmc.enforcer.objects.rules.*;
import com.stardevmc.enforcer.util.*;
import com.starmediadev.lib.pagination.Paginator;
import com.starmediadev.lib.pagination.PaginatorFactory;
import com.starmediadev.lib.util.Unit;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class MrulesCommand implements CommandExecutor {
    
    private Enforcer plugin;
    private Map<UUID, Paginator<?>> paginators = new HashMap<>();
    
    public MrulesCommand(Enforcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!player.hasPermission(Perms.MRULES_MAIN)) {
            player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_MAIN));
            return true;
        }
        
        RuleManager ruleManager = plugin.getRuleModule().getManager();
        if (args.length == 0) {
            PaginatorFactory<Rule> factory = new PaginatorFactory<>();
            factory.setMaxElements(7).setHeader("&7-=Moderator Rules=- &e({pagenumber}/{totalpages})").setFooter("&7Type /mrules page {nextpage} for more");
            ruleManager.getRules().forEach(factory::addElement);
            Paginator<Rule> paginator = factory.build();
            paginator.display(player, 1);
            this.paginators.put(player.getUniqueId(), paginator);
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "page", "p")) {
            if (!this.paginators.containsKey(player.getUniqueId())) {
                player.sendMessage(Utils.color("&cYou do not have any results"));
                return true;
            }
            
            int page;
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color("&cThe value for the page number is not a valid number."));
                return true;
            }
            
            Paginator<?> paginator = this.paginators.get(player.getUniqueId());
            paginator.display(player, page);
            return true;
        } else if (Utils.checkCmdAliases(args, 0, "create", "c")) {
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cUsage: /mrules create|c <name>"));
                return true;
            }
            
            if (!player.hasPermission(Perms.MRULES_CREATE)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_CREATE));
                return true;
            }
            
            String name = StringUtils.join(args, ' ', 1, args.length);
            String internalId = name.toLowerCase().replace(" ", "_");
            
            Rule existing = ruleManager.getRule(internalId);
            if (existing != null) {
                player.sendMessage(Utils.color("&cA rule with that name already exists."));
                return true;
            }
            
            Rule rule = new Rule(internalId, name);
            ruleManager.addRule(rule);
            String message = Messages.RULE_CREATE;
            message = message.replace(Variables.RULE_NAME, rule.getName());
            message = message.replace(Variables.RULE_ID, rule.getId() + "");
            message = message.replace(Variables.RULE_INTERNALID, rule.getInternalId());
            Messages.sendOutputMessage(player, message, plugin);
            return true;
        }
        
        Rule rule = ruleManager.getRule(args[0]);
        if (rule == null) {
            player.sendMessage(Utils.color("&cCould not find a rule with that identifier."));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 1, "setdescription", "sd")) {
            if (!(args.length > 2)) {
                player.sendMessage(Utils.color("&cUsage: /mrules <rule> setdescription|sd <description>"));
                return true;
            }
            
            if (!player.hasPermission(Perms.MRULES_SET_DESCRIPTION)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_SET_DESCRIPTION));
                return true;
            }
            
            String description = StringUtils.join(args, " ", 2, args.length);
            rule.setDescription(description);
            
            String message = Messages.RULE_SET_DESCRIPTION;
            message = message.replace(Variables.RULE_NAME, rule.getName());
            message = message.replace(Variables.RULE_DESCRIPTION, rule.getDescription());
            Messages.sendOutputMessage(player, message, plugin);
        } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cUsage: /mrules <id> remove|r"));
                return true;
            }
            
            if (!player.hasPermission(Perms.MRULES_REMOVE)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_REMOVE));
                return true;
            }
    
            ruleManager.removeRule(rule.getId());
            player.sendMessage(Utils.color("&aRemoved the rule &b" + rule.getInternalId()));
        } else if (Utils.checkCmdAliases(args, 1, "setmaterial", "sm")) {
            if (!(args.length > 1)) {
                player.sendMessage(Utils.color("&cUsage: /mrules <id> setmaterial|sr <material>"));
                return true;
            }
            
            if (!player.hasPermission(Perms.MRULES_SET_MATERIAL)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_SET_MATERIAL));
                return true;
            }
            
            Material material;
            try {
                material = Material.valueOf(args[2].toUpperCase());
            } catch (Exception e) {
                player.sendMessage(Utils.color("&cThe value that you provided is not a valid material."));
                return true;
            }
            
            rule.setMaterial(material);
            player.sendMessage(Utils.color("&aSet the material of the rule &b" + rule.getInternalId() + " &ato &b" + material.name()));
        } else if (Utils.checkCmdAliases(args, 1, "setplayerdescription", "spd")) {
            if (!(args.length > 2)) {
                player.sendMessage(Utils.color("&cUsage: /mrules <rule> setplayerdescription|spd <description>"));
                return true;
            }
    
            if (!player.hasPermission(Perms.MRULES_SET_PLAYER_DESCRIPTION)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_SET_PLAYER_DESCRIPTION));
                return true;
            }
    
            String description = StringUtils.join(args, " ", 2, args.length);
            rule.setPlayerDescription(description);
    
            String message = Messages.RULE_SET_PLAYER_DESCRIPTION;
            message = message.replace(Variables.RULE_NAME, rule.getName());
            message = message.replace(Variables.RULE_DESCRIPTION, rule.getPlayerDescription());
            Messages.sendOutputMessage(player, message, plugin);
        } else if (Utils.checkCmdAliases(args, 1, "view", "v")) {
            if (!player.hasPermission(Perms.MRULES_VIEW)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIEW));
                return true;
            }
            
            player.sendMessage(Utils.color("&aViewing information for rule " + rule.getName() + "\n" + " &8- &7Rule ID: &e" + rule.getId() + "\n" + " &8- &7Rule Internal ID: &e" + rule.getInternalId() + "\n" + " &8- &7Rule Name: &e" + rule.getName() + "\n" + " &8- &7Rule Description: &e" + rule.getDescription() + "\n" + " &8- &7Rule Player Description: &e" + rule.getPlayerDescription() + "\n" + " &8- &7Rule Material: &e" + rule.getMaterial() + "\n" + " &8- &7Offense Count: &e" + rule.getViolations().size() + "\n" + " &8- &7Rule Permission: &e" + rule.getPermission()));
        } else if (Utils.checkCmdAliases(args, 1, "setname", "sn")) {
            if (!player.hasPermission(Perms.MRULES_SET_NAME)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_SET_NAME));
                return true;
            }
            
            String name = StringUtils.join(args, ' ', 2, args.length);
            
            Rule existing = ruleManager.getRule(name);
            if (existing != null) {
                player.sendMessage(Utils.color("&cA rule with that name already exists."));
                return true;
            }
            
            rule.setName(name);
            rule.setInternalId(name);
            player.sendMessage(Utils.color("&aSet the name of rule &b" + rule.getId() + " &ato &b" + rule.getName() + " &aand the internal id to &b" + rule.getInternalId()));
        } else if (Utils.checkCmdAliases(args, 1, "violations", "vio")) {
            if (!(args.length > 2)) {
                player.sendMessage(Utils.color("&cUsage: /mrules <rule> violations|vio <list|punishments> <options...>"));
                return true;
            }
            
            if (!player.hasPermission(Perms.MRULES_VIOLATIONS_MAIN)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_MAIN));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 2, "list", "l")) {
                if (!player.hasPermission(Perms.MRULES_VIOLATIONS_LIST)) {
                    player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_LIST));
                    return true;
                }
                PaginatorFactory<RuleViolation> factory = new PaginatorFactory<>();
                factory.setMaxElements(7).setHeader("&7-=Violations for " + rule.getName() + "=- &e({pagenumber}/{totalpages})").setFooter("&7Type /mrules page {nextpage} for more");
                rule.getViolations().forEach((id, offense) -> factory.addElement(offense));
                Paginator<RuleViolation> paginator = factory.build();
                paginator.display(player, 1);
                this.paginators.put(player.getUniqueId(), paginator);
                return true;
            } else if (Utils.checkCmdAliases(args, 2, "create", "c")) {
                if (!player.hasPermission(Perms.MRULES_VIOLATIONS_CREATE)) {
                    player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_CREATE));
                    return true;
                }
                
                RuleViolation ruleViolation = new RuleViolation();
                rule.addViolation(ruleViolation);
                player.sendMessage(Utils.color("&aAdded a a new violation with the number &b" + ruleViolation.getViolationNumber() + " &ato the rule &b" + rule.getName()));
                return true;
            }
            
            int violationNumber;
            try {
                violationNumber = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(Utils.color("&cThe value you provided for the violation was not a valid number."));
                return true;
            }
            
            RuleViolation violation = rule.getViolation(violationNumber);
            if (violation == null) {
                player.sendMessage(Utils.color("&cThe value you provided did not match a valid violation within that rule"));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 3, "setlength", "sl")) {
                if (!player.hasPermission(Perms.MRULES_VIOLATIONS_LENGTH)) {
                    player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_LENGTH));
                    return true;
                }
                
                int rawLength;
                try {
                    rawLength = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Utils.color("&cThe value you provided for the length is not a valid number"));
                    return true;
                }
                
                Unit unit;
                try {
                    unit = Unit.matchUnit(args[5]);
                } catch (Exception e) {
                    player.sendMessage(Utils.color("&cYou provided an invalid unit type"));
                    return true;
                }
                
                long length = unit.convertTime(rawLength);
                
                violation.setLength(length);
                player.sendMessage(Utils.color("&aYou set the length to &b" + rawLength + unit.name().toLowerCase() + " &afor violation &b" + violation.getViolationNumber() + " &aof the rule &b" + rule.getName()));
            } else if (Utils.checkCmdAliases(args, 3, "punishments", "pu")) {
                if (!player.hasPermission(Perms.MRULES_VIOLATIONS_PUNISHMENTS)) {
                    player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_PUNISHMENTS));
                    return true;
                }
                
                //moderatorrules <rule> offenses|of <offense> punishments|p <add|a|remove|r|clear|c> [options…]
                
                if (Utils.checkCmdAliases(args, 4, "add", "a")) {
                    if (!player.hasPermission(Perms.MRULES_VIOLATIONS_PUNISHMENTS_ADD)) {
                        player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_PUNISHMENTS_ADD));
                        return true;
                    }
                    
                    // /mrules <rule> offenses <offense> punishments <add> <type> <number> <units>
                    if (!(args.length > 7)) {
                        player.sendMessage(Utils.color("&cYou must provide a type, number and unit"));
                        return true;
                    }
                    
                    Type type;
                    try {
                        type = Type.getType(args[5].toUpperCase());
                    } catch (Exception e) {
                        player.sendMessage(Utils.color("Invalid punishment type."));
                        return true;
                    }
                    
                    int length;
                    try {
                        length = Integer.parseInt(args[6]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Utils.color("&cThe value for the length was not a valid number."));
                        return true;
                    }
                    
                    Unit unit;
                    try {
                        unit = Unit.matchUnit(args[7]);
                    } catch (Exception e) {
                        player.sendMessage(Utils.color("&cThe value you provided for the unit was invalid."));
                        return true;
                    }
                    
                    RulePunishment rulePunishment = new RulePunishment(type, length, unit);
                    violation.addPunishment(rulePunishment);
                    player.sendMessage(Utils.color("&e[" + rulePunishment.getId() + "] &aAdded a punishment with the type " + type.getDisplayName() + " &aand the length &b" + length + " " + unit.getName().toLowerCase() + " &ato violation &b" + violation.getViolationNumber() + " &aof the rule &b" + rule.getInternalId()));
                } else if (Utils.checkCmdAliases(args, 4, "remove", "r")) {
                    if (!player.hasPermission(Perms.MRULES_VIOLATIONS_PUNISHMENTS_REMOVE)) {
                        player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_PUNISHMENTS_REMOVE));
                        return true;
                    }
                    
                    int id;
                    try {
                        id = Integer.parseInt(args[5]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Utils.color("&cThe value for the punishment id was not a valid number"));
                        return true;
                    }
                    
                    if (!violation.hasPunishment(id)) {
                        player.sendMessage(Utils.color("&cThat offense does not have a punishment with that id."));
                        return true;
                    }
                    
                    violation.removePunishment(id);
                    player.sendMessage(Utils.color("&aRemoved the punishment with the id &b" + id + " &afrom the violation &b" + violation.getViolationNumber() + " &aof the rule &b" + rule.getName()));
                } else if (Utils.checkCmdAliases(args, 4, "clear", "c")) {
                    if (!player.hasPermission(Perms.MRULES_VIOLATIONS_PUNISHMENTS_CLEAR)) {
                        player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_PUNISHMENTS_CLEAR));
                        return true;
                    }
                    
                    violation.clearPunishments();
                    player.sendMessage(Utils.color("&aYou cleared all punishments from violation &b" + violation.getViolationNumber() + " &aof the rule &b" + rule.getName()));
                } else if (Utils.checkCmdAliases(args, 4, "list", "l")) {
                    if (!player.hasPermission(Perms.MRULES_VIOLATIONS_PUNISHMENTS_LIST)) {
                        player.sendMessage(Messages.noPermissionCommand(Perms.MRULES_VIOLATIONS_PUNISHMENTS_LIST));
                        return true;
                    }
                    
                    PaginatorFactory<RulePunishment> factory = new PaginatorFactory<>();
                    factory.setHeader("&7List of Violation Punishments &e({pagenumber}/{totalpages})").setFooter("&6Type /mrules page {nexpage} for more.").setMaxElements(7);
                    factory.addElements(violation.getPunishments().values().toArray(new RulePunishment[0]));
                    player.sendMessage(Utils.color("&7Permission for offense: &e" + violation.getPermission()));
                    Paginator<RulePunishment> paginator = factory.build();
                    paginator.display(player, 1);
                    this.paginators.put(player.getUniqueId(), paginator);
                }
            }
        }
        
        return true;
    }
}
