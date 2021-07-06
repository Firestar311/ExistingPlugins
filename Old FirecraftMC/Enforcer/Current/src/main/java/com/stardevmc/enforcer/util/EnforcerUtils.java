package com.stardevmc.enforcer.util;

import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.punishment.Punishment.Type;
import com.stardevmc.enforcer.objects.punishment.PunishmentBuilder;
import com.stardevmc.enforcer.objects.rules.*;
import com.starmediadev.lib.pagination.Paginator;
import com.starmediadev.lib.pagination.PaginatorFactory;
import com.starmediadev.lib.util.Unit;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class EnforcerUtils {
    public static Paginator<Punishment> generatePaginatedPunishmentList(List<Punishment> IPunishments, String header, String footer) {
        Collections.sort(IPunishments);
        PaginatorFactory<Punishment> factory = new PaginatorFactory<>();
        factory.setMaxElements(7).setHeader(header).setFooter(footer);
        IPunishments.forEach(factory::addElement);
        return factory.build();
    }
    
    public static String getPunishString(PunishmentBuilder puBuilder) {
        return getPunishString(puBuilder.getType(), puBuilder.getLength());
    }
    
    public static String getPunishString(Type type, long length) {
        String punishmentString = type.getColor();
        switch (type) {
            case PERMANENT_BAN:
                punishmentString += "Permanent Ban";
                break;
            case TEMPORARY_BAN:
                punishmentString += "Ban for " + Utils.formatTime(length);
                break;
            case PERMANENT_MUTE:
                punishmentString += "Permanent Mute";
                break;
            case TEMPORARY_MUTE:
                punishmentString += "Mute for " + Utils.formatTime(length);
                break;
            case WARN:
                punishmentString += "Warning";
                break;
            case KICK:
                punishmentString += "Kick";
                break;
            case JAIL:
                punishmentString += "Jail";
                break;
        }
        return punishmentString;
    }
    
    public static SortedMap<Integer, Rule> getOldRules(FileConfiguration config) {
        SortedMap<Integer, Rule> rules = new TreeMap<>();
        
        for (String r : config.getConfigurationSection("rules").getKeys(false)) {
            Rule rule = new Rule(config.getInt("rules." + r + ".id"), r, config.getString("rules." + r + ".name"), config.getString("rules." + r + ".description"));
            if (config.contains("rules." + r + ".material")) {
                rule.setMaterial(Material.valueOf(config.getString("rules." + r + ".material").toUpperCase()));
            } else {
                rule.setMaterial(Material.STONE);
            }
            if (config.contains("rules." + r + ".offenses")) {
                for (String o : config.getConfigurationSection("rules." + r + ".offenses").getKeys(false)) {
                    int offenseNumber = Integer.parseInt(o);
                    RuleViolation action = new RuleViolation(rule, offenseNumber);
                    int actionLength = 0;
                    if (config.contains("rules." + r + ".offenses." + o + ".length")) {
                        actionLength = config.getInt("rules." + r + ".offenses." + o + ".length");
                    }
                    action.setLength(actionLength);
                    for (String a : config.getConfigurationSection("rules." + r + ".offenses." + o + ".actions").getKeys(false)) {
                        int aN = Integer.parseInt(a);
                        Type type = Type.getType(config.getString("rules." + r + ".offenses." + o + ".actions." + a + ".punishment").toUpperCase());
                        int rawLength = -1;
                        String units = "";
                        int id = -1;
                        if (config.contains("rules." + r + ".offenses." + o + ".actions." + a + ".length")) {
                            rawLength = config.getInt("rules." + r + ".offenses." + o + ".actions." + a + ".length");
                        }
                        
                        if (config.contains("rules." + r + ".offenses." + o + ".actions." + a + ".unit")) {
                            units = config.getString("rules." + r + ".offenses." + o + ".actions." + a + ".unit");
                        }
                        
                        if (config.contains("rules." + r + ".offenses." + o + ".actions." + a + ".id")) {
                            id = config.getInt("rules." + r + ".offenses." + o + ".actions." + a + ".id");
                        }
                        
                        long length = -1;
                        if (!StringUtils.isEmpty(units)) {
                            Unit unit = Unit.matchUnit(units);
                            length = unit.convertTime(rawLength);
                        }
                        
                        RulePunishment punishment = new RulePunishment(type, length, rawLength, units);
                        punishment.setId(id);
                        action.addPunishment(aN, punishment);
                    }
                    rule.addViolation(offenseNumber, action);
                }
            }
            rules.put(rule.getId(), rule);
        }
        return rules;
    }
    
    public static String getReason(int index, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                sb.append(args[i]);
                if (i != args.length - 1) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
}
