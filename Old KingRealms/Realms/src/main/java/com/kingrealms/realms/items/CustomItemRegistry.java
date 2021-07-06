package com.kingrealms.realms.items;

import com.kingrealms.realms.entities.CustomEntities;
import com.kingrealms.realms.items.category.ItemCategory;
import com.kingrealms.realms.items.type.*;
import com.kingrealms.realms.skills.farming.CropType;
import com.kingrealms.realms.skills.mining.ResourceType;
import com.kingrealms.realms.skills.woodcutting.TreeType;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public final class CustomItemRegistry {
    public static final Map<ID, CustomItem> REGISTRY = new HashMap<>();
    public static final double SM = 1.3;
    public static final ItemCategory<ResourceType, MysticalResource> MYSTICAL_RESOURCES = new ItemCategory<>("Mystical Resources", Material.GOLD_ORE, "All available Mystical Resources");
    public static final ItemCategory<ResourceType, MysticalSliver> MYSTICAL_SLIVERS = new ItemCategory<>("Mystical Slivers", Material.WHITE_DYE, "All the available Mystical Slivers");
    public static final ItemCategory<CropType, CropItem> CROP_ITEMS = new ItemCategory<>("Crop Blocks", Material.FARMLAND, "All available Crop Blocks");
    public static final ItemCategory<CropType, CropScrap> CROP_SCRAPS = new ItemCategory<>("Crop Scraps", Material.BROWN_DYE, "All available Crop Scraps");
    public static final ItemCategory<TreeType, ArcaneSapling> ARCANE_SAPLINGS = new ItemCategory<>("Arcane Saplings", Material.OAK_SAPLING, "All Arcane Saplings");
    public static final ItemCategory<ToolType, Excavator> EXCAVATORS = new ItemCategory<>("Excavators", Material.GOLDEN_SHOVEL, "All of the Excavator Types");
    public static final ItemCategory<ToolType, Hammer> HAMMERS = new ItemCategory<>("Hammers", Material.GOLDEN_PICKAXE, "All of the Hammer Types");
    public static final ItemCategory<ToolType, LumberAxe> LUMBERAXES = new ItemCategory<>("Lumber Axes", Material.GOLDEN_AXE, "All of the Lumber Axe Types");
    public static final ItemCategory<Material, CustomItem> MACHINES = new ItemCategory<>("Machines", Material.FURNACE, "All of the machine types");
    public static final ItemCategory<Material, CustomItem> LEGENDARY_ITEMS = new ItemCategory<>("Legendary Items", Material.DRAGON_EGG, "Legendary weapons, tools and items");
    public static final ItemCategory<Material, CustomItem> MISC_ITEMS = new ItemCategory<>("Misc Items", Material.MAP, "All Custom Items that do not have a category.");
    public static final ItemCategory<TreeType, WoodChips> WOOD_CHIPS = new ItemCategory<>("Wood Chips", Material.BEETROOT_SEEDS, "All Wood Chips");
    public static final ItemCategory<TreeType, CustomItem> TREE_DROPS = new ItemCategory<>("Tree Drops", Material.OAK_LOG, "All drops from the Lumberjack Skill");
    public static final ItemCategory<EntityType, SpawnerItem> SPAWNERS = new ItemCategory<>("Spawners", Material.SPAWNER, "All available Spawners");
    public static final ItemCategory<EntityType, SpawnerShard> SPAWNER_SHARDS = new ItemCategory<>("Spawner Shards", Material.BLACK_DYE, "All available spawner shards");
    public static final ItemCategory<Material, Resource> RESOURCES = new ItemCategory<>("Resources", Material.DIAMOND, "All resources");
    public static final ItemCategory<EntityType, SoulFragment> SOUL_FRAGMENTS = new ItemCategory<>("Soul Fragments", Material.GHAST_TEAR, "All Soul Fragments");
    
    static {
        for (EntityType type : CustomEntities.REGISTRY.keySet()) {
            SPAWNERS.addItem(type, new SpawnerItem(type));
            SPAWNER_SHARDS.addItem(type, new SpawnerShard(type));
            SOUL_FRAGMENTS.addItem(type, new SoulFragment(type));
        }
        
        for (CropType type : CropType.values()) {
            CROP_ITEMS.addItem(type, new CropItem(type, REGISTRY.get(new ID(type.name().toLowerCase()))));
            CROP_SCRAPS.addItem(type, new CropScrap(type));
        }
        
        for (TreeType type : TreeType.values()) {
            ARCANE_SAPLINGS.addItem(type, new ArcaneSapling(type, type.getSapling()));
            WOOD_CHIPS.addItem(type, new WoodChips(type));
            TREE_DROPS.addItem(type, new Resource(type.getLog()));
        }
        
        for (ResourceType resourceType : ResourceType.values()) {
            MYSTICAL_RESOURCES.addItem(resourceType, new MysticalResource(resourceType));
            MYSTICAL_SLIVERS.addItem(resourceType, new MysticalSliver(resourceType));
            new Resource(resourceType.getDrop());
        }
    
        for (ToolType type : ToolType.PICKAXES.keySet()) {
            HAMMERS.addItem(type, new Hammer(type));
        }
        
        for (ToolType type : ToolType.AXES.keySet()) {
            LUMBERAXES.addItem(type, new LumberAxe(type));
        }
    
        for (ToolType type : ToolType.SHOVELS.keySet()) {
            EXCAVATORS.addItem(type, new Excavator(type));
        }
    }
    
    public static final FlightOrb ORB_OF_FLIGHT = new FlightOrb();
    
    public static final Resource NAUTILUS_SHELL = new Resource(Material.NAUTILUS_SHELL);
    public static final Resource APPLE = new Resource(Material.APPLE);
    public static final Resource ARROW = new Resource(Material.ARROW);
    public static final Potion BAD_OMEN = new Potion(PotionEffectType.BAD_OMEN, 1, Integer.MAX_VALUE);
    public static final Resource BAMBOO = new Resource(Material.BAMBOO);
    public static final Resource BEETROOT = new Resource(Material.BEETROOT);
    public static final Resource BLACK_DYE = new Resource(Material.BLACK_DYE);
    public static final Resource BLAZE_ROD = new Resource(Material.BLAZE_ROD);
    public static final Resource BONE = new Resource(Material.BONE);
    public static final Resource BOW = new Resource(Material.BOW);
    public static final Resource BROWN_DYE = new Resource(Material.BROWN_DYE);
    public static final Resource BROWN_MUSHROOM = new Resource(Material.BROWN_MUSHROOM);
    public static final Resource CACTUS = new Resource(Material.CACTUS);
    public static final Resource CARROT = new Resource(Material.CARROT);
    public static final Resource CHEST = new Resource(Material.CHEST);
    public static final Resource CHORUS = new Resource(Material.CHORUS_FLOWER);
    public static final Resource COAL = new Resource(Material.COAL);
    public static final Resource COCO_BEANS = new Resource(Material.COCOA_BEANS);
    public static final Resource CREEPER_HEAD = new Resource(Material.CREEPER_HEAD);
    public static final Resource CROSS_BOW = new Resource(Material.CROSSBOW, true);
    public static final Resource CYAN_DYE = new Resource(Material.CYAN_DYE);
    public static final Resource DANDELION = new Resource(Material.DANDELION);
    public static final Resource EGG = new Resource(Material.EGG);
    public static final Resource EMERALD = new Resource(Material.EMERALD);
    public static final Resource ENDER_PEARL = new Resource(Material.ENDER_PEARL);
    public static final Resource END_STONE = new Resource(Material.END_STONE);
    public static final Excalibur EXCALIBUR = new Excalibur();
    public static final Resource FEATHER = new Resource(Material.FEATHER);
    public static final Resource FIRE_CHARGE = new Resource(Material.FIRE_CHARGE);
    public static final Resource GHAST_TEAR = new Resource(Material.GHAST_TEAR);
    public static final Resource GLASS_BOTTLE = new Resource(Material.GLASS_BOTTLE);
    public static final Resource GLOWSTONE = new Resource(Material.GLOWSTONE);
    public static final Resource GOLD = new Resource(Material.GOLD_INGOT);
    public static final Resource GOLD_BOOTS = new Resource(Material.GOLDEN_BOOTS, true);
    public static final Resource GOLD_CHESTPLATE = new Resource(Material.GOLDEN_CHESTPLATE, true);
    public static final Resource GOLD_HELMET = new Resource(Material.GOLDEN_HELMET, true);
    public static final Resource GOLD_LEGGINGS = new Resource(Material.GOLDEN_LEGGINGS, true);
    //TODO Arcane Tiller name of a hoe that can till ground in an area of effect
    public static final Resource GOLD_NUGGET = new Resource(Material.GOLD_NUGGET);
    public static final Resource GOLD_SWORD = new Resource(Material.GOLDEN_SWORD, true);
    public static final Resource GRAY_DYE = new Resource(Material.GRAY_DYE);
    public static final Resource GREEN_DYE = new Resource(Material.GREEN_DYE);
    public static final Resource GUNPOWDER = new Resource(Material.GUNPOWDER);
    public static final Resource HONEY = new Resource(Material.HONEY_BOTTLE);
    public static final Resource HONEY_COMB = new Resource(Material.HONEYCOMB);
    public static final Resource INK_SAC = new Resource(Material.INK_SAC);
    public static final Resource IRON = new Resource(Material.IRON_INGOT);
    public static final Resource IRON_AXE = new Resource(Material.IRON_AXE, true);
    public static final Resource IRON_NUGGET = new Resource(Material.IRON_NUGGET);
    public static final Resource IRON_SWORD = new Resource(Material.IRON_SWORD, true);
    public static final Resource KELP = new Resource(Material.KELP);
    public static final Resource LAPIS = new Resource(Material.LAPIS_LAZULI);
    public static final Resource LEAD = new Resource(Material.LEAD);
    public static final Resource LEATHER = new Resource(Material.LEATHER);
    public static final TippedArrow LEVITATION_ARROW = new TippedArrow(PotionEffectType.LEVITATION, 1);
    public static final Resource LIGHT_GRAY_DYE = new Resource(Material.LIGHT_GRAY_DYE);
    public static final CustomItem CRAFTING_SKILL_TABLE = new CustomItem(new ID("crafting_skill_table"), "&a&lCrafting Skill Table", "", Material.CARTOGRAPHY_TABLE, ItemType.CRAFTING_RESOURCE_CORE, true, true).setCategory(MACHINES);
    public static final Resource MAGMA_BLOCK = new Resource(Material.MAGMA_BLOCK);
    public static final Resource MAGMA_CREAM = new Resource(Material.MAGMA_CREAM);
    public static final Resource MELON = new Resource(Material.MELON);
    public static final Resource MILK_BUCKET = new Resource(Material.MILK_BUCKET);
    public static final Resource MUSHROOM_STEW = new Resource(Material.MUSHROOM_STEW);
    public static final Resource NETHERITE_SCRAP = new Resource(Material.NETHERITE_SCRAP);
    public static final CustomItem NETHER_PICKAXE = new CustomItem(new ID("nether_pickaxe"), "&eNether Pickaxe", "Use this to mine obsidian for the quest", Material.DIAMOND_PICKAXE, ItemType.TOOL, true, true);
    public static final Resource NETHER_STAR = new Resource(Material.NETHER_STAR);
    public static final Resource NETHER_WART = new Resource(Material.NETHER_WART);
    public static final Resource OBSIDIAN = new Resource(Material.OBSIDIAN);
    public static final Resource ORANGE_DYE = new Resource(Material.ORANGE_DYE);
    public static final Resource PHANTOM_MEMBRANE = new Resource(Material.PHANTOM_MEMBRANE);
    public static final Resource PINK_DYE = new Resource(Material.PINK_DYE);
    public static final Resource POPPY = new Resource(Material.POPPY);
    public static final Resource POTATO = new Resource(Material.POTATO);
    public static final Resource PRISMARINE_CRYSTAL = new Resource(Material.PRISMARINE_CRYSTALS);
    public static final Resource PRISMARINE_SHARD = new Resource(Material.PRISMARINE_SHARD);
    public static final Resource PUMPKIN = new Resource(Material.PUMPKIN);
    public static final Resource PURPLE_DYE = new Resource(Material.PURPLE_DYE);
    public static final Resource PURPUR_BLOCK = new Resource(Material.PURPUR_BLOCK);
    public static final Resource QUARTZ = new Resource(Material.QUARTZ);
    public static final Resource RABBIT_FOOT = new Resource(Material.RABBIT_FOOT);
    public static final Resource RABBIT_HIDE = new Resource(Material.RABBIT_HIDE);
    public static final RandomPotion RANDOM_POTION = new RandomPotion();
    public static final RandomRawFish RANDOM_RAW_FISH = new RandomRawFish();
    public static final Resource RAW_BEEF = new Resource(Material.BEEF, "Raw Beef");
    public static final Resource RAW_CHICKEN = new Resource(Material.CHICKEN, "Raw Chicken");
    public static final Resource RAW_COD = new Resource(Material.COD);
    public static final Resource RAW_MUTTON = new Resource(Material.MUTTON, "Raw Mutton");
    public static final Resource RAW_PORKCHOP = new Resource(Material.PORKCHOP, "Raw Porkchop");
    public static final Resource RAW_RABBIT = new Resource(Material.RABBIT, "Raw Rabbit");
    public static final Resource RAW_SALMON = new Resource(Material.SALMON);
    public static final Resource REDSTONE = new Resource(Material.REDSTONE);
    public static final Resource RED_DYE = new Resource(Material.RED_DYE);
    public static final Resource RED_MUSHROOM = new Resource(Material.RED_MUSHROOM);
    public static final Resource SADDLE = new Resource(Material.SADDLE);
    public static final Resource SAND = new Resource(Material.SAND);
    public static final Resource SCUTE = new Resource(Material.SCUTE);
    public static final Resource SEAGRASS = new Resource(Material.SEAGRASS);
    public static final Resource SHULKER_SHELL = new Resource(Material.SHULKER_SHELL);
    public static final Resource SKELETON_HEAD = new Resource(Material.SKELETON_SKULL);
    public static final Resource SLIME_BALL = new Resource(Material.SLIME_BALL);
    public static final Resource SNOWBALL = new Resource(Material.SNOWBALL);
    public static final Resource SPIDER_EYE = new Resource(Material.SPIDER_EYE);
    public static final Resource STICK = new Resource(Material.STICK);
    public static final Resource STONE_SWORD = new Resource(Material.STONE_SWORD, true);
    public static final Resource STRING = new Resource(Material.STRING);
    public static final Resource SUGAR_CANE = new Resource(Material.SUGAR_CANE);
    public static final Resource SWEET_BERRIES = new Resource(Material.SWEET_BERRIES);
    public static final TippedArrow TIPPED_ARROW = new TippedArrow();
    public static final Resource TNT = new Resource(Material.TNT);
    public static final Resource TOTEM_OF_UNDYING = new Resource(Material.TOTEM_OF_UNDYING);
    public static final Resource TRIDENT = new Resource(Material.TRIDENT);
    public static final Resource WET_SPONGE = new Resource(Material.WET_SPONGE);
    public static final Resource WHEAT = new Resource(Material.WHEAT);
    public static final Resource WHITE_DYE = new Resource(Material.WHITE_DYE);
    public static final Resource WHITE_WOOL = new Resource(Material.WHITE_WOOL);
    public static final Resource WITHER_ROSE = new Resource(Material.WITHER_ROSE);
    public static final Resource WITHER_SKULL = new Resource(Material.WITHER_SKELETON_SKULL);
    public static final Resource YELLOW_DYE = new Resource(Material.YELLOW_DYE);
    public static final Resource ROTTEN_FLESH = new Resource(Material.ROTTEN_FLESH);
    public static final Resource ZOMBIE_HEAD = new Resource(Material.ZOMBIE_HEAD);
    public static final CustomItem PORTAL_IGNIGTER = new CustomItem(new ID("portal_igniter"), "&6&lPortal Igniter", "Use this for the lighting of the portal for the quest.", Material.FLINT_AND_STEEL, ItemType.TOOL, true);
    
    private CustomItemRegistry() {
    }
    
    public static CustomItem getCustomItem(Material material) {
        for (CustomItem customItem : REGISTRY.values()) {
            if (customItem.getMaterial().equals(material)) {
                return customItem;
            }
        }
        
        return null;
    }
}