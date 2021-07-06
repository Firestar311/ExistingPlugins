package com.starmediadev.com.common.util;

import com.starmediadev.com.common.enums.EnumRole;

import java.util.*;

import static com.starmediadev.com.common.enums.EnumRole.*;

public class InvestigatorGroups {
    public static final EnumSet<EnumRole> GROUP_ONE = EnumSet.of(SPY, BLACKMAILER, JAILOR, GUARDIAN_ANGEL);
    public static final EnumSet<EnumRole> GROUP_TWO = EnumSet.of(LOOKOUT, FORGER, AMNESIAC, COVEN_LEADER);
    public static final EnumSet<EnumRole> GROUP_THREE = EnumSet.of(MEDIUM, JANITOR, RETRIBUTIONIST, NECROMANCER, TRAPPER);
}