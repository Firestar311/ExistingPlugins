package com.starmediadev.com.common.objects.abstraction;

import com.starmediadev.com.common.enums.*;

import java.util.*;


public abstract class Role implements Comparable<Role> {
    protected String name;
    protected EnumRole roleType;
    protected EnumAttack attackValue;
    protected EnumDefense defenseValue;
    protected String alignment;
    protected final ArrayList<String> abilities = new ArrayList<>();
    protected final ArrayList<String> attributes = new ArrayList<>();
    protected String goal;
    protected String sheriffResult;
    protected ArrayList<EnumRole> investigatorResults;
    protected String consigliereResult;
    protected String summary;
    protected String displayName;
    protected final List<EnumImmunity> immunities = new ArrayList<>();

    public String getName() {
        return name;
    }

    public EnumRole getType() {
        return roleType;
    }

    public EnumAttack getAttackValue() {
        return attackValue;
    }

    public EnumDefense getDefenseValue() {
        return defenseValue;
    }

    public String getAlignment() {
        return alignment;
    }

    public HashSet<String> getAbilities() {
        return new HashSet<>(abilities);
    }

    public HashSet<String> getAttributes() {
        return new HashSet<>(attributes);
    }

    public String getGoal() {
        return goal;
    }

    public String getSheriffResult() {
        return sheriffResult;
    }

    public ArrayList<EnumRole> getInvestigatorResults() {
        return new ArrayList<>(investigatorResults);
    }

    public String getConsigliereResult() {
        return consigliereResult;
    }

    public String getSummary() {
        return summary;
    }

    public String getDisplayName() {
        if (displayName == null) {
            return "NULL:" + name;
        }
        return displayName;
    }

    public boolean isImmune(EnumImmunity immunity) {
        return this.immunities.contains(immunity);
    }

    public int compareTo(Role other) {
        return name.compareTo(other.name);
    }

    protected class GoalMessage {
        public static final String TOWN = "Lynch every criminal and evildoer.";
        public static final String MAFIA = "Kill anyone that will not submit to the Mafia.";
        public static final String COVEN = "Kill all who would oppose the Coven.";
        public static final String PLAGUEBEARER = "Infect all living players and become Pestilence";
        public static final String KILL_OPPOSE = "Kill everyone who would oppose you.";
    }

    protected class SheriffResult {
        public static final String NOT_SUSPICIOUS = "Your target is not suspicious";
        public static final String MAFIA = "Your target is a member of the mafia.";
        public static final String SERIAL_KILLER = "Your target is a Serial Killer!";
        public static final String WEREWOLF = "Your target is a Werewolf.";
        public static final String COVEN = "Your target is a member of the Coven!";
        public static final String APOCALYPSE = "Your target is a member of the Apocalypse!";
    }
}