package com.stardevmc.titangames.kits;

import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Arena.ArenaState;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class KitGiveTimer extends BukkitRunnable {
    
    private int counter = 60;
    private Arena arena;
    private List<Integer> announceTimes = Arrays.asList(60, 30, 15, 10, 5, 4, 3, 2, 1);
    
    public KitGiveTimer(Arena arena) {
        this.arena = arena;
    }
    
    public void run() {
        if (!arena.getState().equals(ArenaState.IN_GAME)) {
            cancel();
            return;
        }
        
        if (counter == 0) {
            arena.giveKits();
            arena.sendMessageToAll(Messages.KITS_GIVEN());
            cancel();
            return;
        }
        
        if (announceTimes.contains(counter)) {
            arena.sendMessageToAll(Messages.KITS_COUNTDOWN(counter));
        }
        counter--;
    }
}
