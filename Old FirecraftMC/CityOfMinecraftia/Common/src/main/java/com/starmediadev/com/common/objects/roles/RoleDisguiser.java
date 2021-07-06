package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleDisguiser extends Role {
    public RoleDisguiser() {
        this.name = "Disguiser";
        this.roleType = DISGUISER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Deception)";
        this.abilities.add("Choose a target to disguise yourself as.");
        this.attributes.add("You will appear to be the role of target to the Investigator");
        this.attributes.add("If you are killed you will appear to be therole of your target.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Disguiser";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(DOCTOR, DISGUISER, SERIAL_KILLER, POTION_MASTER));
        this.consigliereResult = "Your target pretends to be other people. They must be a Disguiser";
        this.summary = "You are a master of disguise who pretends to be other roles.";
    }
}
