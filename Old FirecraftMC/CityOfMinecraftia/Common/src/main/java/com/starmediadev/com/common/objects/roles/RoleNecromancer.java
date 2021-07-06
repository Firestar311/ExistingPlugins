package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleNecromancer extends Role {
    public RoleNecromancer() {
        this.name = "Necromancer";
        this.roleType = NECROMANCER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Coven (Evil)";
        this.abilities.add("You may reanimate a dead player and use their ability on a player.");
        this.attributes.add("Create zombies from dead players who use their abilities on your second target.");
        this.attributes.add("Each zombie can be used once before it rots.");
        this.attributes.add("With the Necronomicon, select yourself to sommon a ghoul to Basic Attack your target.");
        this.goal = GoalMessage.COVEN;
        this.displayName = "§5Necromancer";
        this.sheriffResult = SheriffResult.COVEN;
        this.investigatorResults = new ArrayList<>(Arrays.asList(MEDIUM, JANITOR, RETRIBUTIONIST, NECROMANCER, TRAPPER));
        this.consigliereResult = "Your target uses the deceased to do their dirty work. The must me the Necromancer.";
        this.summary = "You are a failed REtribuitonist who has a grudge against the Town.";
    }
}