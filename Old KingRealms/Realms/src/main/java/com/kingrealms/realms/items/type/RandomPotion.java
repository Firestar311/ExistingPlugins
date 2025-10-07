package com.kingrealms.realms.items.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import java.util.*;

import static org.bukkit.potion.PotionType.*;

public class RandomPotion extends Resource {
    
    private static final Set<PotionType> ALLOWED_EFFECTS = Set.of(NIGHT_VISION, INVISIBILITY, LEAPING, FIRE_RESISTANCE, SWIFTNESS, SLOWNESS, WATER_BREATHING, HEALING, HARMING, POISON, REGENERATION, STRENGTH, WEAKNESS, SLOW_FALLING);
    private static final int MAX_AMPLIFIER = 5, MAX_DURATION = 60, MIN_DURATION = 5;
    
    public RandomPotion() {
        super(Material.POTION);
    }
    
    @Override
    public ItemStack getItemStack() {
        return getItemStack(1);
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        PotionMeta potionMeta = ((PotionMeta) itemStack.getItemMeta());
        List<PotionType> types = new ArrayList<>(ALLOWED_EFFECTS);
        Collections.shuffle(types);
        int index = new Random().nextInt(types.size() - 1) + 1;
        PotionType potionType = types.get(index);
        int amplifier = new Random().nextInt(MAX_AMPLIFIER - 1) + 1;
        int duration = new Random().nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION;
        potionMeta.setBasePotionType(potionType);
        potionMeta.addCustomEffect(new PotionEffect(potionType.getEffectType(), duration * 20, amplifier), true);
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }
}