package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RolePirate extends Role {
    public RolePirate() {
        this.name = "Pirate";
        this.roleType = PIRATE;
        this.attackValue = EnumAttack.POWERFUL;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Neutral (Chaos)";
        this.abilities.add("Choose a player to plunder each night.");
        this.attributes.add("When you plunder a player, you will duel the player for their valuables.");
        this.attributes.add("If the player defends against your attack, you get no loot.");
        this.goal = "Sucessfully plunder two players.";
        this.displayName = "§ePirate";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(VIGILATE, VETERAN, MAFIOSO, PIRATE, AMBUSHER));
        this.consigliereResult = "Your target wants to plunder the Town. They must be a Pirate.";
        this.summary = "You are a swashbuckler with an obsession of gold.";
    }
}