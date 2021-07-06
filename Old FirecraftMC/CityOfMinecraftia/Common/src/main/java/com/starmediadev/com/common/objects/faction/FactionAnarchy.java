package com.starmediadev.com.common.objects.faction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;
import com.starmediadev.com.common.objects.abstraction.Faction;

import java.util.List;

public class FactionAnarchy extends Faction {

    public FactionAnarchy() {
        super("Anarchy", EnumFaction.ANARCHY);
    }
    
    public List<EnumRole> getRoles() {
        return EnumRole.ANARCHY_ROLES();
    }
}