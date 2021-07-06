package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleExecutioner extends Role {
    public RoleExecutioner(String target) {
        this.name = "Executioner";
        this.roleType = EXECUTIONER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.BASIC;
        this.alignment = "Neutral (Evil)";
        this.abilities.add("Trick the Town into lynching your target.");
        this.attributes.add("Your target is " + target);
        this.attributes.add("If your target is killed at night, you will become a Jester");
        this.goal = "Get your target lynched at any cost.";
        this.displayName = "§7Executioner";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SHERIFF, EXECUTIONER, WEREWOLF, POISONER));
        this.consigliereResult = "Your target wants someone to be lynched at any cost. They must be an Executioner.";
        this.summary = "You are an obsessed lyncher who will stop at nothing to execute your target.";
    }
}