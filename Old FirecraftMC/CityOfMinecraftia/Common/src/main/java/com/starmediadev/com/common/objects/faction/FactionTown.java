package com.starmediadev.com.common.objects.faction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;
import com.starmediadev.com.common.objects.abstraction.Faction;

import java.util.List;

public class FactionTown extends Faction {

    public FactionTown() {
        super("Town", EnumFaction.TOWN);
    }
    
    public List<EnumRole> getRoles() {
        return EnumRole.TOWN_ROLES();
    }
}