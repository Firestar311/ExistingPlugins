package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleFramer extends Role {
    public RoleFramer() {
        this.name = "Framer";
        this.roleType = FRAMER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Deception)";
        this.abilities.add("Choose someone to frame at night.");
        this.attributes.add("If your target is investigated they will appear to be a member of the Mafia.");
        this.attributes.add("If there are no kill capable Mafia roles left you will become a Mafioso.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Framer";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(FRAMER, VAMPIRE, JESTER, HEX_MASTER));
        this.consigliereResult = "Your target has a desire to deceive. They must be a Framer";
        this.summary = "You are a skilled counterfeiter who manipulates information.";
    }
}