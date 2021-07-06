package net.firecraftmc.api.punishments;

import java.util.UUID;

public class TemporaryPunishment extends Punishment {
    
    public TemporaryPunishment(Type type, String server, UUID punisher, UUID target, String reason, long date, long expire) {
        super(type, server, punisher, target, reason, date);
        this.expire = expire;
    }
    
    public TemporaryPunishment(int id, Type type, String server, UUID punisher, UUID target, String reason, long date, long expire, boolean active) {
        super(id, type, server, punisher, target, reason, date, active);
        this.expire = expire;
    }
}