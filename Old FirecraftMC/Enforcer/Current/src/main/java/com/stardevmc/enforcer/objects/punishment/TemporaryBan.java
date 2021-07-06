package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.util.Utils;

import java.util.Map;

public class TemporaryBan extends BanPunishment implements Expireable {
    
    private long length;
    
    public TemporaryBan(String server, Actor punisher, Target target, String reason, long date, long length) {
        super(Type.TEMPORARY_BAN, server, punisher, target, reason, date);
        this.length = length;
    }
    
    public TemporaryBan(String server, Actor punisher, Target target, String reason, long date, Visibility visibility, long length) {
        super(Type.TEMPORARY_BAN, server, punisher, target, reason, date, visibility);
        this.length = length;
    }
    
    public TemporaryBan(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility, long length) {
        super(id, Type.TEMPORARY_BAN, server, punisher, target, reason, date, active, visibility);
        this.length = length;
    }
    
    public long getExpireDate() {
        return this.date + length;
    }
    
    @Override
    public long getLength() {
        return length;
    }
    
    @Override
    public void setLength(long length) {
        this.length = length;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() >= getExpireDate();
    }
    
    public String formatExpireTime() {
        return Utils.formatTime(getExpireDate() - System.currentTimeMillis());
    }
    
    public void onExpire() {
        setActive(false);
    }
    
    public void setExpireDate(long expireDate) {
        this.length = expireDate - System.currentTimeMillis();
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        serialized.put("length", this.length + "");
        return serialized;
    }
    
    public static TemporaryBan deserialize(Map<String, Object> serialized) {
        long length = Long.parseLong((String) serialized.get("length"));
        TemporaryBan tempBan = (TemporaryBan) BanPunishment.deserialize(serialized);
        tempBan.length = length;
        return tempBan;
    }
}
