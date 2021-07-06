package com.kingrealms.realms.items.type;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import java.util.*;

import static org.bukkit.potion.PotionType.*;

@SuppressWarnings("DuplicatedCode")
public class TippedArrow extends Resource {
    
    private static final Set<PotionType> ALLOWED_EFFECTS = Set.of(NIGHT_VISION, INVISIBILITY, JUMP, FIRE_RESISTANCE, SPEED, SLOWNESS, WATER_BREATHING, INSTANT_HEAL, INSTANT_DAMAGE, POISON, REGEN, STRENGTH, WEAKNESS, SLOW_FALLING);
    private static final int MAX_AMPLIFIER = 5, MAX_DURATION = 60, MIN_DURATION = 5;
    private int amplifier;
    private PotionEffectType type;
    
    public TippedArrow() {
        super(Material.TIPPED_ARROW);
    }
    
    public TippedArrow(PotionEffectType effect, int i) {
        super(Material.TIPPED_ARROW);
        this.type = effect;
        this.amplifier = i;
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        PotionMeta potionMeta = ((PotionMeta) itemStack.getItemMeta());
        PotionEffectType effectType;
        if (this.type != null) {
            List<PotionType> types = new ArrayList<>(ALLOWED_EFFECTS);
            Collections.shuffle(types);
            int index = new Random().nextInt(types.size() - 1) + 1;
            PotionType potionType = types.get(index);
            int amplifier = new Random().nextInt(MAX_AMPLIFIER - 1) + 1;
            int duration = new Random().nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION;
            effectType = potionType.getEffectType();
            potionMeta.setBasePotionData(new PotionData(potionType));
            potionMeta.addCustomEffect(new PotionEffect(effectType, duration * 20, amplifier), true);
        } else {
            PotionType potionType = null;
            for (PotionType pt : PotionType.values()) {
                if (pt.getEffectType() != null) {
                    if (pt.getEffectType().equals(this.type)) {
                        potionType = pt;
                        break;
                    }
                }
            }
            
            if (potionType == null) potionType = MUNDANE;
            potionMeta.setBasePotionData(new PotionData(potionType));
            int duration = new Random().nextInt(MAX_DURATION - MIN_DURATION) + MIN_DURATION;
            potionMeta.addCustomEffect(new PotionEffect(this.type, duration * 20, amplifier), true);
            effectType = type;
        }
        
        potionMeta.setDisplayName("&ePotion of " + effectType.getName());
        
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }
}