package com.kingrealms.realms.skills.mining;

import com.kingrealms.realms.skills.base.SkillLevel;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class MiningLevel extends SkillLevel {
    private Material material;
    private Set<Material> materials = new HashSet<>();
    private String name;
    
    public MiningLevel(int level, Material material, double xpPerBlock, double totalXpNeeded) {
        super(level, xpPerBlock, totalXpNeeded, material);
        this.material = material;
        this.name = MaterialNames.getName(material).replace(" Ore", "");
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public MiningLevel addMaterial(Material material) {
        this.materials.add(material);
        return this;
    }
    
    public Set<Material> getMaterials() {
        return materials;
    }
    
    @Override
    public ItemStack getIcon() {
        return ItemBuilder.start(material).withName("&e" + name).withLore("", "&7+&a" + Constants.NUMBER_FORMAT.format(getXpPerHarvest()) + " Base XP", "&e" + Constants.NUMBER_FORMAT.format(getTotalXpNeeded()) + " total XP needed to unlock").buildItem();
    }
    
    public MiningLevel setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getName() {
        return name;
    }
}