package com.kingrealms.realms.entities;

import com.starmediadev.lib.reflection.FieldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R6.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CustomEntityType<T extends Entity> {
    
    private final Class<T> clazz;
    private EntityType<?> type;
    
    public CustomEntityType(Class<T> customEntityClass, EntityType<?> type) {
        this.clazz = customEntityClass;
        this.type = type;
    }
    
    public org.bukkit.entity.Entity spawn(Location loc) {
        Entity entity = type.spawn(((CraftWorld) loc.getWorld()).getHandle(), null, null, new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), EntitySpawnReason.SPAWNER, true, false, SpawnReason.SPAWNER);
        return entity == null ? null : entity.getBukkitEntity();
    }
    
    public void register() {
        try {
            Field field = this.type.getClass().getDeclaredField("factory");
            field.setAccessible(true);
            if ((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                FieldHelper.makeNonFinal(field);
            }
            
            field.set(this.type, (EntityType.EntityFactory<?>) (type, world) -> {
                try {
                    return this.clazz.getConstructor(ServerLevel.class).newInstance((ServerLevel) world);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            });
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}