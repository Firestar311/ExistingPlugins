package com.kingrealms.realms.loot;

import com.starmediadev.lib.collection.IncrementalMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LootTable {
    private int id, minimumRolls, maximumRolls;
    private String name;
    private IncrementalMap<Loot> possibleLoot = new IncrementalMap<>();
    
    public LootTable(String name, int minimumRolls, int maximumRolls) {
        this.minimumRolls = minimumRolls;
        this.maximumRolls = maximumRolls;
        this.name = name;
    }
    
    public void addPossibleLoot(Loot loot) {
        int pos = possibleLoot.add(loot);
        loot.setId(pos);
    }
    
    public List<Loot> generateLoot() {
        List<Loot> loot = new ArrayList<>();
    
        List<Loot> chances = new LinkedList<>();
        int totalPossibleLoot = 1000;
        for (Loot possible : this.possibleLoot.values()) {
            int amount = (int) (totalPossibleLoot * possible.getRarity().getPercent());
            for (int i = 0; i < amount; i++) {
                chances.add(possible);
            }
        }
        
        int remaining = totalPossibleLoot - chances.size();
        if (remaining > 0) {
            for (int i = 0; i < remaining; i++) {
                chances.add(new Loot(new ItemStack(Material.AIR), Rarity.COMMON));
            }
        }
    
        Random random = new Random();
        int rolls = random.nextInt(maximumRolls - minimumRolls) + minimumRolls;
        Collections.shuffle(chances);
        for (int r = 0; r < rolls; r++) {
            loot.add(chances.get(random.nextInt(chances.size())));
            Collections.shuffle(chances);
        }
        
        return loot;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getMinimumRolls() {
        return minimumRolls;
    }
    
    public void setMinimumRolls(int minimumRolls) {
        this.minimumRolls = minimumRolls;
    }
    
    public int getMaximumRolls() {
        return maximumRolls;
    }
    
    public void setMaximumRolls(int maximumRolls) {
        this.maximumRolls = maximumRolls;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public IncrementalMap<Loot> getPossibleLoot() {
        return possibleLoot;
    }
}