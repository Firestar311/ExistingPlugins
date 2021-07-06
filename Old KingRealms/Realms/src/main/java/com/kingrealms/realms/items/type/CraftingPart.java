package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.ItemType;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;

public class CraftingPart extends CustomItem {
    public CraftingPart(Material material, ItemType type) {
        super(material, type);
    }
    
    public CraftingPart() {
    }
    
    public CraftingPart(ID id, String displayName, String description, Material material, ItemType type, boolean glowing) {
        super(id, displayName, description, material, type, glowing);
    }
    
    public CraftingPart(ID id, String displayName, String description, Material material, ItemType type, boolean glowing, boolean register) {
        super(id, displayName, description, material, type, glowing, register);
    }
}