package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleGodfather extends Role {
    public RoleGodfather() {
        this.name = "Godfather";
        this.roleType = GODFATHER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.BASIC;
        this.alignment = "Mafia (Killing)";
        this.abilities.add("You may choose to attack a player each night");
        this.attributes.add("If there is a Mafioso he will attack the target instead of you.");
        this.attributes.add("You will appear to be a Town member to the Sheriff.");
        this.attributes.add("You can talk with the other Mafia at night.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Godfather";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(BODYGUARD, GODFATHER, ARSONIST, CRUSADER));
        this.consigliereResult = "Your target is the leader of the Mafia. They must be the Godfather.";
        this.summary = "You are the leader of organized crime.";
    }
}