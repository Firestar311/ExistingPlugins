package com.kingrealms.realms.items.type;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.*;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

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
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "attack_speed", 20, Operation.ADD_NUMBER, EquipmentSlot.HAND));
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "attack_damage", 20, Operation.ADD_NUMBER, EquipmentSlot.HAND));
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}