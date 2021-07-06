package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleJanitor extends Role {
    public RoleJanitor() {
        this.name = "Janitor";
        this.roleType = JANITOR;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Deception)";
        this.abilities.add("Choose a person to clean at night.");
        this.attributes.add("If your target dies their role and last will won't be revealed to the Town");
        this.attributes.add("Only you will see the cleaned targets role and last will.");
        this.attributes.add("You may only perform 3 cleanings");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Janitor";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(MEDIUM, JANITOR, RETRIBUTIONIST, NECROMANCER, TRAPPER));
        this.consigliereResult = "Your target cleans up dead bodies. They must be a Janitor.";
        this.summary = "You are a sanitation expert working for organized crime.";
    }
}