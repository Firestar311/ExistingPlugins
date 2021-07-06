package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleVigilante extends Role {
    public RoleVigilante() {
        this.name = "Vigilante";
        this.roleType = VIGILATE;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Killing)";
        this.abilities.add("Choose to take justice into your own hands and shoot someone.");
        this.attributes.add("If you shoot another Town member you will commit suicide over the guilt.");
        this.attributes.add("You can only shoot your gun 3 times.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Vigilante";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(VIGILATE, VETERAN, MAFIOSO, PIRATE, AMBUSHER));
        this.consigliereResult = "Your target will bend the law to enact justice. They must be a Vigilante.";
        this.summary = "You are a corrupt cop who takes the law into his own hands.";
    }
}