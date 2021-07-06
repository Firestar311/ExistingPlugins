package com.stardevmc.enforcer.modules.wave;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.WaveManager;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.wave.*;
import com.stardevmc.enforcer.objects.wave.Wave.State;
import com.stardevmc.enforcer.util.Messages;
import com.stardevmc.enforcer.util.Perms;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class WaveCommand implements CommandExecutor {
    
    private Enforcer plugin;
    
    public WaveCommand(Enforcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Actor actor = plugin.getActorModule().getManager().getActor(sender);
        if (actor == null) {
            sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_AND_CONSOLE_CMD));
            return true;
        }
        
        WaveManager waveManager = plugin.getWaveModule().getManager();
        if (args.length == 0) {
            sender.sendMessage(Utils.color("&aThere are a total of " + waveManager.getWaves().size() + " waves"));
            //TODO Separate messages for the different wave states
        } else {
            if (Utils.checkCmdAliases(args, 0, "create", "c")) {
                Wave wave = waveManager.createWave(actor);
                sender.sendMessage(Utils.color("&aYou created a wave with the id " + wave.getId()));
            } else if (Utils.checkCmdAliases(args, 0, "list", "l")) {
                Paginator<Wave> paginator = PaginatorFactory.generatePaginator(7, waveManager.getWaves().values(), new HashMap<>() {{
                    put(DefaultVariables.TYPE, "Waves");
                    put(DefaultVariables.COMMAND, "wave list");
                }});
                
                if (args.length == 1) {
                    paginator.display(sender, 1);
                } else {
                    paginator.display(sender, args[1]);
                }
            } else {
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must supply a subcommand."));
                    return true;
                }
    
                Wave wave = waveManager.getWave(args[0]);
                //TODO Proper message
                if (Utils.checkCmdAliases(args, 1, "assign", "ass")) {
                    if (!sender.hasPermission(Perms.WAVE_ASSIGN)) {
                        sender.sendMessage(Utils.color(Messages.noPermissionCommand(Perms.WAVE_ASSIGN)));
                        return true;
                    }
                    Actor assignee;
                    if (args.length == 2) {
                        assignee = actor;
                    } else if (args[2].equalsIgnoreCase("self")) {
                        assignee = actor;
                    } else {
                        assignee = plugin.getActorModule().getManager().getActor(args[2]);
                    }
                    
                    wave.setAssignee(assignee);
                    sender.sendMessage(Utils.color("&aSuccessfully assigned the wave " + wave.getId() + " to " + wave.getAssignee().getName()));
                    return true;
                } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                    if (!sender.hasPermission(Perms.WAVE_LIST)) {
                        sender.sendMessage(Utils.color(Messages.noPermissionCommand(Perms.WAVE_LIST)));
                        return true;
                    }
                    
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_CMD));
                        return true;
                    }
                    
                    Player player = (Player) sender;
                    
                    if (args.length == 2) {
                        wave.getPaginator().display(player, 1);
                    } else {
                        wave.getPaginator().display(player, args[2]);
                    }
                }
                
                if (wave.getAssignee() == null) {
                    sender.sendMessage(Utils.color("&cThere is no one assigned to that wave."));
                    return true;
                } else {
                    if (!wave.getAssignee().equals(actor)) {
                        sender.sendMessage(Utils.color("&cOnly the one assigned can do that."));
                        return true;
                    }
                }
                
                if (Utils.checkCmdAliases(args, 1, "approve", "app")) {
                    if (!sender.hasPermission(Perms.WAVE_APPROVE)) {
                        sender.sendMessage(Utils.color(Messages.noPermissionCommand(Perms.WAVE_APPROVE)));
                        return true;
                    }
                    
                    if (!(args.length > 2)) {
                        sender.sendMessage(Utils.color("&cYou must provide an index."));
                        return true;
                    }
                    
                    int index;
                    try {
                        index = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Utils.color("&cThe value for the index must be a number."));
                        return true;
                    }
                    
                    WaveEntry waveEntry = wave.getWaveEntry(index);
                    if (waveEntry == null) {
                        sender.sendMessage(Utils.color("&cThe index does not match a valid entry."));
                        return true;
                    }
                    
                    waveEntry.setApproved(actor);
                    sender.sendMessage(Utils.color("&aYou have approved the punishment " + waveEntry.getIndex() + " in the wave " + wave.getId()));
                } else if (Utils.checkCmdAliases(args, 1, "reject", "r")) {
                    if (!sender.hasPermission(Perms.WAVE_REJECT)) {
                        sender.sendMessage(Utils.color(Messages.noPermissionCommand(Perms.WAVE_REJECT)));
                        return true;
                    }
                    
                    if (!(args.length > 2)) {
                        sender.sendMessage(Utils.color("&cYou must provide an index."));
                        return true;
                    }
                    
                    int index;
                    try {
                        index = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Utils.color("&cThe value for the index must be a number."));
                        return true;
                    }
                    
                    WaveEntry waveEntry = wave.getWaveEntry(index);
                    if (waveEntry == null) {
                        sender.sendMessage(Utils.color("&cThe index does not match a valid entry."));
                        return true;
                    }
                    
                    waveEntry.setRejected(actor);
                    sender.sendMessage(Utils.color("&aYou have rejected the punishment " + waveEntry.getIndex() + " in the wave " + wave.getId()));
                } else if (Utils.checkCmdAliases(args, 1, "activate", "act")) {
                    if (!sender.hasPermission(Perms.WAVE_ACTIVATE)) {
                        sender.sendMessage(Utils.color(Messages.noPermissionCommand(Perms.WAVE_ACTIVATE)));
                        return true;
                    }
                    
                    if (wave.getState().equals(State.ACTIVATED)) {
                        sender.sendMessage(Utils.color("&cYou cannot activate an already activated wave."));
                        return true;
                    }
                    
                    ActivationStats stats = wave.activate(actor);
                    sender.sendMessage(Utils.color("&aYou activated the wave " + wave.getId() + ", below are the statistics of activation."));
                    sender.sendMessage(Utils.color("    &8- &aApproved: " + stats.getApproved()));
                    sender.sendMessage(Utils.color("    &8- &cRejected: " + stats.getRejected()));
                    sender.sendMessage(Utils.color("    &8- &7Undecided: " + stats.getUndecided()));
                    if (waveManager.getCurrentWave().getId().equals(wave.getId())) {
                        waveManager.setCurrentWave(waveManager.createWave(plugin.getActorModule().getManager().getActor("Console")));
                    }
                } else if (Utils.checkCmdAliases(args, 1, "review", "r")) {
                    if (!sender.hasPermission(Perms.WAVE_REVIEW)) {
                        sender.sendMessage(Utils.color(Messages.noPermissionCommand(Perms.WAVE_REVIEW)));
                        return true;
                    }
                    wave.setState(State.UNDER_REVIEW);
                    sender.sendMessage(Utils.color("&aYou changed the state of the wave " + wave.getId() + " to UNDER REVIEW."));
                    sender.sendMessage(Utils.color("&aThis means that punishments in the wave can be approved/rejected and no more can be added."));
                }
            }
        }
        
        return true;
    }
}