package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleVeteran extends Role {
    public RoleVeteran() {
        this.name = "Veteran";
        this.roleType = VETERAN;
        this.attackValue = EnumAttack.POWERFUL;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Killing)";
        this.abilities.add("Decide if you will go an alert.");
        this.attributes.add("While on alert you gain Basic Defense.");
        this.attributes.add("While on alert you attack anyone who visits you.");
        this.attributes.add("You can only go on alert 3 times.");
        this.attributes.add("You cannot be role blocked.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Veteran";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(VIGILATE, VETERAN, MAFIOSO, PIRATE, AMBUSHER));
        this.consigliereResult = "Your target is a paranoid war hero. They must be a Veteran.";
        this.summary = "A paranoid war hero who will shoot anyone who visits him.";
    }
}