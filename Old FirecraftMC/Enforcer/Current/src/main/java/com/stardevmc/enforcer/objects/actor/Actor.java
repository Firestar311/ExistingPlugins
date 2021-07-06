package com.stardevmc.enforcer.objects.actor;

import com.stardevmc.enforcer.Enforcer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public abstract class Actor implements ConfigurationSerializable {
    
    public Actor() {
        try {
            Enforcer.getInstance().getActorModule().getManager().addActor(this);
        } catch (Exception e) {}
    }
    
    public abstract String getName();
    
    public abstract Player getPlayer();
    
    public boolean equals(Actor o) {
        return this.getName().equals(o.getName());
    }
}