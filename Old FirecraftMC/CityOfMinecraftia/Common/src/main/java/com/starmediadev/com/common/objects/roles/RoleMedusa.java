package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleMedusa extends Role {
    public RoleMedusa() {
        this.name = "Medusa";
        this.roleType = MEDUSA;
        this.attackValue = EnumAttack.POWERFUL;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Coven (Evil)";
        this.abilities.add("You may choose to Stone Gaze all visitors at night.");
        this.attributes.add("You may choose to stone gaze thrice.");
        this.attributes.add("With the Necronomicon, you may visit players and turn them to stone.");
        this.goal = GoalMessage.COVEN;
        this.displayName = "§5Medusa";
        this.sheriffResult = SheriffResult.COVEN;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SURVIVOR, VAMPIRE_HUNTER, MEDUSA, PSYCHIC));
        this.consigliereResult = "Your target has a gaze of stone. They must be a Medusa.";
        this.summary = "You are a snake haired monster gifted with a gaze that turns people to stone leaving no trance of who they were.";
    }
}