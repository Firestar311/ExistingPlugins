package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleSerialKiller extends Role {
    public RoleSerialKiller() {
        this.name = "Serial Killer";
        this.roleType = SERIAL_KILLER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.BASIC;
        this.alignment = "Neutral (Killing)";
        this.abilities.add("You may choose to attack a player each night.");
        this.attributes.add("If you are role blocked you will attack the role blocker instead of your target.");
        this.goal = "Kill everyone who would oppose you.";
        this.displayName = "§1Serial Killer";
        this.sheriffResult = SheriffResult.SERIAL_KILLER;
        this.investigatorResults = new ArrayList<>(Arrays.asList(DOCTOR, DISGUISER, SERIAL_KILLER, POTION_MASTER));
        this.consigliereResult = "Your target wants to kill everyone. They must be a Serial Killer.";
        this.summary = "You are a spychotic criminal who wants everyone to die.";
    }
}