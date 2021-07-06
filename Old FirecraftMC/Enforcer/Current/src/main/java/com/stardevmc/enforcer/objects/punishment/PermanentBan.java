package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.Target;

import java.util.Map;

public class PermanentBan extends BanPunishment {
    
    public PermanentBan(String server, Actor punisher, Target target, String reason, long date) {
        super(Type.PERMANENT_BAN, server, punisher, target, reason, date);
    }
    
    public PermanentBan(String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(Type.PERMANENT_BAN, server, punisher, target, reason, date, visibility);
    }
    
    public PermanentBan(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility) {
        super(id, Type.PERMANENT_BAN, server, punisher, target, reason, date, active, visibility);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return super.serializeBase();
    }
    
    public static PermanentBan deserialize(Map<String, Object> serialized) {
        return (PermanentBan) deserializeBase(serialized).build();
    }
}