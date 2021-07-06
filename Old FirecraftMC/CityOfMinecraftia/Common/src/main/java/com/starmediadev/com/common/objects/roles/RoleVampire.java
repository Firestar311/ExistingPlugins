package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleVampire extends Role {

    public RoleVampire() {
        this.name = "Vampire";
        this.roleType = VAMPIRE;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Chaos)";
        this.abilities.add("Convert others to Vampires at night.");
        this.attributes.add("Vampires vote at night to bite a target.");
        this.attributes.add("The youngest Vampire will visit the target at night.");
        this.goal = "Convert everyone who would oppose you.";
        this.displayName = "§cVampire";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(FRAMER, VAMPIRE, JESTER, HEX_MASTER));
        this.consigliereResult = "Your target drinks blood. They must be a Vampire!";
        this.summary = "You are among the dead who turns others at night.";
    }
}