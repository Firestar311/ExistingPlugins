package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleWerewolf extends Role {
    public RoleWerewolf() {
        this.name = "Werewolf";
        this.roleType = WEREWOLF;
        this.attackValue = EnumAttack.POWERFUL;
        this.defenseValue = EnumDefense.BASIC;
        this.alignment = "Neutral (Killing)";
        this.abilities.add("Transform into a Werewolf during the full moon.");
        this.attributes.add("You will Rampage at a player's house when you attack.");
        this.goal = GoalMessage.KILL_OPPOSE;
        this.displayName = "§9Werewolf";
        this.sheriffResult = SheriffResult.WEREWOLF;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SHERIFF, EXECUTIONER, WEREWOLF, POISONER));
        this.consigliereResult = "Your target howls at the moon. They must be a Werewolf.";
        this.summary = "You are a normal citizen who transforms during the full moon.";
    }
}