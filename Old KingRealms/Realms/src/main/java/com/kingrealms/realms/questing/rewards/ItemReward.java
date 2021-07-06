package com.kingrealms.realms.questing.rewards;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class ItemReward extends Reward {
    
    private ItemStack itemStack;
    
    public ItemReward(String name, ItemStack itemStack) {
        super(new ID("item"), name);
        try {
            this.itemStack = NBTWrapper.cloneItemStack(itemStack);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void applyReward(RealmProfile profile) {
        profile.getInventory().addItem(itemStack);
    }
}