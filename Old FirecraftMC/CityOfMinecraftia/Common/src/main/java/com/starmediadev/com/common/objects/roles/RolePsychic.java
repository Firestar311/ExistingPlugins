package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RolePsychic extends Role {
    public RolePsychic() {
        this.name = "Psychic";
        this.roleType = PSYCHIC;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.alignment = "Town (Investigative)";
        this.abilities.add("Receive a vision every night.");
        this.attributes.add("On odd nights youwill hvae a vision of three players, at least one will be Evil.");
        this.attributes.add("On even nights youwill hvae a vision of two players, at least one will be Good.");
        this.goal = GoalMessage.TOWN;
        this.displayName = "§2Psychic";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SURVIVOR, VAMPIRE_HUNTER, MEDUSA, PSYCHIC));
        this.consigliereResult = "Youre target has the sight. They must be a Psychic.";
        this.summary = "You are a powerful seer with a gift for finding one's secrets.";
    }
}