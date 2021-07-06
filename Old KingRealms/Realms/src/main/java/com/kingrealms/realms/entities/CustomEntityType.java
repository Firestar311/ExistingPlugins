package com.kingrealms.realms.entities;

import com.starmediadev.lib.reflection.FieldHelper;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CustomEntityType<T extends EntityLiving> {
    
    private final Class<T> clazz;
    private EntityTypes<? super T> type;
    
    public CustomEntityType(Class<T> customEntityClass, EntityTypes<? super T> type) {
        this.clazz = customEntityClass;
        this.type = type;
    }
    
    public org.bukkit.entity.Entity spawn(Location loc) {
        Entity entity = type.spawnCreature(((CraftWorld) loc.getWorld()).getHandle(), null, null, null, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), EnumMobSpawn.SPAWNER, true, false);
        return entity == null ? null : entity.getBukkitEntity();
    }
    
    public void register() {
        try {
            Field field = this.type.getClass().getDeclaredField("be");
            field.setAccessible(true);
            if ((field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                FieldHelper.makeNonFinal(field);
            }
            
            field.set(this.type, (EntityTypes.b<?>) (type, world) -> {
                try {
                    return this.clazz.getConstructor(net.minecraft.server.v1_16_R1.World.class).newInstance(world);
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