package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleJester extends Role {
    public RoleJester() {
        this.name = "Jester";
        this.roleType = JESTER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Evil)";
        this.abilities.add("Trick the Town into voting against you.");
        this.attributes.add("If you are lynched you will attack one of your guilty votes the following night with an Unstoppable attack.");
        this.goal = "Get yourself lynched by any means necessary.";
        this.displayName = "§dJester";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(FRAMER, VAMPIRE, JESTER, HEX_MASTER));
        this.consigliereResult = "Your target wants to be lynched. They must be a Jester.";
        this.summary = "You are a crazed lunatic whose life goal is to be publicly executed.";
    }
}