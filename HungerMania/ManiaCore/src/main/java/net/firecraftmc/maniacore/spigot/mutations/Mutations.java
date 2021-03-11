package net.firecraftmc.maniacore.spigot.mutations;

import net.firecraftmc.maniacore.spigot.mutations.types.Zombie;

import java.util.HashMap;
import java.util.Map;

public final class Mutations {
    public static final Mutation PIG_ZOMBIE = new net.firecraftmc.maniacore.spigot.mutations.types.PigZombie();
    public static final Mutation CREEPER = new net.firecraftmc.maniacore.spigot.mutations.types.Creeper();
    public static final Mutation CHICKEN = new net.firecraftmc.maniacore.spigot.mutations.types.Chicken();
    public static final Mutation ENDERMAN = new net.firecraftmc.maniacore.spigot.mutations.types.Enderman();
    public static final Mutation SKELETON = new net.firecraftmc.maniacore.spigot.mutations.types.Skeleton();
    public static final Mutation ZOMBIE = new Zombie();
    
    public static final Map<MutationType, Mutation> MUTATIONS = new HashMap<>();
    static {
        MUTATIONS.put(MutationType.PIG_ZOMBIE, PIG_ZOMBIE);
        MUTATIONS.put(MutationType.CREEPER, CREEPER);
        //MUTATIONS.put(MutationType.CHICKEN, CHICKEN);
        MUTATIONS.put(MutationType.ZOMBIE, ZOMBIE);
        MUTATIONS.put(MutationType.ENDERMAN, ENDERMAN);
        MUTATIONS.put(MutationType.SKELETON, SKELETON);
    }
}
