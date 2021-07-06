package com.starmediadev.com.common.objects.faction;

import com.starmediadev.com.common.enums.EnumFaction;
import com.starmediadev.com.common.enums.EnumRole;
import com.starmediadev.com.common.objects.abstraction.Faction;

import java.util.List;

public class FactionMafia extends Faction {

    public FactionMafia() {
        super("Mafia", EnumFaction.MAFIA);
    }
    
    public List<EnumRole> getRoles() {
        return EnumRole.MAFIA_ROLES();
    }
}