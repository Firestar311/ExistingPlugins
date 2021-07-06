package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleSovereign extends Role {

    public RoleSovereign() {
        this.name = "Sovereign";
        this.roleType = SOVEREIGN;
        this.attackValue = EnumAttack.DIVINE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Killing)";
        this.abilities.add("Choose someone each night to obliterate after all other Town die.");
        this.attributes.add("Your defense gets higher the less Town that is left ");
        this.attributes.add("When all Town are dead, you can choose to rampage at someone's house in revenge");
        this.attributes.add("This attack can kill a Pestilence");
        this.attributes.add("You cannot be resurrected by a Retributionist");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Sovereign";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        //TODO TEMPORARY RESULTS, WILL BE CHANGED
        this.investigatorResults = new ArrayList<>(Arrays.asList(BODYGUARD, GODFATHER, ARSONIST, CRUSADER, SOVEREIGN));
        this.consigliereResult = "Your target is a divine entity, they must be the Sovereign";
        this.summary = "You are a divine entity who will see revenge after all Town die.";
    }
}
