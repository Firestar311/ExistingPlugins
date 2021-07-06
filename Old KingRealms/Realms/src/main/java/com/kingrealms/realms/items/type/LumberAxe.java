package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

public class LumberAxe extends CustomItem {
    
    private ToolType toolType;
    
    public LumberAxe(ToolType type) {
        super(new ID(type.name().toLowerCase() + "_lumberaxe"), Utils.capitalizeEveryWord(type.name()) + " Lumber Axe", "A 3 x 3 tool for chopping trees", ToolType.AXES.get(type), ItemType.TOOL, true);
        this.lore.add("&b" + description);
        this.toolType = type;
    }
    
    public ToolType getToolType() {
        return toolType;
    }
    
    public static boolean isLumberAxe(ItemStack itemStack) {
        try {
            String itemid = NBTWrapper.getNBTString(itemStack, "itemid");
            if (StringUtils.isEmpty(itemid)) return false;
            CustomItem customItem = CustomItemRegistry.REGISTRY.get(new ID(itemid));
            return customItem instanceof LumberAxe;
        } catch (Exception e) {
            return false;
        }
    }
}