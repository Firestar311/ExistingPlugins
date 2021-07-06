package com.kingrealms.realms.entities;

import com.kingrealms.realms.entities.type.*;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.EntityTypes;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.entity.*;

import java.util.HashMap;

public final class CustomEntities {
    private CustomEntities() {}
    
    public static final CustomEntityType<CustomChicken> CHICKEN = new CustomEntityType<>(CustomChicken.class, EntityTypes.CHICKEN);
    public static final CustomEntityType<CustomPig> PIG = new CustomEntityType<>(CustomPig.class, EntityTypes.PIG);
    public static final CustomEntityType<CustomSheep> SHEEP = new CustomEntityType<>(CustomSheep.class, EntityTypes.SHEEP);
    public static final CustomEntityType<CustomCow> COW = new CustomEntityType<>(CustomCow.class, EntityTypes.COW);
    public static final CustomEntityType<CustomFox> FOX = new CustomEntityType<>(CustomFox.class, EntityTypes.FOX);
    public static final CustomEntityType<CustomParrot> PARROT = new CustomEntityType<>(CustomParrot.class, EntityTypes.PARROT);
    public static final CustomEntityType<CustomZombie> ZOMBIE = new CustomEntityType<>(CustomZombie.class, EntityTypes.ZOMBIE);
    public static final CustomEntityType<CustomCreeper> CREEPER = new CustomEntityType<>(CustomCreeper.class, EntityTypes.CREEPER);
    public static final CustomEntityType<CustomRabbit> RABBIT = new CustomEntityType<>(CustomRabbit.class, EntityTypes.RABBIT);
    public static final CustomEntityType<CustomSkeleton> SKELETON = new CustomEntityType<>(CustomSkeleton.class, EntityTypes.SKELETON);
    public static final CustomEntityType<CustomTurtle> TURTLE = new CustomEntityType<>(CustomTurtle.class, EntityTypes.TURTLE);
    public static final CustomEntityType<CustomBee> BEE = new CustomEntityType<>(CustomBee.class, EntityTypes.BEE);
    public static final CustomEntityType<CustomSquid> SQUID = new CustomEntityType<>(CustomSquid.class, EntityTypes.SQUID);
    public static final CustomEntityType<CustomDonkey> DONKEY = new CustomEntityType<>(CustomDonkey.class, EntityTypes.DONKEY);
    public static final CustomEntityType<CustomCat> CAT = new CustomEntityType<>(CustomCat.class, EntityTypes.CAT);
    public static final CustomEntityType<CustomMooshroom> MOOSHROOM = new CustomEntityType<>(CustomMooshroom.class, EntityTypes.MOOSHROOM);
    public static final CustomEntityType<CustomSpider> SPIDER = new CustomEntityType<>(CustomSpider.class, EntityTypes.SPIDER);
    public static final CustomEntityType<CustomHusk> HUSK = new CustomEntityType<>(CustomHusk.class, EntityTypes.HUSK);
    public static final CustomEntityType<CustomPanda> PANDA = new CustomEntityType<>(CustomPanda.class, EntityTypes.PANDA);
    public static final CustomEntityType<CustomBlaze> BLAZE = new CustomEntityType<>(CustomBlaze.class, EntityTypes.BLAZE);
    public static final CustomEntityType<CustomDolphin> DOLPHIN = new CustomEntityType<>(CustomDolphin.class, EntityTypes.DOLPHIN);
    public static final CustomEntityType<CustomDrowned> DROWNED = new CustomEntityType<>(CustomDrowned.class, EntityTypes.DROWNED);
    public static final CustomEntityType<CustomElderGuardian> ELDER_GUARDIAN = new CustomEntityType<>(CustomElderGuardian.class, EntityTypes.ELDER_GUARDIAN);
    public static final CustomEntityType<CustomEnderman> ENDERMAN = new CustomEntityType<>(CustomEnderman.class, EntityTypes.ENDERMAN);
    public static final CustomEntityType<CustomEndermite> ENDERMITE = new CustomEntityType<>(CustomEndermite.class, EntityTypes.ENDERMITE);
    public static final CustomEntityType<CustomEvoker> EVOKER = new CustomEntityType<>(CustomEvoker.class, EntityTypes.EVOKER);
    public static final CustomEntityType<CustomGhast> GHAST = new CustomEntityType<>(CustomGhast.class, EntityTypes.GHAST);
    public static final CustomEntityType<CustomGuardian> GUARDIAN = new CustomEntityType<>(CustomGuardian.class, EntityTypes.GUARDIAN);
    public static final CustomEntityType<CustomHoglin> HOGLIN = new CustomEntityType<>(CustomHoglin.class, EntityTypes.HOGLIN);
    public static final CustomEntityType<CustomHorse> HORSE = new CustomEntityType<>(CustomHorse.class, EntityTypes.HORSE);
    public static final CustomEntityType<CustomIronGolem> IRON_GOLEM = new CustomEntityType<>(CustomIronGolem.class, EntityTypes.IRON_GOLEM);
    public static final CustomEntityType<CustomLlama> LLAMA = new CustomEntityType<>(CustomLlama.class, EntityTypes.LLAMA);
    public static final CustomEntityType<CustomMagmaCube> MAGMA_CUBE = new CustomEntityType<>(CustomMagmaCube.class, EntityTypes.MAGMA_CUBE);
    public static final CustomEntityType<CustomOcelot> OCELOT = new CustomEntityType<>(CustomOcelot.class, EntityTypes.OCELOT);
    public static final CustomEntityType<CustomPhantom> PHANTOM = new CustomEntityType<>(CustomPhantom.class, EntityTypes.PHANTOM);
    public static final CustomEntityType<CustomPiglin> PIGLIN = new CustomEntityType<>(CustomPiglin.class, EntityTypes.PIGLIN);
    public static final CustomEntityType<CustomPillager> PILLAGER = new CustomEntityType<>(CustomPillager.class, EntityTypes.PILLAGER);
    public static final CustomEntityType<CustomPolarBear> POLAR_BEAR = new CustomEntityType<>(CustomPolarBear.class, EntityTypes.POLAR_BEAR);
    public static final CustomEntityType<CustomRavager> RAVAGER = new CustomEntityType<>(CustomRavager.class, EntityTypes.RAVAGER);
    public static final CustomEntityType<CustomShulker> SHULKER = new CustomEntityType<>(CustomShulker.class, EntityTypes.SHULKER);
    public static final CustomEntityType<CustomSilverfish> SILVERFISH = new CustomEntityType<>(CustomSilverfish.class, EntityTypes.SILVERFISH);
    public static final CustomEntityType<CustomSlime> SLIME = new CustomEntityType<>(CustomSlime.class, EntityTypes.SLIME);
    public static final CustomEntityType<CustomStray> STRAY = new CustomEntityType<>(CustomStray.class, EntityTypes.STRAY);
    public static final CustomEntityType<CustomStrider> STRIDER = new CustomEntityType<>(CustomStrider.class, EntityTypes.STRIDER);
    public static final CustomEntityType<CustomVex> VEX = new CustomEntityType<>(CustomVex.class, EntityTypes.VEX);
    public static final CustomEntityType<CustomVillager> VILLAGER = new CustomEntityType<>(CustomVillager.class, EntityTypes.VILLAGER);
    public static final CustomEntityType<CustomVindicator> VINDICATOR = new CustomEntityType<>(CustomVindicator.class, EntityTypes.VINDICATOR);
    public static final CustomEntityType<CustomWitch> WITCH = new CustomEntityType<>(CustomWitch.class, EntityTypes.WITCH);
    public static final CustomEntityType<CustomWither> WITHER = new CustomEntityType<>(CustomWither.class, EntityTypes.WITHER);
    public static final CustomEntityType<CustomWolf> WOLF = new CustomEntityType<>(CustomWolf.class, EntityTypes.WOLF);
    public static final CustomEntityType<CustomZoglin> ZOGLIN = new CustomEntityType<>(CustomZoglin.class, EntityTypes.ZOGLIN);
    public static final CustomEntityType<CustomZombifiedPiglin> ZOMBIFIED_PIGLIN = new CustomEntityType<>(CustomZombifiedPiglin.class, EntityTypes.ZOMBIFIED_PIGLIN);
    
