package com.starmediadev.lib.user;

import com.starmediadev.lib.builder.ScoreboardBuilder;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.SortedMap;
import java.util.TreeMap;

public class PlayerBoard {
    
    private String name;
    private Scoreboard scoreboard;
    private Objective objective;
    private SortedMap<Integer, Team> teams = new TreeMap<>();
    private int scoreIndex = 15;
    
    public PlayerBoard(String name, String title) {
        this.name = name;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = this.scoreboard.registerNewObjective(name, "dummy", Utils.color(title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public static ScoreboardBuilder start(String name) {
        return new ScoreboardBuilder(name);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int addLine(String text) {
        int position;
        if (this.teams.isEmpty()) {
            position = 0;
        } else {
            position = this.teams.lastKey() + 1;
        }
        
        String entryName = "";
        
        color:
        for (ChatColor color : ChatColor.values()) {
            for (Team team : this.teams.values()) {
                if (team.getEntries().contains(color.toString())) {
                    continue color;
                }
            }
    
            entryName = color.toString();
            break;
        }
        
        Team team = this.scoreboard.registerNewTeam(entryName);
        team.addEntry(entryName);
        team.setSuffix(Utils.color(text)); //There is no character limit as far as I know.
        objective.getScore(team.getName()).setScore(scoreIndex);
        scoreIndex--;
        this.teams.put(position, team);
        return position;
    }
    
    public void setLine(int line, String text) {
        Team team = this.teams.get(line);
        if (team == null) {
            return;
        }
        
        team.setSuffix(Utils.color(text));
    }
    
    public String getName() {
        return name;
    }
    
    public void send(Player player) {
        player.setScoreboard(this.scoreboard);
    }
}
