package net.firecraftmc.api.punishments;

import java.util.UUID;

public class PermanentPunishment extends Punishment {
    
    public PermanentPunishment(Type type, String server, UUID punisher, UUID target, String reason, long date) {
        super(type, server, punisher, target, reason, date);
    }
    
    public PermanentPunishment(int id, Type type, String server, UUID punisher, UUID target, String reason, long date, boolean active) {
        super(id, type, server, punisher, target, reason, date, active);
    }
}