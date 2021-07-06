package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleSheriff extends Role {
    public RoleSheriff() {
        this.name = "Sheriff";
        this.roleType = SHERIFF;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Investigative)";
        this.abilities.add("Interrogate one person each night for suspicious activity.");
        this.attributes.add("You will know if your target is a member of the Mafia.");
        this.attributes.add("You will know if your target is a member of the Coven.");
        this.attributes.add("You will know if your target is a Serial Killer");
        this.attributes.add("You will know if your target is a Werewolf on full moon nights.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Sheriff";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SHERIFF, EXECUTIONER, WEREWOLF, POISONER));
        this.consigliereResult = "Your target is a protector of the Town. They must be a Sheriff";
        this.summary = "The law enforcer of the Town, forced into hiding from threat of murder.";
    }
}