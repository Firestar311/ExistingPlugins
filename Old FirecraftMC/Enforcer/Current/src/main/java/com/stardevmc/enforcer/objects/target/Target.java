package com.stardevmc.enforcer.objects.target;

import com.stardevmc.enforcer.Enforcer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public abstract class Target implements ConfigurationSerializable {
    
    public Target() {
        Enforcer.getInstance().getTargetModule().getManager().addTarget(this);
    }
    
    public abstract String getName();
    public abstract Player getPlayer();
    
    public boolean equals(Target target) {
        return target.getName().equals(this.getName());
    }
}