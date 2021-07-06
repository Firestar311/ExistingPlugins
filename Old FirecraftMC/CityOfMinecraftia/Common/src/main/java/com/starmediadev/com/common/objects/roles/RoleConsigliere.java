package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleConsigliere extends Role {
    public RoleConsigliere() {
        this.name = "Consigliere";
        this.roleType = CONSIGLIERE;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Support)";
        this.abilities.add("Check one person for their exact role each night.");
        this.attributes.add("If there are no kill capable Mafia roles left you will become a Mafioso");
        this.attributes.add("You can talk with the other Mafia at night.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Consigliere";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(INVESTIGATOR, CONSIGLIERE, MAYOR, TRACKER, PLAGUEBEARER));
        this.consigliereResult = "Your target gathers information for the Mafia. They must be a Consigliere";
        this.summary = "You are a corrupted Investigator who gathers information for the Mafia.";
    }
}