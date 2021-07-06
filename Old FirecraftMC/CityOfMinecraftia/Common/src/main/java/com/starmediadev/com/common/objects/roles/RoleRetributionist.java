package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleRetributionist extends Role {
    public RoleRetributionist() {
        this.name = "Retributionist";
        this.roleType = RETRIBUTIONIST;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue= EnumDefense.NONE;
        this.alignment = "Town (Support)";
        this.abilities.add("You may revive a dead Town member.");
        this.attributes.add("You may only resurrect one person.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Retributionist";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(MEDIUM, JANITOR, RETRIBUTIONIST, NECROMANCER, TRAPPER));
        this.consigliereResult = "Your targets wields mystical powers. They must be a Retributionist.";
        this.summary = "A powerful mystic who will give one person a second chance at life.";
    }
}