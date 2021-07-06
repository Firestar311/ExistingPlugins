package com.starmediadev.com.common.objects.faction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;
import com.starmediadev.com.common.objects.abstraction.Faction;

import java.util.List;

public class FactionApocalypse extends Faction {

    public FactionApocalypse() {
        super("Apocalypse", EnumFaction.APOCALYPSE);
    }
    
    public List<EnumRole> getRoles() {
        return EnumRole.APOCALYPSE_ROLES();
    }
}