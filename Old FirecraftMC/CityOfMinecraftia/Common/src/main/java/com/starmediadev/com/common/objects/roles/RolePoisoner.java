package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RolePoisoner extends Role {
    public RolePoisoner() {
        this.name = "Poisoner";
        this.roleType = POISONER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.abilities.add("You may choose to poison a player each night.");
        this.attributes.add("Your poisons take one day to take effect.");
        this.attributes.add("Poison can be removed by Heals");
        this.attributes.add("With the Necronomicon, your poison can no longer be Healed.");
        this.goal = GoalMessage.COVEN;
        this.displayName = "§5Poisoner";
        this.sheriffResult = SheriffResult.COVEN;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SHERIFF, EXECUTIONER, WEREWOLF, POISONER));
        this.consigliereResult = "Your target uses herps and plants to kill their victims. They must be the Poisoner.";
        this.summary = "You are a woman who lives in the woods and has a knowledge of poisonous plants.";
    }
}