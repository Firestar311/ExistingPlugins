package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleVampireHunter extends Role {
    public RoleVampireHunter() {
        this.name = "Vampire Hunter";
        this.roleType = VAMPIRE_HUNTER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Killing)";
        this.abilities.add("Check for Vampires each night.");
        this.attributes.add("If a Vampire visits you, you will attack them.");
        this.attributes.add("If all Vampires die you will become a Vigilante.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Vampire Hunter";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SURVIVOR, VAMPIRE_HUNTER, MEDUSA, PSYCHIC));
        this.consigliereResult = "Your target tracks Vampires. They must be a Vampire Hunter!";
        this.summary = "You are a priest turned monster hunter, who slays Vampires.";
    }
}