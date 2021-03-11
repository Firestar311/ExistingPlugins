package net.firecraftmc.hungergames.chat;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.maniacore.api.chat.ChatFormatter;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.user.User;

public class HGChatFormatter extends ChatFormatter {

    public static final String GAME_CHANNEL_FORMAT = "{gamechannel}";
    public static final String SCORE_FORMAT = "&8<&3{score}&8>";

    public HGChatFormatter() {
        super(LEVEL_FORMAT + " " + GAME_CHANNEL_FORMAT + SCORE_FORMAT + " " + PLAYER_NAME_FORMAT + "&8: &r" + MESSAGE_FORMAT);
    }

    public String format(User user, String message) {
        String format = super.format(user, message);
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        int score = user.getStat(Stats.HG_SCORE).getAsInt();
        System.out.println(user);
        System.out.println(user.getNickname());
        if (user.getNickname().isActive()) {
            Statistic fakedStat = user.getFakedStat(Stats.HG_SCORE);
            if (fakedStat != null) {
                score = fakedStat.getAsInt();
            } else {
                score = 100;
            }
        }
        format = format.replace("{score}", score + "");
        if (game != null) {
            if (game.getSpectatorsTeam().isMember(user.getUniqueId()) || game.getHiddenStaffTeam().isMember(user.getUniqueId())) {
                format = format.replace("{gamechannel}", "&8[&cSpectators&8] ");
            } else {
                format = format.replace("{gamechannel}", "");
            }
        } else {
            format = format.replace("{gamechannel}", "");
        }
        return format;
    }
}
