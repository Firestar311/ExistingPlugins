package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleArsonist extends Role {
    public RoleArsonist() {
        this.name = "Arsonist";
        this.roleType = ARSONIST;
        this.attackValue = EnumAttack.UNSTOPPABLE;
        this.defenseValue = EnumDefense.BASIC;
        this.alignment = "Neutral (Killing)";
        this.abilities.add("You may Douse someone in gasoline or ignite Doused targets.");
        this.attributes.add("Select yourself to ignite doused people.");
        this.attributes.add("If you take no action, you will attempt to clean gasoline off yourself.");
        this.goal = "Live to see everyone burn.";
        this.displayName = "§6Arsonist";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(BODYGUARD, GODFATHER, ARSONIST, CRUSADER));
        this.consigliereResult = "Your target likes to watch things burn. They must be an Arsonist.";
        this.summary = "You are a pyromaniac that wants to burn everyone.";
    }
}