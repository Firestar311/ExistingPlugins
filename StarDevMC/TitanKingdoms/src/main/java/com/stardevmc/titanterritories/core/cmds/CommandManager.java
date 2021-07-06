package com.stardevmc.titanterritories.core.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.kingdom.*;
import com.stardevmc.titanterritories.core.objects.kingdom.Transaction.Type;
import com.stardevmc.titanterritories.core.objects.member.Member;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager implements CommandExecutor {
    
    @SuppressWarnings("unused")
    private Object kingdomCommand, kingdomAdminCommand, debugCommand, townCommand, colonyCommand;
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    
    public CommandManager(TitanTerritories plugin) {
        plugin.getCommand("kingdoms").setExecutor(this);
        plugin.getCommand("kingdomsadmin").setExecutor(this);
        plugin.getCommand("debug").setExecutor(this);
        plugin.getCommand("town").setExecutor(this);
        plugin.getCommand("colony").setExecutor(this);
        plugin.getCommand("createtestkingdom").setExecutor(this);
        plugin.getCommand("testchunks").setExecutor(this);
        plugin.getCommand("kingdomlock").setExecutor(this);
        this.kingdomCommand = new KingdomCommand(plugin);
        this.debugCommand = new DebugExecutor(plugin);
        this.townCommand = new TownCommand(plugin);
        this.colonyCommand = new ColonyCommand(plugin);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kingdomlock")) {
            UUID thomasRW = UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc");
            if (sender instanceof Player) {
                if (!((Player) sender).getUniqueId().equals(thomasRW)) {
                    sender.sendMessage(Utils.color("&cOnly ThomasRW can control the Kingdoms Plugin"));
                    return true;
                } else {
                    TitanTerritories.getInstance().setKingdomsEnabled(!TitanTerritories.getInstance().isKingdomsEnabled());
                    if (TitanTerritories.getInstance().isKingdomsEnabled()) {
                        Bukkit.broadcastMessage(Utils.color("&2&lKINGDOMS HAS BEEN ENABLED!"));
                    } else {
                        Bukkit.broadcastMessage(Utils.color("&4&lKINGDOMS HAS BEEN DISABLED!"));
                    }
                }
            }
            return true;
        }
        
        if (!TitanTerritories.getInstance().isKingdomsEnabled()) {
            sender.sendMessage(Utils.color("&cThe Kingdoms Plugin is currently locked."));
            return true;
        }
        
        if (cmd.getName().equalsIgnoreCase("kingdoms")) {
            if (sender instanceof Player) {
                ((KingdomCommand) kingdomCommand).executePlayer(cmd, (Player) sender, args);
            }
        } else if (cmd.getName().equalsIgnoreCase("debug")) {
            if (sender instanceof Player) {
                ((DebugExecutor) debugCommand).executePlayer((Player) sender, args);
            }
        } else if (cmd.getName().equalsIgnoreCase("town")) {
            if (sender instanceof Player) {
                ((TownCommand) townCommand).executePlayer((Player) sender, args);
            }
        } else if (cmd.getName().equalsIgnoreCase("colony")) {
            if (sender instanceof Player) {
                ((ColonyCommand) colonyCommand).executePlayer((Player) sender, args);
            }
        } else if (cmd.getName().equalsIgnoreCase("createtestkingdom")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cOnly players may do that."));
                return true;
            }
            
            Player player = (Player) sender;
            
            if (plugin.getKingdomManager().getKingdom("testKingdom") != null) {
                plugin.getKingdomManager().removeKingdom("testKingdom");
            }
            
            Member member = plugin.getMemberManager().getMember(((Player) sender).getUniqueId());
            Kingdom testKingom = plugin.getKingdomManager().createKingdom(member, "testKingdom");
            
            List<Chunk> chunks = Utils.getSurroundingChunks(player.getLocation().getChunk());
            List<Location> warpLocations = new ArrayList<>();
            for (Chunk chunk : chunks) {
                Location location = chunk.getBlock(8, 100, 8).getLocation();
                Plot plot = plugin.getPlotManager().getPlot(location);
                warpLocations.add(location);
                testKingom.getClaimController().addPlot(plot);
            }
            
            testKingom.getEconomyController().addTransaction(new Transaction(100, Type.DEPOSIT, player.getUniqueId()));
            testKingom.getEconomyController().addTransaction(new Transaction(49, Type.WITHDRAWL, player.getUniqueId()));
            testKingom.getExperienceController().addExperienceAction(new ExperienceAction(100, ExperienceAction.Type.GAIN, member));
            testKingom.getExperienceController().addExperienceAction(new ExperienceAction(49, ExperienceAction.Type.LOSS, member));
            testKingom.getAnnouncementController().addAnnouncement(new Announcement(player.getUniqueId(), 0, "Test Announcement 1"));
            testKingom.getAnnouncementController().addAnnouncement(new Announcement(player.getUniqueId(), 1, "Test Announcement 2"));
            Rank officerRank = new Rank("Officer", 1), verifiedRank = new Rank("Verified", 50);
            officerRank.setPrefix("&cOfficer ");
            verifiedRank.setPrefix("&aVerified ");
            testKingom.getRankController().addRank(officerRank);
            testKingom.getRankController().addRank(verifiedRank);
            testKingom.getRankController().getLeaderRank().setPrefix("&6King ");
            testKingom.getRankController().getDefaultRank().setPrefix("&7");
            for (int i = 0; i < warpLocations.size(); i++) {
                testKingom.getWarpController().addWarp(new Warp("plot" + i, warpLocations.get(i), player.getUniqueId()));
            }
            
            player.sendMessage(Utils.color("&aYou created the test kingdom."));
        } else if (cmd.getName().equalsIgnoreCase("testchunks")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cOnly players may do that."));
                return true;
            }
            
            sender.sendMessage(Utils.color("&aTesting chunks"));
            
            Location baseLocation = ((Player) sender).getLocation();
            List<Chunk> chunks = Utils.getSurroundingChunks(baseLocation.getChunk());
            int baseY = baseLocation.getBlockY();
            Block baseBlock = baseLocation.getChunk().getBlock(8, baseY, 8);
            baseBlock.setType(Material.RED_WOOL);
            for (Chunk chunk : chunks) {
                Block block = chunk.getBlock(8, baseY, 8);
                block.setType(Material.YELLOW_WOOL);
            }
        }
        
        return true;
    }
}