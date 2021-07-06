package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleCrusader extends Role {
    public RoleCrusader() {
        this.name = "Crusader";
        this.roleType = CRUSADER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.abilities.add("Protect one person during the night.");
        this.attributes.add("Grant your target Powerful defense");
        this.attributes.add("You will know if your target is attacked.");
        this.attributes.add("You will attack one person who visits your target on the same night.");
        this.attributes.add("You do not attack vampires, but you do block their attacks.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Crusader";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(BODYGUARD, GODFATHER, ARSONIST, CRUSADER));
        this.consigliereResult = "Your target is a knight protector. They must be a Crusader.";
        this.summary = "You are a divine protector whose skills in comat are only matched by the miracles you perform.";
    }
}