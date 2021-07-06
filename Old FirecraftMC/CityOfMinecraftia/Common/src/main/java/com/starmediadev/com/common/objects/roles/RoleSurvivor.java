package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleSurvivor extends Role {
    public RoleSurvivor() {
        this.name = "Survivor";
        this.roleType = SURVIVOR;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Benign)";
        this.abilities.add("Put on a bulletproof vest.");
        this.attributes.add("Putting on a bulletproof vest gives you Basic defense.");
        this.attributes.add("You can only use the bulletproof vest 4 times.");
        this.goal = "Live until the end of the game.";
        this.displayName = "§eSurvivor";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SURVIVOR, VAMPIRE_HUNTER, MEDUSA, PSYCHIC));
        this.consigliereResult = "Your target simply wants to live. They must be a Survivor.";
        this.summary = "You are a neutral character who just wants to live.";
    }
}