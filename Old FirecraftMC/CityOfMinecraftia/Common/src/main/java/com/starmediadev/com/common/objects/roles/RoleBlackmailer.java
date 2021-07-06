package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleBlackmailer extends Role {
    public RoleBlackmailer() {
        this.name = "Blackmailer";
        this.roleType = BLACKMAILER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Support)";
        this.abilities.add("Choose one person each night to blackmail.");
        this.attributes.add("Blackmailed targets cannot talk during the day.");
        this.attributes.add("You can hear private messages");
        this.attributes.add("If there are no kill capable Mafia left you will become a Mafioso");
        this.attributes.add("You can talk with the other Mafia members at night.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Blackmailer";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SPY, BLACKMAILER, JAILOR, GUARDIAN_ANGEL));
        this.consigliereResult = "Your target uses information to silence people. They must be a Blackmailer.";
        this.summary = "You are an eavesdraopper who uses information to keep people quiet";
    }
}