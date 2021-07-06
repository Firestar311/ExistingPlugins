package net.firecraftmc.api.punishments;

import net.firecraftmc.api.paginator.Paginatable;
import net.firecraftmc.api.util.Utils;

import java.util.UUID;

import static net.firecraftmc.api.punishments.Punishment.Colors.BAN;
import static net.firecraftmc.api.punishments.Punishment.Colors.MUTE;
import static net.firecraftmc.api.punishments.Punishment.Variables.*;

public class Punishment implements Paginatable {
    
    protected int id;
    protected final Type type;
    protected final String server;
    protected final UUID punisher;
    protected final UUID target;
    protected final String reason;
    protected final long date;
    protected long expire;
    protected boolean active = true;
    
    private String localPunisherName, localTargetName;
    
    private UUID remover;
    private String removerName;
    
    protected final String punishFormat = "&6(" + SERVER + ") &4&l[i] &c<ID:{id}> &b" + TARGET + " &fwas " + COLOR + PUNISHMENT + " &fby &b" + PUNISHER + " &ffor &b" + REASON;
    protected final String lengthFormat = "&c(" + LENGTH + ")";
    protected final String permanentFormat = "&c(Permanent)";
    protected final String unPunishFormat = "&6(" + SERVER + ") &4&l[i] &b" + TARGET + " &fwas " + COLOR + "un" + PUNISHMENT + " &fby &b" + REMOVER;
    private boolean acknowledged;
    
    
    public Punishment(Type type, String server, UUID punisher, UUID target, String reason, long date) {
        this.server = server;
        this.type = type;
        this.punisher = punisher;
        this.target = target;
        this.reason = reason;
        this.date = date;
    }
    
    public Punishment(int id, Type type, String server, UUID punisher, UUID target, String reason, long date, boolean active) {
        this.id = id;
        this.type = type;
        this.server = server;
        this.punisher = punisher;
        this.reason = reason;
        this.active = active;
        this.target = target;
        this.date = date;
    }
    
    public void setPunisherName(String punisherName) {
        this.localPunisherName = punisherName;
    }
    
    public void setTargetName(String targetName) {
        this.localTargetName = targetName;
    }
    
    public final int getId() {
        return id;
    }
    
    public String getPunisherName() {
        return localPunisherName;
    }
    
    public String getTargetName() {
        return localTargetName;
    }
    
    public String getServer() {
        return server;
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public UUID getPunisher() {
        return punisher;
    }
    
    public final String getReason() {
        return reason;
    }
    
    public final boolean isActive() {
        return active;
    }
    
    public final void setActive(boolean active) {
        this.active = active;
    }
    
    public final Type getType() {
        return type;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public final String formatMessage() {
        String message = punishFormat;
        message = message.replace(SERVER, server.toUpperCase());
        message = message.replace(TARGET, getTargetName());
        message = message.replace(PUNISHER, getPunisherName());
        message = message.replace(REASON, reason);
        message = message.replace("{id}", id + "");
        if (type.equals(Type.BAN)) {
            message = (message + " " + permanentFormat);
            message = message.replace(COLOR, BAN);
            message = message.replace(PUNISHMENT, "banned");
        } else if (type.equals(Type.MUTE)) {
            message = (message + " " + permanentFormat);
            message = message.replace(COLOR, MUTE);
            message = message.replace(PUNISHMENT, "muted");
        } else if (type.equals(Type.TEMP_BAN)) {
            message = (message + " " + lengthFormat);
            message = message.replace(LENGTH, formatExpireTime());
            message = message.replace(COLOR, BAN);
            message = message.replace(PUNISHMENT, "banned");
        } else if (type.equals(Type.TEMP_MUTE)) {
            message = (message + " " + lengthFormat);
            message = message.replace(LENGTH, formatExpireTime());
            message = message.replace(COLOR, MUTE);
            message = message.replace(PUNISHMENT, "muted");
        } else if (type.equals(Type.JAIL)) {
            message = message.replace(COLOR, Colors.JAIL);
            message = message.replace(PUNISHMENT, "jailed");
        } else if (type.equals(Type.KICK)) {
            message = message.replace(COLOR, Colors.KICK);
            message = message.replace(PUNISHMENT, "kicked");
        } else if (type.equals(Type.WARN)) {
            message = message.replace(COLOR, Colors.WARN);
            message = message.replace(PUNISHMENT, "warned");
        }
        return message;
    }
    
    public final String formatRemoveMessage(String server) {
        String message = unPunishFormat;
        message = message.replace(SERVER, server);
        message = message.replace(TARGET, localTargetName);
        message = message.replace(REMOVER, removerName);
        if (type.equals(Type.BAN) || type.equals(Type.TEMP_BAN)) {
            message = message.replace(COLOR, Colors.BAN);
            message = message.replace(PUNISHMENT, "banned");
        } else if (type.equals(Type.MUTE) || type.equals(Type.TEMP_MUTE)) {
            message = message.replace(COLOR, Colors.MUTE);
            message = message.replace(PUNISHMENT, "muted");
        } else if (type.equals(Type.JAIL)) {
            message = message.replace(COLOR, Colors.JAIL);
            message = message.replace(PUNISHMENT, "jailed");
        } else if (type.equals(Type.KICK)) {
            message = message.replace(COLOR, Colors.KICK);
            message = message.replace(PUNISHMENT, "kicked");
        }
        
        return message;
    }
    
    public void setRemover(UUID remover) {
        this.remover = remover;
    }
    
    public UUID getRemover() {
        return remover;
    }
    
    public void setRemoverName(String name) {
        this.removerName = name;
    }
    
    public String getRemoverName() {
        return removerName;
    }
    
    public final long getExpire() {
        return expire;
    }
    
    public final boolean isExpired() {
        return System.currentTimeMillis() >= expire;
    }
    
    public final String formatExpireTime() {
        return Utils.Time.formatTime(expire - System.currentTimeMillis());
    }
    
    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
    
    public boolean isAcknowledged() {
        return acknowledged;
    }
    
    public String formatLine() {
        return "§b" + localTargetName + " " + type + " " + localPunisherName + " " + reason;
    }
    
    public final class Variables {
        public static final String REMOVER = "<remover>";
        public static final String SERVER = "<server>";
        public static final String TARGET = "<target>";
        public static final String COLOR = "<color>";
        public static final String PUNISHMENT = "<punishment>";
        public static final String PUNISHER = "<punisher>";
        public static final String REASON = "<reason>";
        public static final String LENGTH = "<length>";
    }
    
    public final class Colors {
        public static final String BAN = "&4";
        public static final String MUTE = "&9";
        public static final String KICK = "&a";
        public static final String WARN = "&e";
        public static final String JAIL = "&d";
    }
    
    public enum Type {
        BAN, TEMP_BAN, MUTE, TEMP_MUTE, JAIL, KICK, WARN, IP_BAN
    }
}