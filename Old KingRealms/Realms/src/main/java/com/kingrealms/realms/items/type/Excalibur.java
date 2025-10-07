package com.kingrealms.realms.items.type;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.ItemType;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("UnstableApiUsage")
public class Excalibur extends CustomItem implements Listener {
    public Excalibur() {
        super(new ID("excalibur"), "&6&lExcalibur", "A legendary weapon with unmatched power", Material.DIAMOND_SWORD, ItemType.LEGENDARY_WEAPON, true);
        Realms.getInstance().getServer().getPluginManager().registerEvents(this, Realms.getInstance());
        //CustomItemRegistry.LEGENDARY_ITEMS.addItem(getMaterial(), this);
    }
    
    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = super.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(new NamespacedKey("kingrealms", "attackspeed"), 20, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        itemMeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey("kingrealms", "attackdamage"), 20, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}