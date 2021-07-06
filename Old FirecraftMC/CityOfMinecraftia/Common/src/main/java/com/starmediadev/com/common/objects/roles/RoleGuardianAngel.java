package com.starmediadev.com.common.objects.roles;

import com.stardevmc.cityofminecraftia.enums.EnumAttack;
import com.stardevmc.cityofminecraftia.enums.EnumDefense;
import com.stardevmc.cityofminecraftia.model.abstraction.Role;

import java.util.ArrayList;
import java.util.Arrays;

import static com.stardevmc.cityofminecraftia.enums.EnumRole.*;

public class RoleGuardianAngel extends Role {
    public RoleGuardianAngel(String target) {
        this.name = "Guardian Angel";
        this.roleType = GUARDIAN_ANGEL;
        this.attackValue = EnumAttack.NONE;
        this.defenseValue = EnumDefense.NONE;
        this.abilities.add("Keep your target alive");
        this.attributes.add("Your target is {TARGET}".replace("{TARGET}", target));
        this.attributes.add("If your target is killed you will become a Survivor without any bulletproof vests.");
        this.attributes.add("Twice a game you may Heal and Purge your target. This may be done from the grave. Watching over a player ignores jail.");
        this.goal = "Keep your target alive until the end of the game.";
        this.displayName = "§fGuardian Angel";
        this.sheriffResult = SheriffResult.NOT_SUSPICIOUS;
        this.investigatorResults = new ArrayList<>(Arrays.asList(SPY, BLACKMAILER, JAILOR, GUARDIAN_ANGEL));
        this.consigliereResult = "Your target is watching over someone. They must be a Guardian Angel.";
        this.summary = "You are an Angel whose only goal is the protection of their charge.";
    }
}