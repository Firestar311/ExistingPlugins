package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleMafioso extends Role {
    public RoleMafioso() {
        this.name = "Mafioso";
        this.roleType = MAFIOSO;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.abilities.add("Carry out the Godfather's orders.");
        this.attributes.add("You can attack if the Godfather doesn't give you orders.");
        this.attributes.add("If the Godfather dies you will become the next Godfather.");
        this.attributes.add("You can talk with the other Mafia at night.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Mafioso";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(VIGILATE, VETERAN, MAFIOSO, PIRATE, AMBUSHER));
        this.consigliereResult = "Your target does the Godfather's dirty work. They must be a Mafioso.";
        this.summary = "You are a member of organized crime, trying to work your way to the top.";
    }
}