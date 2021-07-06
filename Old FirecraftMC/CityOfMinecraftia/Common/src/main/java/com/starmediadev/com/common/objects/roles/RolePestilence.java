package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RolePestilence extends Role {

    public RolePestilence() {
        this.name = "Pestilence";
        this.roleType = PESTILENCE;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.BASIC;
        this.alignment = "Neutral (Chaos)";
        
        this.abilities.addAll(Arrays.asList("Pick a Plaguebearer to infect a player.",
                "When all Town are infected you gain Powerful attack and Invincible Defense",
                "When all town are infected, choose to rampage at a player's house at night."));
        
        this.attributes.addAll(Arrays.asList("You cannot be roleblocked or controlled",
                "If you are jailed, you will attack the Jailor, if you have been summoned",
                "If you die, a Plaguebearer will become the new Pestilence."));
        this.goal = GoalMessage.KILL_OPPOSE;
        this.displayName = "§8Pestilence";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(INVESTIGATOR, CONSIGLIERE, MAYOR, TRACKER, PLAGUEBEARER));
        this.consigliereResult = "Your target reeks of disease. They must be Pestilence, Horseman of the Apocalypse.";
        this.summary = "You are a god among the Town, who obliterates people with the plague.";
    }
}