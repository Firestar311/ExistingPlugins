package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleHypnotist extends Role {
    public RoleHypnotist() {
        this.name = "Hypnotist";
        this.roleType = HYPNOTIST;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Mafia (Deception)";
        this.abilities.add("You sneak into a players house at night and plant a memory.");
        this.attributes.add("A planted memory will confuse the player.");
        this.attributes.add("If there are no kill capable Mafia roles left you will become a Mafioso.");
        this.goal = GoalMessage.MAFIA;
        this.displayName = "§4Hypnotist";
        this.sheriffResult = SheriffResult.MAFIA;
        this.investigatorResults = new ArrayList<>(Arrays.asList(ESCORT, TRANSPORTER, CONSORT, HYPNOTIST));
        this.consigliereResult = "Your target is skilled at disrupting others. They must be a Hypnotist.";
        this.summary = "You are a skilled hypnotist who can alter the perception of others.";
    }
}