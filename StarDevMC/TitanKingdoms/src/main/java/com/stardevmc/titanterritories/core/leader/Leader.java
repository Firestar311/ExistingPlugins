package com.stardevmc.titanterritories.core.leader;

import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public abstract class Leader<T> implements ConfigurationSerializable {
    protected T object;
    protected long joinDate;
    
    public Leader(T object, long joinDate) {
        this.object = object;
        this.joinDate = joinDate;
    }
    
    public T getObject() {
        return object;
    }
    
    public void setObject(T object) {
        this.object = object;
    }
    
    public long getJoinDate() {
        return joinDate;
    }
    
    public abstract IUser getUser();
    
    public abstract String getName();
    public abstract void sendMessage(String message);
    
}