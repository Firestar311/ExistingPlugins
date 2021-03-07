package net.firecraftmc.maniacore.spigot.perks;

import net.firecraftmc.maniacore.spigot.perks.impl.BettyPerk;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.TreeSet;

public class Perks {
    
    public static final Set<Perk> PERKS = new TreeSet<>();
    public static final Perk CHEF, ENCHANT_XP_BOOST, SPEED_KILL, RESISTANCE, REGEN, ASSASSIN, ABSORPTION, ENDERMAN, SURVIVALIST, MIRACLE, SPEED_RACER, FATTY, FEATHERWEIGHT, SHARPSHOOTER, BETTY;
    
    static {
        PERKS.add(SPEED_KILL = new PotionPerk(2000, PotionEffectType.SPEED, 1, 5, Material.SUGAR, Perk.PerkCategory.KILL, "Gives you speed for a short time on a kill."));
        PERKS.add(RESISTANCE = new PotionPerk(500, PotionEffectType.DAMAGE_RESISTANCE, 1, 3, Material.ANVIL, Perk.PerkCategory.KILL, "Gives you Resistance for a short time on a kill."));
        PERKS.add(REGEN = new PotionPerk(1500, PotionEffectType.REGENERATION, 2, 3, Material.POTION, Perk.PerkCategory.KILL, "Gives you regeneration for a short time on a kill."));
        PERKS.add(ASSASSIN = new PotionPerk("Assassin", 5000, PotionEffectType.INVISIBILITY, 1, 5, Material.DIAMOND_SWORD, Perk.PerkCategory.KILL, "Gives you invisibility for a short time on a kill."));
        PERKS.add(ABSORPTION = new PotionPerk(5000, PotionEffectType.ABSORPTION, 1, 30, Material.APPLE, Perk.PerkCategory.KILL, "Gives you absorption for a short time after a kill."));
        PERKS.add(ENDERMAN = new FlatPerk("Enderman", 1000, 100, Material.ENDER_PEARL, Perk.PerkCategory.OTHER, "Completely removes enderpearl teleport damage."));
        PERKS.add(SURVIVALIST = new net.firecraftmc.maniacore.spigot.perks.impl.SurvivalistPerk());
        PERKS.add(MIRACLE = new ItemPerk("Miracle", 1000, 5, new ItemStack(Material.GOLDEN_APPLE), Perk.PerkCategory.KILL, "A low chance to get a golden apple on a kill."));
        PERKS.add(SPEED_RACER = new net.firecraftmc.maniacore.spigot.perks.impl.SpeedRacerPerk());
        PERKS.add(FATTY = new net.firecraftmc.maniacore.spigot.perks.impl.FattyPerk());
        PERKS.add(FEATHERWEIGHT = new net.firecraftmc.maniacore.spigot.perks.impl.FeatherweightPerk());
        PERKS.add(SHARPSHOOTER = new net.firecraftmc.maniacore.spigot.perks.impl.SharpshooterPerk());
        PERKS.add(ENCHANT_XP_BOOST = new net.firecraftmc.maniacore.spigot.perks.impl.EnchantXpPerk());
        PERKS.add(BETTY = new BettyPerk());
        PERKS.add(CHEF = new FlatPerk("Chef", 4000, 100, Material.COOKED_BEEF, Perk.PerkCategory.OTHER, "When holding an uncooked food item \nfor 7 seconds, it will automatically cook."));
    }
    
    public static Perk getPerk(String name) {
        for (Perk perk : PERKS) {
            if (perk.getName().replace(" ", "_").equalsIgnoreCase(name)) {
                return perk;
            }
        }
        return null;
    }
}
