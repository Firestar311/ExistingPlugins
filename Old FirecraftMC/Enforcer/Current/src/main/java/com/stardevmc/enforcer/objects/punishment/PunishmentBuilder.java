package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.punishment.Punishment.Type;
import com.stardevmc.enforcer.objects.enums.RawType;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.actor.PlayerActor;
import com.stardevmc.enforcer.objects.target.Target;
import com.stardevmc.enforcer.objects.evidence.Evidence;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class PunishmentBuilder {
    
    protected boolean active, offline = false, trainingMode = false;
    protected Actor actor;
    protected long date, removedDate, length;
    protected Evidence evidence;
    protected String id;
    protected RawType rawType;
    protected String reason;
    protected String removedReason;
    protected Actor remover;
    protected int ruleId = -1, violationNumber = -1, prisonId;
    protected String server;
    protected Target target;
    protected Type type;
    protected Visibility visibility, pardonVisibility = Visibility.STAFF_ONLY;
    
    public PunishmentBuilder(Target target, Type type) {
        this.target = target;
    }
    
    public PunishmentBuilder(Map<String, Object> serialized) {
        for (Field field : getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (serialized.containsKey(field.getName())) {
                    if (field.getType().isEnum()) {
                        Class<?> enumClass = field.getType();
                        Method valueOf = enumClass.getMethod("valueOf", String.class);
                        field.set(this, valueOf.invoke(null, (String) serialized.get(field.getName())));
                    } else if (field.getType().isAssignableFrom(UUID.class)) {
                        field.set(this, UUID.fromString((String) serialized.get(field.getName())));
                    } else {
                        field.set(this, serialized.get(field.getName()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public PunishmentBuilder(Target target) {
        this.target = target;
    }
    
    public PunishmentBuilder() {
    }
    
    public boolean isOffline() {
        return offline;
    }
    
    public PunishmentBuilder setOffline(boolean offline) {
        this.offline = offline;
        return this;
    }
    
    public boolean isTrainingMode() {
        return trainingMode;
    }
    
    public PunishmentBuilder setTrainingMode(boolean trainingMode) {
        this.trainingMode = trainingMode;
        return this;
    }
    
    public Punishment build() {
        performChecks();
        Punishment punishment = null;
        switch (type) {
            case PERMANENT_BAN:
                punishment = new PermanentBan(server, actor, target, reason, date, visibility);
                break;
            case TEMPORARY_BAN:
                punishment = new TemporaryBan(server, actor, target, reason, date, visibility, length);
                break;
            case PERMANENT_MUTE:
                punishment = new PermanentMute(server, actor, target, reason, date, visibility);
                break;
            case TEMPORARY_MUTE:
                punishment = new TemporaryMute(server, actor, target, reason, date, visibility, length);
                break;
            case WARN:
                punishment = new WarnPunishment(server, actor, target, reason, date, visibility);
                break;
            case KICK:
                punishment = new KickPunishment(server, actor, target, reason, date, visibility);
                break;
            case JAIL:
                punishment = new JailPunishment(server, actor, target, reason, date, visibility, prisonId);
                break;
            case BLACKLIST:
                punishment = new BlacklistPunishment(server, actor, target, reason, date, visibility);
        }
        if (this.ruleId > -1 || this.violationNumber > -1) {
            punishment.setRuleViolation(ruleId, violationNumber);
        }
        
        punishment.setOffline(offline);
        punishment.setTrainingMode(trainingMode);
        punishment.setEvidence(evidence);
        punishment.setRemover(remover);
        punishment.setRemovedDate(removedDate);
        punishment.setPardonVisibility(pardonVisibility);
        punishment.setRemovedReason(removedReason);
        punishment.setActive(active);
        punishment.setId(id);
        return punishment;
    }
    
    public void performChecks() {
        if (actor == null) {
            throw new IllegalArgumentException("Punisher must not be null");
        }
        
        if (target == null) {
            throw new IllegalArgumentException("Target must not be null");
        }
        
        if (rawType != null) {
            if (rawType == RawType.BAN) {
                if (length == -1) {
                    type = Type.PERMANENT_BAN;
                } else if (length > 0) {
                    type = Type.TEMPORARY_BAN;
                }
            } else if (rawType == RawType.MUTE) {
                if (length == -1) {
                    type = Type.PERMANENT_MUTE;
                } else if (length > 0) {
                    type = Type.TEMPORARY_MUTE;
                }
            }
        }
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
        
        if (StringUtils.isEmpty(reason)) {
            throw new IllegalArgumentException("Reason must not be null");
        }
        
        if (date == 0) {
            throw new IllegalArgumentException("Date must not be 0");
        }
        
        if (visibility == null) {
            this.visibility = Visibility.STAFF_ONLY;
        }
        
        if (StringUtils.isEmpty(server)) {
            this.server = Enforcer.getInstance().getSettingsManager().getServerName();
        }
        
        if (type.equals(Type.JAIL)) {
            if (prisonId == -1) {
                throw new IllegalArgumentException("Punishment Type is of Jail Type and does not have a prison id set.");
            }
        }
        
        if (type.getPunishmentClass().isAssignableFrom(Expireable.class)) {
            if (this.length == 0) {
                throw new IllegalArgumentException("Punishment type can expire but no length for that time is defined");
            }
        }
    }
    
    public String getRemovedReason() {
        return removedReason;
    }
    
    public PunishmentBuilder setRemovedReason(String removedReason) {
        this.removedReason = removedReason;
        return this;
    }
    
    public int getRuleId() {
        return ruleId;
    }
    
    public PunishmentBuilder setRuleId(int ruleId) {
        this.ruleId = ruleId;
        return this;
    }
    
    public int getViolationNumber() {
        return violationNumber;
    }
    
    public PunishmentBuilder setViolationNumber(int violationNumber) {
        this.violationNumber = violationNumber;
        return this;
    }
    
    public Type getType() {
        return type;
    }
    
    public PunishmentBuilder setType(Type type) {
        this.type = type;
        return this;
    }
    
    public String getServer() {
        return server;
    }
    
    public PunishmentBuilder setServer(String server) {
        this.server = server;
        return this;
    }
    
    public Actor getActor() {
        return actor;
    }
    
    public Target getTarget() {
        return target;
    }
    
    public PunishmentBuilder setTarget(Target target) {
        this.target = target;
        return this;
    }
    
    public String getReason() {
        return reason;
    }
    
    public PunishmentBuilder setReason(String reason) {
        this.reason = reason;
        return this;
    }
    
    public long getDate() {
        return date;
    }
    
    public PunishmentBuilder setDate(long date) {
        this.date = date;
        return this;
    }
    
    public long getLength() {
        return length;
    }
    
    public PunishmentBuilder setLength(long length) {
        this.length = length;
        return this;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public PunishmentBuilder setVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }
    
    public int getPrisonId() {
        return prisonId;
    }
    
    public PunishmentBuilder setPrisonId(int prisonId) {
        this.prisonId = prisonId;
        return this;
    }
    
    public RawType getRawType() {
        return rawType;
    }
    
    public PunishmentBuilder setRawType(RawType rawType) {
        this.rawType = rawType;
        return this;
    }
    
    public PunishmentBuilder setPunisher(UUID actor) {
        this.actor = new PlayerActor(actor);
        return this;
    }
    
    public PunishmentBuilder setPunisher(Actor actor) {
        this.actor = actor;
        return this;
    }
    
    public PunishmentBuilder setActive(boolean active) {
        this.active = active;
        return this;
    }
    
    public PunishmentBuilder setRemovedDate(long removedDate) {
        this.removedDate = removedDate;
        return this;
    }
    
    public PunishmentBuilder setEvidence(Evidence evidence) {
        this.evidence = evidence;
        return this;
    }
    
    public PunishmentBuilder setId(String id) {
        this.id = id;
        return this;
    }
    
    public PunishmentBuilder setRemover(Actor remover) {
        this.remover = remover;
        return this;
    }
    
    public PunishmentBuilder setPardonVisibility(Visibility pardonVisibility) {
        this.pardonVisibility = pardonVisibility;
        return this;
    }
}