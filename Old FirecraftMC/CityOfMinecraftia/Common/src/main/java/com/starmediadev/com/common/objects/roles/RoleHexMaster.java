package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleHexMaster extends Role {
    public RoleHexMaster() {
        this.name = "Hex Master";
        this.roleType = HEX_MASTER;
        this.attackValue = EnumAttack.UNSTOPPABLE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Coven (Evil)";
        this.abilities.add("You may choose to Hex a player each night.");
        this.attributes.add("With the Necronomicon you gain Astral and Basic Attacks.");
        this.attributes.add("Players are still Hexed when you have the Necronomicon.");
        this.goal = GoalMessage.COVEN;
        this.displayName = "§5Hex Master";
        this.sheriffResult = SheriffResult.COVEN;
        this.investigatorResults = new ArrayList<>(Arrays.asList(FRAMER, VAMPIRE, JESTER, HEX_MASTER));
        this.consigliereResult = "Your target is versed in the ways of hexes. They must be the Hex Master.";
        this.summary = "You are a spell slinger with a proficiency in hexes";
    }
}