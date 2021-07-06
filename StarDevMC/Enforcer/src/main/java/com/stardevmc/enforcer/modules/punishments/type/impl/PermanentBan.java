package com.stardevmc.enforcer.modules.punishments.type.impl;

import com.stardevmc.enforcer.modules.punishments.Visibility;
import com.stardevmc.enforcer.modules.punishments.actor.Actor;
import com.stardevmc.enforcer.modules.punishments.target.Target;
import com.stardevmc.enforcer.modules.punishments.type.PunishmentType;
import com.stardevmc.enforcer.modules.punishments.type.abstraction.BanPunishment;

public class PermanentBan extends BanPunishment {
    
    public PermanentBan(String server, Actor punisher, Target target, String reason, long date) {
        super(PunishmentType.PERMANENT_BAN, server, punisher, target, reason, date);
    }
    
    public PermanentBan(String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(PunishmentType.PERMANENT_BAN, server, punisher, target, reason, date, visibility);
    }
    
    public PermanentBan(int id, String server, Actor punisher, Target target, String reason, long date, boolean active, boolean purgatory, Visibility visibility) {
        super(id, PunishmentType.PERMANENT_BAN, server, punisher, target, reason, date, active, purgatory, visibility);
    }
}