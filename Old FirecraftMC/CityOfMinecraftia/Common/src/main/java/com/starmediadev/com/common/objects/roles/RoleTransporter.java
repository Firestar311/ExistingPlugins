package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleTransporter extends Role {
    public RoleTransporter() {
        this.name = "Transporter";
        this.roleType = TRANSPORTER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Support)";
        this.abilities.add("Choose two people to transport at night");
        this.attributes.add("Transporting two people swaps all targets against them.");
        this.attributes.add("You may transport yourself");
        this.attributes.add("Your targets will know that they were transported.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Transporter";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(ESCORT, TRANSPORTER, CONSORT, HYPNOTIST));
        this.consigliereResult = "Your target specializes in transportation. They must be a Transporter";
        this.summary = "You transport people without asking any questions.";
    }
}