package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleAmnesiac extends Role {
    public RoleAmnesiac() {
        this.name = "Amnesiac";
        this.roleType = AMNESIAC;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Benign)";
        this.abilities.add("Remeber who you were by selecting a graveyard role.");
        this.attributes.add("When you choose a role it will be revealed to all th eplayers in the game.");
        this.attributes.add("You can't choose a unique Town role.");
        this.goal = "Remember who you were and complete that roles goal.";
        this.displayName = "§bAmnesiac";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(LOOKOUT, FORGER, AMNESIAC, COVEN_LEADER));
        this.consigliereResult = "Your target does not remember their role. They must be an Amnesiac.";
        this.summary = "You are a trauma patient that does not remeber who you were.";
    }
}