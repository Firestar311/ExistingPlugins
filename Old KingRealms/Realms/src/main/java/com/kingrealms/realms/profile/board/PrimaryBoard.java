package com.kingrealms.realms.profile.board;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.enums.Privacy;
import com.starmediadev.lib.util.Constants;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrimaryBoard extends RealmsBoard {
    
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");
    private static final String SPAWN = "&dSpawn", WARZONE = "&4Warzone", WILDERNESS = "&2Wilderness";
    private final Realms plugin = Realms.getInstance();
    
    private final int claimLine, /*hamletLine,*/ nameLine, xpLine, dateLine;
    private RealmProfile player;
    
    public PrimaryBoard(Player player) {
        super("main", "&6&lKingRealms &7- &b&lMedieval");
        this.player = plugin.getProfileManager().getProfile(player);
        this.dateLine = addLine("&7" + DATE_FORMAT.format(new Date(System.currentTimeMillis())));
        addLine("");
        addLine("&d&lYou");
        this.nameLine = addLine("&b" + this.player.getName());
        //this.hamletLine = addLine("");
        addLine("");
        addLine("&c&lLocation");
        this.claimLine = addLine(WILDERNESS);
        addLine("");
        addLine("&a&lXP");
        this.xpLine = addLine("&d" + Constants.NUMBER_FORMAT.format(this.player.getTotalExperience()));
        addLine("");
        addLine("&5&lVersion");
        addLine("&f" + plugin.getDescription().getVersion());
        this.player.setDisplayBoard(this);
        send(this.player.getBukkitPlayer());
    }
    
    public void updateLines() {
        Location location = player.getLocation();
        setDateLine("&7" + DATE_FORMAT.format(new Date(System.currentTimeMillis())));
        if (plugin.getSpawn().contains(location)) {
            setClaimLine(SPAWN);
        } else if (plugin.getWarzone().contains(location)) {
            setClaimLine(WARZONE);
        } else {
            Territory territory = plugin.getTerritoryManager().getTerritory(location);
            if (territory != null) {
                String line = "&e" + territory.getName() + " (Hamlet)";
                if (territory.getMember(player.getUniqueId()) == null) {
                    if (territory.getPrivacy() == Privacy.PRIVATE) {
                        if (player.hasPermission("realms.staff.claim.override")) {
                            line += " [SO]";
                        } else {
                            if (player.getBukkitPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                line += " [I]";
                            }
                        }
                    }
                }
                setClaimLine(line);
            } else {
                setClaimLine(WILDERNESS);
            }
        }
    
//        Territory territory = plugin.getTerritoryManager().getTerritory(player);
//        if (territory != null) {
//            setHamletLine("&fHamlet: &a" + territory.getName());
//        } else {
//            setHamletLine("&fHamlet: &aNONE");
//        }
        
        setNameLine("&b" + player.getName());
        setXpLine("&d" + Constants.NUMBER_FORMAT.format(player.getTotalExperience()));
    }
    
    public void setClaimLine(String value) {
        this.setLine(claimLine, value);
    }
    
//    public void setHamletLine(String value) {
//        this.setLine(hamletLine, value);
//    }
    
    public void setNameLine(String value) {
        this.setLine(nameLine, value);
    }
    
    public void setXpLine(String value) {
        this.setLine(xpLine, value);
    }
    
    public void setDateLine(String value) {
        this.setLine(dateLine, value);
    }
}