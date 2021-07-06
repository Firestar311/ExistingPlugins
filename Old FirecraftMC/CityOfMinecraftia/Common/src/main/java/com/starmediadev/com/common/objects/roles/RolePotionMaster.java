package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RolePotionMaster extends Role {
    public RolePotionMaster() {
        this.name = "Potion Master";
        this.roleType = POTION_MASTER;
        this.attackValue = EnumAttack.BASIC;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Coven (Evil)";
        this.abilities.add("You may choose to use a potion on a player each night.");
        this.attributes.add("You may choose to use a Heal, reveal, or attack potion on a player.");
        this.attributes.add("Each potion has a three day cooldown.");
        this.attributes.add("With the Necronomicon, your potion sno longer have a cooldown.");
        this.goal = GoalMessage.COVEN;
        this.displayName = "§5Potion Master";
        this.sheriffResult = SheriffResult.COVEN;
        this.investigatorResults = new ArrayList<>(Arrays.asList(DOCTOR, DISGUISER, SERIAL_KILLER, POTION_MASTER));
        this.consigliereResult = "Your target works with alchemy. They must be the Potion Master.";
        this.summary = "You are an experienced alchemist with potent recipes for potions.";
    }
}