package com.stardevmc.enforcer.objects.punishment;

public interface Expireable {
    
    long getExpireDate();
    long getLength();
    void setLength(long length);
    boolean isExpired();
    String formatExpireTime();
    void onExpire();
    void setExpireDate(long expireDate);
}