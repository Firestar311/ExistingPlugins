package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.JUGGERNAUGHT;

public class RoleJuggernaut extends Role {

    public RoleJuggernaut() {
        this.name = "Juggernaut";
        this.roleType = JUGGERNAUGHT;
        this.attackValue = EnumAttack.POWERFUL;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Killing)";
        this.abilities.add("You may choose to attack a player on full moon nights.");
        this.attributes.add("With each kill your powers grow");
        this.goal = GoalMessage.KILL_OPPOSE;
        this.displayName = "§3Juggernaut";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>();
        this.consigliereResult = "Your target gets more powerful with each kill. They must be a Juggernaut.";
        this.summary = "You are an unstoppable force that only gets stronger with each kill.";
    }
}