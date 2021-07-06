package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleSpy extends Role {
    public RoleSpy() {
        this.name = "Spy";
        this.roleType = SPY;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Investigative)";
        this.abilities.add("You may bug a player's house to see what happens to them at night.");
        this.attributes.add("You will know who the Mafia and Coven visit at night.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Spy";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SPY, BLACKMAILER, JAILOR, GUARDIAN_ANGEL));
        this.consigliereResult = "Your target secretly watches who someone visits. They must be a Spy.";
        this.summary = "You are a talented watcher who keeps track of evil in the Town.";
    }
}