package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleTrapper extends Role {
    public RoleTrapper() {
        this.name = "Trapper";
        this.roleType = TRAPPER;
        this.attackValue = EnumAttack.POWERFUL;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Protective)";
        this.abilities.add("You may set up a Trap at another player's house.");
        this.attributes.add("Traps take one day to build.");
        this.attributes.add("Traps can be torn down by selecting yourself at night.");
        this.attributes.add("You may only have one Trap out at a time.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Trapper";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(MEDIUM, JANITOR, RETRIBUTIONIST, NECROMANCER, TRAPPER));
        this.consigliereResult = "Your target is waiting for a big catch. They must bea  Trapper.";
        this.summary = "You are an intelligent woodsman with a knack for traps.";
    }
}