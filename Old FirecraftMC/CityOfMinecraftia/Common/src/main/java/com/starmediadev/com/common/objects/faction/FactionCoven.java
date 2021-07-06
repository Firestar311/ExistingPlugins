package com.starmediadev.com.common.objects.faction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;
import com.starmediadev.com.common.objects.abstraction.Faction;

import java.util.List;

public class FactionCoven extends Faction {

    public FactionCoven() {
        super("Coven", EnumFaction.COVEN);
    }
    
    public List<EnumRole> getRoles() {
        return EnumRole.COVEN_ROLES();
    }
}