package com.starmediadev.com.common.objects.abstraction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;

import java.util.List;

public abstract class Faction {
    protected final EnumFaction type;
    protected final String name;
    
    protected Faction(String name, EnumFaction type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract List<EnumRole> getRoles();
}