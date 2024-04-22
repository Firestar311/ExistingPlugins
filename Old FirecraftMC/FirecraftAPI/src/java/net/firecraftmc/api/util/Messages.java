package net.firecraftmc.api.util;

import net.firecraftmc.api.enums.Channel;
import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.Report;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.model.server.Warp;
import net.firecraftmc.api.punishments.Punishment;

/**
 * All of the messages used between the Core, Socket and Shared
 */
public final class Messages {
    private Messages() {
    }
    /*
    Error color: RED (&c)
    None-Error Color: AQUA (&b)
    None-Error Value Color: GOLD (&e)
     */

    public static final String ERROR_COLOR = "&c";
    public static final String NORMAL_COLOR = "&7";
    public static final String VALUE_COLOR = "&e";

    //Action Bars
    public static final String actionBar_Nicked = "&fYou are currently &4NICKED";
    public static final String actionBar_Vanished = "&fYou are currently &9VANISHED";
    public static final String actionBar_Recording = "&fYou are currently &eRECORDING";
    public static final String actionBar_Staffmode = "&fYou are currently in &aSTAFF MODE";
    public static final String actionBar_Incognito = "&fYou are currently &5INCOGNITO";

    public static String warnMessage(String punisher, String reason, String code) {
        return "<ec>You have been warned by {punisher} for {reason}. You must acknowledge this warning before you can speak again.\nType the code <nc>{code} <ec>in chat.".replace("{punisher}", punisher).replace("{reason}", reason).replace("{code}", code);
    }

    public static String reportBcFormat(FirecraftServer server, int id, String reporter, String target, String reason) {
        return "&4[REPORT] <ec><ID:{id}> &a({serverid}) &d{reporter} &freported &d{target} &ffor &d{reason}".replace("{serverid}", server.getName().toUpperCase())
                .replace("{reporter}", reporter).replace("{target}", target).replace("{reason}", reason).replace("{id}", id + "");
    }

    public static String formatReportChange(Report report, String changed) {
        String[] changedArr = changed.split(" ");
        if (changedArr.length == 2) {
            if (changedArr[0].equalsIgnoreCase("assigned")) {
                return "<nc>Your report against <vc>" + report.getTargetName() + " <nc>has been assigned to <vc>" + changedArr[1];
            }
        } else if (changedArr.length == 3) {
            String base = "<nc>The <vc>{so} <nc>of your report against <vc>{target} <nc>has been changed to {value}".replace("{target}", report.getTargetName());
            if (changedArr[0].equalsIgnoreCase("changed")) {
                if (changedArr[1].equalsIgnoreCase("status")) {
                    base = base.replace("{so}", "status");
                    Report.Status value = Report.Status.valueOf(changedArr[2]);
                    base = base.replace("{value}", value.getColor() + value.toString());
                } else if (changedArr[1].equalsIgnoreCase("outcome")) {
                    base = base.replace("{so}", "outcome");
                    Report.Outcome value = Report.Outcome.valueOf(changedArr[2]);
                    base = base.replace("{value}", value.getColor() + value.toString());
                }
                return base;
            }
        }
        return "";
    }

    public static String staffReportLogin(int notClosed, int notAssigned, int assignedNotClosed) {
        return "<nc>There are a total of <vc>" + notClosed + " <nc>reports that are not closed.\n" +
        "<nc>There are a total of <vc>" + notAssigned + " <nc>reports that are not assigned.\n" +
        "<nc>There are a total of <vc>" + assignedNotClosed + " <nc>reports that are assigned to you and not closed.";
    }
}
