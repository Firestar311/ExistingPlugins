package com.starmediadev.com.common.enums;

import com.google.common.collect.Lists;
import com.starmediadev.com.common.objects.abstraction.Role;
import com.starmediadev.com.common.objects.roles.*;
import com.starmediadev.com.common.util.Goal;

import java.util.*;

import static com.starmediadev.com.common.enums.EnumAlignment.*;
import static com.starmediadev.com.common.enums.EnumAttack.*;
import static com.starmediadev.com.common.enums.EnumDefense.NONE;
import static com.starmediadev.com.common.enums.EnumFaction.TOWN;
import static com.starmediadev.com.common.enums.SheriffResult.INNOCENT;

public enum EnumRole {
    
    //TOWN ROLES
    BODYGUARD(RoleBodyguard.class, false, 7, TOWN, POWERFUL, NONE, PROTECTIVE, Goal.TOWN, "Your target is a trained protector, they must be a Bodyguard.",
            INNOCENT, Collections.singletonList("Protect a player from direct attacks at night"), Arrays.asList("If your target is directly attacked or is the victim of a harmful visit, you and the visitor will fight.", "If you successfully protect someone, you can still be Healed."),
            new ArrayList<>(), "You are an ex-soldier who secretly makes a living by selling protection."),
    DOCTOR(RoleDoctor.class, false, 7, TOWN, EnumAttack.NONE, NONE, PROTECTIVE, Goal.TOWN, "Your target is a professional surgeon, they must be a Doctor.",
            INNOCENT, Collections.singletonList("Heal one person each night, preventing them from dying"),
            Arrays.asList("You may only Heal yourself once.", "You will know if your target is attacked."),
            new ArrayList<>(), "You are a surgeon skilled in trauma care who secretly heals people."),
    ESCORT(RoleEscort.class, false, 7, TOWN, EnumAttack.NONE, NONE, SUPPORT, Goal.TOWN,
            "Your target is a beautiful person working for the Town. They must be an Escort.",
            INNOCENT, Collections.singletonList("Distract someone each night"),
            Arrays.asList("Distraction blocks your target from using their role's night ability", "You cannot be role blocked"),
            Collections.singletonList(EnumImmunity.ROLE_BLOCK),
            "You are a beautiful woman skiled in distraction."),
    INVESTIGATOR(RoleInvestigator.class, false, 7, TOWN, EnumAttack.NONE, NONE, INVESTIGATIVE, Goal.TOWN,
            "Your target gathers information about people. They must be an Investigato",
            INNOCENT, Collections.singletonList("Investigate one person each night for a clue to their role."), Collections.singletonList("None"),
            new ArrayList<>(),
            "You are a private eye who secretly gathers information about people."),
    JAILOR(RoleJailor.class, true, 7, TOWN, UNSTOPPABLE, NONE, KILLING, Goal.TOWN,
            "Your target detains people at night. They must be the Jailor.",
            INNOCENT, Collections.singletonList("You may choose one person during the day to jail for the night."),
            Arrays.asList("You may anonymously talk with your prisoner.", "You can choose to attack your prisoner", "The jailed target can't perform their night ability.",
                    "While jailed the prisoner is given Powerful defense"),
            new ArrayList<>(), "You are a prison guard who secretly detains suspects."),
    LOOKOUT(RoleLookout.class, false, 7, TOWN, EnumAttack.NONE, NONE, INVESTIGATIVE, Goal.TOWN,
            "Your target watches who visits people at night. They must be a Lookout.",
            INNOCENT, Collections.singletonList("Watch one person at night to see who visits them."),
            Collections.singletonList("None"),
            new ArrayList<>(), "You are an eagle-eyed observer, stealthily camping outside houses to gain information."),
    MAYOR(RoleMayor.class, false, 7, TOWN, EnumAttack.NONE, NONE, SUPPORT, Goal.TOWN,
            "Your target is the leader of the Town. They must be the Mayor.",
            INNOCENT, Collections.singletonList("You may reveal yourself as the Mayor of the Town"),
            Arrays.asList("Once you have revealed yourself as Mayor your vote counts as 3 votes.", "You may not be Healed once you have revealed yourself.",
                    "Once revealed you can't whisper, or be whispered to."),
            new ArrayList<>(),
            "You are the leader of the Town."),
    MEDIUM(RoleMedium.class, false, 7, TOWN, EnumAttack.NONE, NONE, SUPPORT, Goal.TOWN,
            "Your target speaks with the dead. They must be a Medium.",
            INNOCENT, Collections.singletonList("When dead speak to a living person at night."),
            Arrays.asList("You will speak to the dead anonymously at night.", "You may only speak to a living person when dead."),
            new ArrayList<>(),
            "You are a secret Psychic who talks with the dead."),
    RETRIBUTIONIST(true, 7),
    SHERIFF(false, 7),
    SPY(false, 7),
    TRANSPORTER(false, 7),
    VAMPIRE_HUNTER(false, 7),
    VETERAN(true, 7),
    VIGILATE(false, 7),
    CRUSADER(false, 7),
    PSYCHIC(false, 7),
    TRAPPER(false, 7),
    TRACKER(false, 7),
    SOVEREIGN(true, 1),
    
