package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleAmbusher extends Role {
    public RoleAmbusher() {
        this.name = "Ambusher";
        this.roleType = AMBUSHER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Killing)";
        this.abilities.add("You may choose to lie in wait outside your targets house.");
        this.attributes.add("You will attack one player who visits your target.");
        this.attributes.add("All players visiting your target will learn your name.");
        this.attributes.add("If there are no kill capable Mafia roles left you will become a Mafioso");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Ambusher";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(VIGILATE, VETERAN, MAFIOSO, PIRATE, AMBUSHER));
        this.consigliereResult = "Your target lies in wait. They must be an Ambusher.";
        this.summary = "You are a stealthy killer who lies in wait for the perfect moment to strike.";
    }
}