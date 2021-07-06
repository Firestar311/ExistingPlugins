package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.util.Utils;

import java.util.Map;

public class TemporaryMute extends MutePunishment implements Expireable {
    
    private long length;
    
    public TemporaryMute(String server, Actor punisher, Target target, String reason, long date, long length) {
        super(Type.TEMPORARY_MUTE, server, punisher, target, reason, date);
        this.length = length;
    }
    
    public TemporaryMute(String server, Actor punisher, Target target, String reason, long date, Visibility visibility, long length) {
        super(Type.TEMPORARY_MUTE, server, punisher, target, reason, date, visibility);
        this.length = length;
    }
    
    public TemporaryMute(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility, long length) {
        super(id, Type.TEMPORARY_MUTE, server, punisher, target, reason, date, active, visibility);
        this.length = length;
    }
    
    public static TemporaryMute deserialize(Map<String, Object> serialized) {
        long length = Long.parseLong((String) serialized.get("length"));
        TemporaryMute tempMute = (TemporaryMute) MutePunishment.deserialize(serialized);
        tempMute.length = length;
        return tempMute;
    }    public long getExpireDate() {
        return this.date + this.length;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        serialized.put("length", this.length + "");
        return serialized;
    }    @Override
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
        active = false;
    }
    
    public void setExpireDate(long expireDate) {
        this.length = expireDate - System.currentTimeMillis();
    }
}
