package com.stardevmc.enforcer.objects.punishment;

public interface Acknowledgeable {
    
    boolean isAcknowledged();
    void setAcknowledged(boolean value);
    void onAcknowledge();
}