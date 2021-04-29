package net.firecraftmc.maniacore.api.ranks;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.redis.RedisListener;
import net.firecraftmc.maniacore.api.user.User;

import java.util.UUID;

public class RankRedisListener implements RedisListener {
    public void onCommand(String cmd, String[] args) {
        if (cmd.equalsIgnoreCase("rankUpdate")) {
            UUID uuid = UUID.fromString(args[0]);
            Rank rank = Rank.valueOf(args[1]);
            User user = CenturionsCore.getInstance().getUserManager().getUser(uuid);
            if (user.getRank() != rank) {
                user.setRank(rank);
            }
        }
    }
}