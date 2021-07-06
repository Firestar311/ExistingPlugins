package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RolePlaguebearer extends Role {

    public RolePlaguebearer() {
        this.name = "Plaguebearer";
        this.roleType = PLAGUEBEARER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Chaos)";
        this.abilities.add("You may vote to infect a player with the Plague each night");
        
        this.attributes.addAll(Arrays.asList("Players will not know they have been infeced.",
                "When all living players are infected you will gain Basic defense.",
                "If Pestilence dies, you will become the new Pestilence.",
                "Pestilence will choose who visits."));
        this.goal = GoalMessage.PLAGUEBEARER;
        this.displayName = "§aPlaguebearer";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(INVESTIGATOR, CONSIGLIERE, MAYOR, TRACKER, PLAGUEBEARER));
        this.consigliereResult = "Your target is a carrier of disease. They must be a Plaguebearer.";
        this.summary = "You are an acolyte of Pestilence who spreads disease among the Town.";
    }
}