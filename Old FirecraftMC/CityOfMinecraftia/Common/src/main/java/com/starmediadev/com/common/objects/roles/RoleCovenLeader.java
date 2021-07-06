package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleCovenLeader extends Role {
    public RoleCovenLeader() {
        this.name = "Coven Leader";
        this.roleType = COVEN_LEADER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Coven (Evil)";
        this.abilities.add("You may choose to Control someone each night.");
        this.attributes.add("Your victim will know they are being controlled.");
        this.attributes.add("With the Necronomicon, your victim is dealt a Basic attack and you gain Basic defense.");
        this.attributes.add("You will know the player you control.");
        this.goal = GoalMessage.COVEN;
        this.displayName = "§5Coven Leader";
        this.sheriffResult = SheriffResult.COVEN;
        this.investigatorResults = new ArrayList<>(Arrays.asList(LOOKOUT, FORGER, AMNESIAC, COVEN_LEADER));
        this.consigliereResult = "Your target leads the mystical. They must be the Coven Leader.";
        this.summary = "You are a voodoo master who can control the actions of others.";
    }
}