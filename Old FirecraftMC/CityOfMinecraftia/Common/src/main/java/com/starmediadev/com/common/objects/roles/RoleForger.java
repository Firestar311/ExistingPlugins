package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleForger extends Role {
    public RoleForger() {
        this.name = "Forger";
        this.roleType = FORGER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Deception)";
        this.abilities.add("Choose a person and rewrite their last will at night.");
        this.attributes.add("If your target dies their last will is replaced with your forgery.");
        this.attributes.add("You may perform 3 forgeries");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Forger";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(LOOKOUT, FORGER, AMNESIAC, COVEN_LEADER));
        this.consigliereResult = "Your target is good at forging documents. They must be a Forger.";
        this.summary = "You are a crooked lawyer that replaces documents.";
    }
}