package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.ranks.RankInfo;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRecord implements net.firecraftmc.manialib.sql.IRecord<User> {
    
    private User user;
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "users");
        net.firecraftmc.manialib.sql.Column id = new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        net.firecraftmc.manialib.sql.Column uniqueId = new net.firecraftmc.manialib.sql.Column("uniqueId", net.firecraftmc.manialib.sql.DataType.VARCHAR, 64, false, false);
        net.firecraftmc.manialib.sql.Column name = new net.firecraftmc.manialib.sql.Column("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 32, false, false);
        net.firecraftmc.manialib.sql.Column rank = new net.firecraftmc.manialib.sql.Column("rank", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000);
        net.firecraftmc.manialib.sql.Column channel = new net.firecraftmc.manialib.sql.Column("channel", net.firecraftmc.manialib.sql.DataType.VARCHAR, 15);
        table.addColumns(id, uniqueId, name, rank, channel);
        return table;
    }
    
    public UserRecord(User user) {
        this.user = user;
    }
    
    public UserRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        UUID uniqueId = UUID.fromString(row.getString("uniqueId"));
        String name = row.getString("name");
        String rankString = row.getString("rank");

        RankInfo rankInfo = new RankInfo(uniqueId);
        String[] rawRank = rankString.split(":");
        if (rawRank == null || rawRank.length < 1) {
            rankInfo.setRank(Rank.valueOf(rankString));
        } else {
            try {
                rankInfo.setRank(Rank.valueOf(rawRank[0]));
            } catch (Exception e) {
                rankInfo.setRank(Rank.DEFAULT);
            }
            try {
                rankInfo.setExpire(Long.parseLong(rawRank[1]));
            } catch (Exception e) {
                rankInfo.setExpire(-1);
            }
            try {
                rankInfo.setPreviousRank(Rank.valueOf(rawRank[2]));
            } catch (Exception e) {
                rankInfo.setPreviousRank(Rank.DEFAULT);
            }
            try {
                rankInfo.setActor(rawRank[3]);
            } catch (Exception e) {
                rankInfo.setActor("SERVER");
            }
        }
        
        Channel channel;
        try {
            channel = Channel.valueOf(row.getString("channel"));
        } catch (Exception e) {
            channel = Channel.GLOBAL;
        }
        this.user = new User(id, uniqueId, name, rankInfo, channel);
    }
    
    public int getId() {
        return user.getId();
    }
    
    public void setId(int id) {
        user.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", user.getId());
            put("uniqueId", user.getUniqueId().toString());
            put("name", user.getName());
            String rankString = user.getRankInfo().getRank().name() + ":" + user.getRankInfo().getExpire() + ":" + user.getRankInfo().getPreviousRank().name() + ":" + user.getRankInfo().getActor();
            put("rank", rankString);
            put("channel", user.getChannel().name());
        }};
    }
    
    public User toObject() {
        return user;
    }
}
