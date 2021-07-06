package com.stardevmc.titangames.arenas;

import com.stardevmc.titangames.arenas.Arena.ArenaState;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Countdown extends BukkitRunnable {
    
    private Arena arena;
    private int count;
    private List<Integer> announceNumbers = new ArrayList<>();
    
    public Countdown(Arena arena, int start, int... announceNumbers) {
        this.arena = arena;
        this.count = start;
        for (int aN : announceNumbers) {
            this.announceNumbers.add(aN);
        }
    }
    
    public void run() {
        if (arena.getState() != ArenaState.COUNTDOWN) {
            cancel();
            return;
        }
        
        if (announceNumbers.contains(count)) {
            arena.sendMessageToTributes(Messages.GAME_STARTING(count));
        }
        
        if (count == 0) {
            arena.start();
            cancel();
        }
        
        count--;
    }
}
