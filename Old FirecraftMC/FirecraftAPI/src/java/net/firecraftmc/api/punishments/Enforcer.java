package net.firecraftmc.api.punishments;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.punishments.Punishment.Type;

import java.time.Duration;
import java.util.List;

public final class Enforcer {
    private static final Enforcer instance = new Enforcer();
    
    public static Enforcer getInstance() {
        return instance;
    }
    
    private Enforcer() {
    }
    
    public static Punishment createPunishment(FirecraftPlayer target, FirecraftPlayer punisher, Punishment.Type type, String reason, long date, long expire) {
        Punishment punishment;
        if (expire == 0) {
            punishment = new PermanentPunishment(type, FirecraftAPI.getServer().getName(), punisher.getUniqueId(), target.getUniqueId(), reason, date);
        } else if (expire == -1) {
            punishment = new Punishment(type, FirecraftAPI.getServer().getName(), punisher.getUniqueId(), target.getUniqueId(), reason, date);
        } else {
            punishment = new TemporaryPunishment(type, FirecraftAPI.getServer().getName(), punisher.getUniqueId(), target.getUniqueId(), reason, date, expire);
        }
        punishment.setActive(true);
        punishment.setPunisherName(punisher.getName());
        punishment.setTargetName(target.getName());
        
        punishment = FirecraftAPI.getDatabase().addPunishment(punishment);
        return punishment;
    }
    
    public static Punishment createPunishment(FirecraftPlayer target, FirecraftPlayer punisher, long date, Rule rule, RulePunishment punishment) {
        return createPunishment(target, punisher, punishment.getType(), rule.getName() + " Offense #" + punishment.getOffenseNumber(), date, date + punishment.getLength());
    }
    
    public static long calculateExpireDate(long currentDate, String rawText) {
        String expireTime = "P";
        String time = rawText.toUpperCase();
        String[] a = time.split("d".toUpperCase());
        
        if (a.length == 1) {
            expireTime += a[0].contains("H") || a[0].contains("M") || a[0].contains("S") ? "T" + a[0] : a[0] + "d";
        } else if (a.length == 2) {
            expireTime = a[0] + "dT" + a[1];
        }
        
        long expire = Duration.parse(expireTime).toMillis();
        return currentDate + expire;
    }
    
    public static RulePunishment getNextPunishment(Rule rule, FirecraftPlayer player) {
        List<Punishment> punishments = FirecraftAPI.getDatabase().getPunishments(player.getUniqueId());
        int offenseCount = 1;
        for (Punishment punishment : punishments) {
            if (punishment.getReason().toLowerCase().startsWith(rule.getName().toLowerCase())) {
                offenseCount++;
            }
        }
        
        RulePunishment punishment;
        
        punishment = offenseCount > rule.getMaxOffenses() ? rule.getPunishment(rule.getMaxOffenses()) : rule.getPunishment(offenseCount);
        
        return punishment;
    }
    
    public static RulePunishment getNextPunishment(FirecraftPlayer staff, Rule rule, FirecraftPlayer target) {
        List<Punishment> punishments = FirecraftAPI.getDatabase().getPunishments(target.getUniqueId());
        int offenseCount = 1;
        for (Punishment punishment : punishments) {
            if (punishment.getReason().toLowerCase().startsWith(rule.getName().toLowerCase())) {
                if (punishment.getType().equals(Punishment.Type.JAIL)) {
                    if (punishment.getRemover() == null) {
                        break;
                    }
                }
                
                offenseCount++;
            }
        }
        RulePunishment punishment = offenseCount > rule.getMaxOffenses() ? rule.getPunishment(rule.getMaxOffenses()) : rule.getPunishment(offenseCount);
        
        if (staff.getMainRank() == Rank.TRIAL_MOD) {
            if (punishment.getType().equals(Type.TEMP_BAN) || punishment.getType().equals(Type.BAN) || punishment.getType().equals(Type.MUTE)) {
                return new RulePunishment(punishment.getOffenseNumber(), Type.JAIL, -1);
            }
        }
        
        return punishment;
    }
}
