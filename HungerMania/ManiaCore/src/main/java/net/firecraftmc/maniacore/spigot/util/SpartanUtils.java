package net.firecraftmc.maniacore.spigot.util;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.user.toggle.Toggles;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpartanUtils {
    public static void sendSpartanMessage(String server, String playerName, String hack, int violation, boolean falsePositive, double tps, int ping) {
        String format = "{channelPrefix}&4({server}){falsePositive} &8[&eSpartan&8] &d{player} &cfailed {hack} (VL: {violation}) &8(&dTPS: {tps}&8) &8(&dPing: {ping}ms&8)";
        format = format.replace("{channelPrefix}", Channel.STAFF.getChatPrefix());
        format = format.replace("{server}", server);
        format = falsePositive ? format.replace("{falsePositive}", "&4(False Positive)") : format.replace("{falsePositive}", "");
        format = format.replace("{player}", playerName);
        format = format.replace("{hack}", hack);
        format = format.replace("{violation}", violation + "");
        format = format.replace("{tps}", tps + "");
        format = format.replace("{ping}", ping + "");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(Channel.STAFF.getPermission())) {
                User user = CenturionsCore.getInstance().getUserManager().getUser(p.getUniqueId());
                if (user.getToggle(Toggles.SPARTAN_NOTIFICATIONS).getAsBoolean()) {
                    p.sendMessage(CenturionsUtils.color(format));
                }
            }
        }
    }
}