    public static final CustomEntityType<CustomWitherSkeleton> WITHER_SKELETON = new CustomEntityType<>(CustomWitherSkeleton.class, EntityTypes.WITHER_SKELETON);
    
    public static final HashMap<EntityType, CustomEntityType<?>> REGISTRY = new HashMap<>() {{
        put(EntityType.CHICKEN, CHICKEN);
        put(EntityType.PIG, PIG);
        put(EntityType.SHEEP, SHEEP);
        put(EntityType.COW, COW);
        put(EntityType.FOX, FOX);
        put(EntityType.PARROT, PARROT);
        put(EntityType.ZOMBIE, ZOMBIE);
        put(EntityType.CREEPER, CREEPER);
        put(EntityType.RABBIT, RABBIT);
        put(EntityType.SKELETON, SKELETON);
        put(EntityType.WITHER_SKELETON, WITHER_SKELETON);
        put(EntityType.TURTLE, TURTLE);
        put(EntityType.BEE, BEE);
        put(EntityType.SQUID, SQUID);
        put(EntityType.DONKEY, DONKEY);
        put(EntityType.CAT, CAT);
        put(EntityType.MUSHROOM_COW, MOOSHROOM);
        put(EntityType.SPIDER, SPIDER);
        put(EntityType.HUSK, HUSK);
        put(EntityType.PANDA, PANDA);
        put(EntityType.BLAZE, BLAZE);
        put(EntityType.DOLPHIN, DOLPHIN);
        put(EntityType.DROWNED, DROWNED);
        put(EntityType.ELDER_GUARDIAN, ELDER_GUARDIAN);
        put(EntityType.ENDERMAN, ENDERMAN);
        put(EntityType.ENDERMITE, ENDERMITE);
        put(EntityType.EVOKER, EVOKER);
        put(EntityType.GHAST, GHAST);
        put(EntityType.GUARDIAN, GUARDIAN);
        put(EntityType.HOGLIN, HOGLIN);
        put(EntityType.HORSE, HORSE);
        put(EntityType.IRON_GOLEM, IRON_GOLEM);
        put(EntityType.LLAMA, LLAMA);
        put(EntityType.MAGMA_CUBE, MAGMA_CUBE);
        put(EntityType.OCELOT, OCELOT);
        put(EntityType.PHANTOM, PHANTOM);
        put(EntityType.PIGLIN, PIGLIN);
        put(EntityType.PILLAGER, PILLAGER);
        put(EntityType.POLAR_BEAR, POLAR_BEAR);
        put(EntityType.RAVAGER, RAVAGER);
        put(EntityType.SHULKER, SHULKER);
        put(EntityType.SILVERFISH, SILVERFISH);
        put(EntityType.SLIME, SLIME);
        put(EntityType.STRAY, STRAY);
        put(EntityType.STRIDER, STRIDER);
        put(EntityType.VEX, VEX);
        put(EntityType.VILLAGER, VILLAGER);
        put(EntityType.VINDICATOR, VINDICATOR);
        put(EntityType.WITCH, WITCH);
        put(EntityType.WITHER, WITHER);
        put(EntityType.WOLF, WOLF);
        put(EntityType.ZOGLIN, ZOGLIN);
        put(EntityType.ZOMBIFIED_PIGLIN, ZOMBIFIED_PIGLIN);
    }};
    
    public static ICustomEntity getCustomEntity(Entity entity) {
        if (!(entity instanceof LivingEntity)) return null;
        LivingEntity livingEntity = (LivingEntity) entity;
        CraftLivingEntity craftLivingEntity = (CraftLivingEntity) livingEntity;
        EntityLiving nmsEntity = craftLivingEntity.getHandle();
        if (nmsEntity instanceof ICustomEntity) {
            return (ICustomEntity) nmsEntity;
        }
    
        return null;
    }
}