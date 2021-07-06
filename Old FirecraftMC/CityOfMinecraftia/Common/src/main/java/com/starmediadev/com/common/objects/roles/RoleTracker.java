package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleTracker extends Role {
    public RoleTracker() {
        this.name = "Tracker";
        this.roleType = TRACKER;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Investigative)";
        this.abilities.add("Track one person at night to see who they visit.");
        this.attributes.add("None");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Tracker";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(INVESTIGATOR, CONSIGLIERE, MAYOR, TRACKER, PLAGUEBEARER));
        this.consigliereResult = "Your target is skilled in the art of tracking. They must be a Tracker.";
        this.summary = "You are a skilled tracker who will follow their prey to any destination.";
    }
}