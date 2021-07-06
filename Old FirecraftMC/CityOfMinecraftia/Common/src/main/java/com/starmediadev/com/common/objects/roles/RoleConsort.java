package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleConsort extends Role {
    public RoleConsort() {
        this.name = "Consort";
        this.roleType = CONSORT;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Support)";
        this.abilities.add("Distract someone each night.");
        this.attributes.add("Distraction blocks your target from using their role's night ability.");
        this.attributes.add("If there are no kill capable Mafia roles left, you will become a Mafioso");
        this.attributes.add("You can talk with the other Mafia at night.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Consort";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(ESCORT, TRANSPORTER, CONSORT, HYPNOTIST));
        this.consigliereResult = "Your target is a beautiful person working for the Mafia. She must be a Consort.";
        this.summary = "You are a beautiful dancer working for organized crime.";
    }
}