    //MAFIA ROLES
    BLACKMAILER(false, 7), CONSIGLIERE(false, 7), CONSORT(false, 7), DISGUISER(false, 7), FORGER(false, 7), FRAMER(false, 7),
    GODFATHER(true, 3), JANITOR(false, 7), MAFIOSO(true, 7), HYPNOTIST(false, 7), AMBUSHER(true, 7),
    
    //COVEN ROLES
    COVEN_LEADER(true, 7), POTION_MASTER(true, 7), HEX_MASTER(true, 7), NECROMANCER(true, 7), POISONER(true, 7), MEDUSA(true, 7),
    
    //NEUTRAL ROLES
    AMNESIAC(false, 7), EXECUTIONER(false, 7), SURVIVOR(false, 7), JESTER(false, 7), GUARDIAN_ANGEL(true, 7), PIRATE(true, 7),
    
    //ANARCHY ROLES
    //TODO Change the faction class to a more tailored setup
    ARSONIST(false, 7), SERIAL_KILLER(false, 7), JUGGERNAUGHT(true, 3),
    
    //MYTHICAL ROLES
    //TODO Change the faction class to a more tailored setup
    VAMPIRE(false, 7), WEREWOLF(true, 7),
    
    //APOCALYPSE ROLES
    //TODO Change the faction class to a more tailored setup
    PLAGUEBEARER(true, 4), PESTILENCE(true, 0);
    
    
    Class<? extends Role> clazz;
    boolean unique;
    int chance;
    EnumFaction faction;
    EnumAttack attack;
    EnumDefense defenseValue;
    EnumAlignment alignment;
    Goal goal;
    String directResult;
    SheriffResult sheriffResult;
    List<String> abilities = new ArrayList<>();
    List<String> attributes = new ArrayList<>();
    List<EnumImmunity> immunities = new ArrayList<>();
    String summary;
    
    EnumRole(Class<? extends Role> clazz, boolean unique, int chance, EnumFaction faction, EnumAttack attack, EnumDefense defenseValue, EnumAlignment alignment, Goal goal, String directResult, SheriffResult sheriffResult, List<String> abilities, List<String> attributes, List<EnumImmunity> immunities, String summary) {
        this.clazz = clazz;
        this.unique = unique;
        this.chance = chance;
        this.faction = faction;
        this.attack = attack;
        this.defenseValue = defenseValue;
        this.alignment = alignment;
        this.goal = goal;
        this.directResult = directResult;
        this.sheriffResult = sheriffResult;
        this.abilities = abilities;
        this.attributes = attributes;
        this.immunities = immunities;
        this.summary = summary;
    }
    
    EnumRole(boolean unique, int chance) {
        this.unique = unique;
        this.chance = chance;
    }

    public static List<EnumRole> TOWN_ROLES() {
        return Lists.newArrayList(BODYGUARD, DOCTOR, ESCORT, INVESTIGATOR, JAILOR, LOOKOUT, MAYOR, MEDIUM, RETRIBUTIONIST, SHERIFF, SPY, TRANSPORTER, VAMPIRE_HUNTER, VETERAN, VIGILATE, CRUSADER, PSYCHIC, TRAPPER, TRACKER, SOVEREIGN);
    }

    public static List<EnumRole> MAFIA_ROLES() {
        return Lists.newArrayList(BLACKMAILER, CONSIGLIERE, CONSORT, DISGUISER, FORGER, FRAMER, GODFATHER, JANITOR, MAFIOSO, HYPNOTIST, AMBUSHER);
    }

    public static List<EnumRole> COVEN_ROLES() {
        return Lists.newArrayList(COVEN_LEADER, POTION_MASTER, HEX_MASTER, NECROMANCER, POISONER, MEDUSA);
    }

    public static List<EnumRole> NEUTRAL_ROLES() {
        return Lists.newArrayList(AMNESIAC, EXECUTIONER, SURVIVOR, JESTER, GUARDIAN_ANGEL, PIRATE);
    }

    public static List<EnumRole> ANARCHY_ROLES() {
        return Lists.newArrayList(ARSONIST, SERIAL_KILLER, JUGGERNAUGHT);
    }

    public static List<EnumRole> MYTHICAL_ROLES() {
        return Lists.newArrayList(VAMPIRE, WEREWOLF);
    }
    
    public static List<EnumRole> APOCALYPSE_ROLES() {
        return Lists.newArrayList(PLAGUEBEARER, PESTILENCE);
    }
    
    public static boolean canKill(EnumAttack attack, EnumDefense defense) {
        if (attack.equals(EnumAttack.NONE)) { return false; }
        if (attack.equals(DIVINE)) { return true; }
        if (attack.equals(BASIC)) {
            if (defense.equals(NONE)) {
                return true;
            }
        }
        if (attack.equals(POWERFUL)) {
            if (defense.equals(NONE) || defense.equals(EnumDefense.BASIC)) {
                return true;
            }
        }
        
        if (attack.equals(UNSTOPPABLE)) {
            return defense.equals(NONE) || defense.equals(EnumDefense.BASIC) || defense.equals(EnumDefense.POWERFUL);
        }
        return false;
    }
    
    public boolean isUnique() {
        return unique;
    }
    
    public int getChance() {
        return chance;
    }
}