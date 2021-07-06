package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.Colors;
import com.stardevmc.enforcer.objects.Variables;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.actor.PlayerActor;
import com.stardevmc.enforcer.objects.rules.Rule;
import com.stardevmc.enforcer.objects.rules.RuleViolation;
import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.target.*;
import com.stardevmc.enforcer.util.*;
import com.stardevmc.enforcer.objects.evidence.Evidence;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Punishment implements IElement, Comparable<Punishment>, ConfigurationSerializable {
    
    protected boolean active, offline = false, trainingMode = false;
    protected long date, removedDate;
    protected Evidence evidence;
    protected String removerName, id;
    protected Actor actor;
    protected String reason;
    protected String removedReason = "";
    protected Actor remover;
    protected int ruleId, violoation;
    protected String server;
    protected Target target;
    protected Type type;
    protected Visibility visibility, pardonVisibility = Visibility.STAFF_ONLY;
    
    public Punishment(Type type, String server, Actor actor, Target target, String reason, long date) {
        this("-1", type, server, actor, target, reason, date, true, Visibility.STAFF_ONLY);
    }
    
    public Punishment(String id, Type type, String server, Actor actor, Target target, String reason, long date, boolean active, Visibility visibility) {
        this.id = id;
        this.type = type;
        this.server = server;
        this.actor = actor;
        this.reason = reason;
        this.active = active;
        this.target = target;
        this.date = date;
        this.visibility = visibility;
    }
    
    protected Punishment() {
    }
    
    public Punishment(Type type, String server, Actor actor, Target target, String reason, long date, Visibility visibility) {
        this("-1", type, server, actor, target, reason, date, true, visibility);
    }
    
    public Map<String, Object> serializeBase() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("active", this.active);
        serialized.put("offline", this.offline);
        serialized.put("trainingMode", this.trainingMode);
        serialized.put("date", this.date + "");
        serialized.put("removedDate", this.removedDate + "");
        serialized.put("id", this.id + "");
        serialized.put("ruleId", this.ruleId);
        serialized.put("violation", this.violoation);
        serialized.put("punisher", this.actor);
        serialized.put("reason", this.reason);
        serialized.put("removedReason", this.removedReason);
        serialized.put("remover", this.remover);
        serialized.put("server", this.server);
        serialized.put("target", this.target);
        serialized.put("type", this.type.name());
        serialized.put("visibility", this.visibility.name());
        serialized.put("pardonVisibility", this.pardonVisibility.name());
        serialized.put("evidence", this.evidence);
        return serialized;
    }
    
    public String replaceExpireVariables(String message) {
        if (this instanceof Expireable) {
            Expireable expireable = (Expireable) this;
            if (type.equals(Type.TEMPORARY_BAN)) {
                message = (message + " " + Messages.LENGTH_FORMAT);
                message = message.replace(Variables.LENGTH, expireable.formatExpireTime());
                message = message.replace(Variables.COLOR, Colors.BAN);
                return message.replace(Variables.PUNISHMENT, "banned");
            }
            if (type.equals(Type.TEMPORARY_MUTE)) {
                message = (message + " " + Messages.LENGTH_FORMAT);
                message = message.replace(Variables.LENGTH, expireable.formatExpireTime());
                message = message.replace(Variables.COLOR, Colors.MUTE);
                return message.replace(Variables.PUNISHMENT, "muted");
            }
        }
        return message;
    }
    
    public String replacePunishmentVariables(String message) {
        if (type.equals(Type.PERMANENT_BAN)) {
            message = (message + " " + Messages.PERMANENT_FORMAT);
            message = message.replace(Variables.COLOR, Colors.BAN);
            return message.replace(Variables.PUNISHMENT, "banned");
        }
        if (type.equals(Type.PERMANENT_MUTE)) {
            message = (message + " " + Messages.PERMANENT_FORMAT);
            message = message.replace(Variables.COLOR, Colors.MUTE);
            return message.replace(Variables.PUNISHMENT, "muted");
        }
        if (type.equals(Type.JAIL)) {
            message = message.replace(Variables.COLOR, Colors.JAIL);
            return message.replace(Variables.PUNISHMENT, "jailed");
        }
        if (type.equals(Type.KICK)) {
            message = message.replace(Variables.COLOR, Colors.KICK);
            return message.replace(Variables.PUNISHMENT, "kicked");
        }
        if (type.equals(Type.WARN)) {
            message = message.replace(Variables.COLOR, Colors.WARN);
            return message.replace(Variables.PUNISHMENT, "warned");
        }
        if (type.equals(Type.BLACKLIST)) {
            message = message.replace(Variables.COLOR, Colors.BLACKLIST);
            return message.replace(Variables.PUNISHMENT, "blacklisted");
        }
        return message;
    }
    
    public abstract void reversePunishment(Actor actor, long time, String reason);
    public abstract void executePunishment(ExecuteOptions... options);
    
    public final boolean isActive() {
        return active;
    }
    
    public final void setActive(boolean active) {
        this.active = active;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public void sendPunishMessage(ExecuteOptions... options) {
        List<ExecuteOptions> executeOptions = new ArrayList<>(Arrays.asList(options));
        if (executeOptions.contains(ExecuteOptions.NO_MESSAGE)) {
            return;
        }
        
        String message = formatMessage();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!canSeeMessages(p, visibility)) { continue; }
            String msg = message;
            
            if (actor instanceof PlayerActor) {
                PlayerActor playerActor = ((PlayerActor) actor);
                if (p.getUniqueId().equals(playerActor.getUniqueId())) {
                    msg = msg.replace(Variables.PUNISHER, "&lYou");
                } else {
                    Player player = Bukkit.getPlayer(playerActor.getUniqueId());
                    if (Enforcer.getInstance().getSettingsManager().isUsingDisplayNames()) {
                        msg = msg.replace(Variables.PUNISHER, player.getDisplayName());
                    } else {
                        msg = msg.replace(Variables.PUNISHER, getPunisherName());
                    }
                }
            } else {
                msg = msg.replace(Variables.PUNISHER, "&4&lConsole");
            }
            
            p.sendMessage(Utils.color(msg));
        }
    }
    
    public final String formatMessage() {
        String message = Messages.PUNISH_FORMAT;
        message = message.replace(Variables.VISIBILITY, visibility.getPrefix());
        message = message.replace(Variables.PREFIX, server.toUpperCase());
        if (target instanceof PlayerTarget) {
            message = message.replace(Variables.TARGET, getTargetName());
        } else {
            User ipInfo = null;
            playerLoop:
            for (User info : Enforcer.getInstance().getPlayerManager().getUsers().values()) {
                if (target instanceof IPTarget) {
                    String ip = ((IPTarget) target).getIpAddress();
                    if (info.getIpAddresses().contains(ip)) {
                        ipInfo = info;
                        break;
                    }
                } else if (target instanceof IPListTarget) {
                    for (String ip : ((IPListTarget) target).getIpAddresses()) {
                        if (info.getIpAddresses().contains(ip)) {
                            ipInfo = info;
                            break playerLoop;
                        }
                    }
                }
            }
            
            message = message.replace(Variables.TARGET, ipInfo.getLastName());
        }
        String reason = getReason();
        message = message.replace(Variables.REASON, StringUtils.isEmpty(reason) ? "No reason" : reason);
        if (target instanceof PlayerTarget) {
            PlayerTarget playerTarget = ((PlayerTarget) this.target);
            Player t = Bukkit.getPlayer(playerTarget.getUniqueId());
            if (t != null) {
                message = message.replace(Variables.TARGET_STATUS, "&2");
            } else {
                message = message.replace(Variables.TARGET_STATUS, "&c");
            }
        } else {
            message = message.replace(Variables.TARGET_STATUS, "");
        }
        message = message.replace("{id}", id + "");
        message = replaceExpireVariables(message);
        message = replacePunishmentVariables(message);
        return Utils.color(message);
    }
    
    public String getPunisherName() {
        return actor.getName();
    }
    
    public String getTargetName() {
        return target.getName();
    }
    
    public final String getReason() {
        return reason;
    }
    
    public void sendRemovalMessage() {
        String message = formatRemoveMessage(this.server);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!canSeeMessages(p, pardonVisibility)) { continue; }
            String msg = message;
            if (remover instanceof PlayerActor) {
                PlayerActor playerActor = ((PlayerActor) actor);
                if (p.getUniqueId().equals(playerActor.getUniqueId())) {
                    msg = msg.replace(Variables.REMOVER, "&lYou");
                } else {
                    Player player = Bukkit.getPlayer(playerActor.getUniqueId());
                    if (Enforcer.getInstance().getSettingsManager().isUsingDisplayNames()) {
                        msg = msg.replace(Variables.REMOVER, player.getDisplayName());
                    } else {
                        msg = msg.replace(Variables.REMOVER, getPunisherName());
                    }
                }
            } else {
                msg = msg.replace(Variables.PUNISHER, "&4&lConsole");
            }
            p.sendMessage(Utils.color(msg));
        }
    }
    
    public final String formatRemoveMessage(String server) {
        String message = Messages.PARDON_FORMAT;
        message = message.replace(Variables.VISIBILITY, pardonVisibility.getPrefix());
        message = message.replace(Variables.PREFIX, server.toUpperCase());
        message = message.replace(Variables.TARGET, getTargetName());
        message = message.replace(Variables.REASON, StringUtils.isEmpty(removedReason) ? "No reason" : removedReason);
        if (type.equals(Type.PERMANENT_BAN) || type.equals(Type.TEMPORARY_BAN)) {
            message = message.replace(Variables.COLOR, Colors.BAN);
            message = message.replace(Variables.PUNISHMENT, "banned");
        } else if (type.equals(Type.PERMANENT_MUTE) || type.equals(Type.TEMPORARY_MUTE)) {
            message = message.replace(Variables.COLOR, Colors.MUTE);
            message = message.replace(Variables.PUNISHMENT, "muted");
        } else if (type.equals(Type.JAIL)) {
            message = message.replace(Variables.COLOR, Colors.JAIL);
            message = message.replace(Variables.PUNISHMENT, "jailed");
        }
        return Utils.color(message);
    }
    
    public String formatLine(String[] args) {
        if (args == null) {
            return "";
        }
        String message = "";
        if (args[0].equalsIgnoreCase("history")) {
            message += "&5[" + Variables.PUNISHMENT_STATUS + "] &e{id} &b" + Variables.TARGET + " &fwas " + Variables.COLOR + Variables.PUNISHMENT + " &fby &b" + Variables.PUNISHER + " &ffor &b" + Variables.REASON;
        } else if (args[0].equalsIgnoreCase("staffhistory")) {
            message += "&5[" + Variables.PUNISHMENT_STATUS + "] &e{id} &b" + Variables.PUNISHER + " " + Variables.COLOR + Variables.PUNISHMENT + " &b" + Variables.TARGET + " &ffor &b" + Variables.REASON;
        }
        message = message.replace(Variables.TARGET, target.getName());
        message = message.replace(Variables.REASON, getReason());
        message = message.replace("{id}", id + "");
        message = message.replace(Variables.PUNISHER, actor.getName());
        if (this instanceof Expireable) {
            Expireable expireable = (Expireable) this;
            message = (message + " " + Messages.LENGTH_FORMAT);
            message = message.replace(Variables.LENGTH, Utils.formatTime(Math.abs(this.getDate() - expireable.getExpireDate())));
            if (!expireable.isExpired() && remover == null) {
                message = message + "\n    &8- &9Expires in: " + expireable.formatExpireTime();
            }
            if (type.equals(Type.TEMPORARY_BAN)) {
                message = message.replace(Variables.COLOR, Colors.BAN);
                message = message.replace(Variables.PUNISHMENT, "banned");
            } else if (type.equals(Type.TEMPORARY_MUTE)) {
                message = message.replace(Variables.COLOR, Colors.MUTE);
                message = message.replace(Variables.PUNISHMENT, "muted");
            }
        }
        message = replacePunishmentVariables(message);
        if (this.trainingMode) {
            message = message.replace(Variables.PUNISHMENT_STATUS, "Training Mode");
        } else if (this.remover != null) {
            message = message.replace(Variables.PUNISHMENT_STATUS, "Removed");
        } else if (this instanceof Expireable) {
            Expireable expireable = (Expireable) this;
            if (expireable.isExpired()) {
                message = message.replace(Variables.PUNISHMENT_STATUS, "Expired");
            } else {
                message = message.replace(Variables.PUNISHMENT_STATUS, "Active");
            }
        } else if (this instanceof Acknowledgeable) {
            Acknowledgeable acknowledgeable = ((Acknowledgeable) this);
            if (acknowledgeable.isAcknowledged()) {
                message = message.replace(Variables.PUNISHMENT_STATUS, "Acknowledged");
            } else {
                message = message.replace(Variables.PUNISHMENT_STATUS, "Active");
            }
        } else {
            message = message.replace(Variables.PUNISHMENT_STATUS, "Active");
        }
        return Utils.color(message);
    }
    
    public long getDate() {
        return date;
    }
    
    public boolean wasOffline() {
        return offline;
    }
    
    public boolean isTrainingPunishment() {
        return trainingMode;
    }
    
    public int compareTo(Punishment o) {
        return Long.compare(this.getDate(), o.getDate());
    }
    
    public final String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getServer() {
        return server;
    }
    
    public Target getTarget() {
        return target;
    }
    
    public Actor getActor() {
        return actor;
    }
    
    public final Type getType() {
        return type;
    }
    
    public Evidence getEvidence() {
        return evidence;
    }
    
    public void setEvidence(Evidence evidence) {
        this.evidence = evidence;
    }
    
    public Actor getRemover() {
        return remover;
    }
    
    public void setRemover(Actor remover) {
        this.remover = remover;
    }
    
    public String getRemoverName() {
        if (removerName == null) {
            removerName = remover.getName();
        }
        return removerName;
    }
    
    public void setRemoverName(String name) {
        this.removerName = name;
    }
    
    public long getRemovedDate() {
        return removedDate;
    }
    
    public void setRemovedDate(long removedDate) {
        this.removedDate = removedDate;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    public Visibility getPardonVisibility() {
        return pardonVisibility;
    }
    
    public void setPardonVisibility(Visibility pardonVisibility) {
        this.pardonVisibility = pardonVisibility;
    }
    
    public int getRuleId() {
        return ruleId;
    }
    
    public int getViolationNumber() {
        return violoation;
    }
    
    public String getRemovedReason() {
        return removedReason;
    }
    
    public void setRemovedReason(String removedReason) {
        this.removedReason = removedReason;
    }
    
    public void setRuleViolation(Rule rule, RuleViolation violation) {
        this.ruleId = rule.getId();
        this.violoation = violation.getViolationNumber();
    }
    
    public void setRuleViolation(int rule, int violation) {
        this.ruleId = rule;
        this.violoation = violation;
    }
    
    public void setOffline(boolean offline) {
        this.offline = offline;
    }
    
    public void setTrainingMode(boolean trainingMode) {
        this.trainingMode = trainingMode;
    }
    
    public static PunishmentBuilder deserializeBase(Map<String, Object> serialized) {
        PunishmentBuilder puBuilder = new PunishmentBuilder();
        boolean active = (boolean) serialized.get("active");
        boolean offline = (boolean) serialized.get("offline");
        boolean trainingMode = (boolean) serialized.get("trainingMode");
        long date = Long.parseLong((String) serialized.get("date"));
        long removedDate = Long.parseLong((String) serialized.get("removedDate"));
        Evidence evidence = (Evidence) serialized.get("evidence");
        String id = (String) serialized.get("id");
        int ruleId = (int) serialized.get("ruleId");
        int violation = (int) serialized.get("violation");
        Actor punisher = (Actor) serialized.get("punisher");
        String reason = (String) serialized.get("reason");
        String removedReason = (String) serialized.get("removedReason");
        Actor remover = (Actor) serialized.get("remover");
        String server = (String) serialized.get("server");
        Target target = (Target) serialized.get("target");
        Type type = Type.valueOf((String) serialized.get("type"));
        Visibility visibility = Visibility.valueOf((String) serialized.get("visibility"));
        Visibility pardonVisibility = Visibility.valueOf((String) serialized.get("pardonVisibility"));
        puBuilder.setRuleId(ruleId).setTarget(target).setReason(reason).setViolationNumber(violation).setActive(active).setOffline(offline).setType(type).setTrainingMode(trainingMode).setDate(date).setRemovedDate(removedDate).setEvidence(evidence).setId(id).setPunisher(punisher).setRemovedReason(removedReason).setRemover(remover).setServer(server).setVisibility(visibility).setPardonVisibility(pardonVisibility);
        return puBuilder;
    }
    
    public static boolean canSeeMessages(Player p, Visibility visibility) {
        if (visibility.equals(Visibility.STAFF_ONLY)) {
            return p.hasPermission(Perms.NOTIFY_PUNISHMENTS);
        }
        if (visibility.equals(Visibility.SILENT)) {
            try {
                net.milkbowl.vault.permission.Permission perms = Enforcer.getInstance().getPermission();
                if (perms != null) {
                    String groupName = perms.getPrimaryGroup(p).toLowerCase();
                    return p.hasPermission("enforcer.punishments.notify." + groupName);
                }
                return p.hasPermission(Perms.NOTIFY_PUNISHMENTS);
            } catch (Exception ignored) {
            
            }
        }
        return true;
    }
    
    public enum Type {
        PERMANENT_BAN("&4", "&4&lPERMANENT BAN", PermanentBan.class, Material.RED_WOOL, "BAN"),
        TEMPORARY_BAN("&c", "&c&lTEMPORARY BAN", TemporaryBan.class, Material.RED_WOOL, "TEMP_BAN", "TEMPBAN"),
        PERMANENT_MUTE("&7", "&1&lPERMANENT MUTE", PermanentMute.class, Material.LIGHT_GRAY_WOOL, "MUTE"),
        TEMPORARY_MUTE("&7", "&9&lTEMPORARY MUTE", TemporaryMute.class, Material.LIGHT_GRAY_WOOL, "TEMP_MUTE", "TEMPMUTE"),
        WARN("&e", "&e&lWARN", Material.YELLOW_WOOL, WarnPunishment.class), KICK("&a", "&a&lKICK", Material.LIME_WOOL, KickPunishment.class),
        JAIL("&d", "&d&lJAIL", Material.PINK_WOOL, JailPunishment.class),
        BLACKLIST("&8", "&8&L", Material.BLACK_WOOL, BlacklistPunishment.class);
        
        private String color;
        private String displayName;
        private Material material;
        private Class<? extends Punishment> clazz;
        
        private String[] aliases;
        
        Type() {
        }
        
        Type(String color, String displayName, Material material, Class<? extends Punishment> clazz) {
            this.color = color;
            this.displayName = displayName;
            this.material = material;
            this.clazz = clazz;
        }
        
        Type(String color, String displayName, Class<? extends Punishment> clazz, Material material, String... aliases) {
            this(color, displayName, material, clazz);
            this.aliases = aliases;
        }
        
        public String getColor() {
            return color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String[] getAliases() {
            return aliases;
        }
        
        public Class<? extends Punishment> getPunishmentClass() {
            return clazz;
        }
        
        public static Punishment.Type getType(String name) {
            try {
                return Punishment.Type.valueOf(name);
            } catch (Exception e) {
                for (Punishment.Type t : Punishment.Type.values()) {
                    if (t.getAliases() != null) {
                        for (String alias : t.getAliases()) {
                            if (alias.equalsIgnoreCase(name)) return t;
                        }
                    }
                }
            }
            return null;
        }
        
        public Material getMaterial() {
            return material;
        }
    }
}