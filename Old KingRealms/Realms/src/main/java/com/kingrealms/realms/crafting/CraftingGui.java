package com.kingrealms.realms.crafting;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.crafting.recipe.TableRecipe;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CraftingGui extends PaginatedGUI {
    public CraftingGui() {
        super(Realms.getInstance(), "&aCrafting Skill Table", false, 54, true);
        
        ItemStack infoItem = ItemBuilder.start(Material.CRAFTING_TABLE).withName("&aCrafting").withLore(Utils.wrapLore(40, "&7This menu is to be used for custom recipes like convertion Shards, Scraps and Slivers into their full placeable blocks")).buildItem();
        Material craftMaterial;
        if (!Realms.getInstance().getCraftingManager().hasValidRecipe(null)) {
            craftMaterial = Material.LIGHT_GRAY_DYE;
        } else {
            craftMaterial = Material.LIME_DYE;
        }
        ItemStack craftItem = ItemBuilder.start(craftMaterial).withName("&aCraft").buildItem();
        
        GUIButton infoButton = new GUIButton(infoItem);
        GUIButton craftButton = new GUIButton(craftItem);
        ItemStack fillerItem = ItemBuilder.start(Material.WHITE_STAINED_GLASS_PANE).buildItem();
        GUIButton fillerButton = new GUIButton(fillerItem);
        setButton(4, infoButton);
        setButton(13, craftButton);
        
        craftButton.setListener(e -> {
            List<ItemStack> items = getItems(e.getClickedInventory());
            for (TableRecipe tableRecipe : Realms.getInstance().getCraftingManager().getTableRecipes()) {
                if (tableRecipe.matches(items)) {
                    for (int i = 18; i < maxSlots; i++) {
                        e.getClickedInventory().setItem(i, null);
                    }
                    CraftResult result = tableRecipe.craft(items);
                    if (result.isSuccess()) {
                        e.getClickedInventory().addItem(result.getItem());
                        if (!result.getLeftOvers().isEmpty()) {
                            for (ItemStack leftOver : result.getLeftOvers()) {
                                e.getClickedInventory().addItem(leftOver);
                            }
                        }
                    } else {
                        if (!result.getLeftOvers().isEmpty()) {
                            for (ItemStack leftOver : result.getLeftOvers()) {
                                HashMap<Integer, ItemStack> nf = e.getClickedInventory().addItem(leftOver);
                                if (!nf.isEmpty()) {
                                    for (ItemStack itemStack : nf.values()) {
                                        HashMap<Integer, ItemStack> pnf = e.getWhoClicked().getInventory().addItem(itemStack);
                                        if (!pnf.isEmpty()) {
                                            for (ItemStack item : pnf.values()) {
                                                Item dropped = e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), item);
                                                dropped.setVelocity(new Vector(0, 0, 0));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    items.clear();
                    items.addAll(getItems(e.getClickedInventory()));
                }
            }
        });
        
        for (int i = 0; i < 18; i++) {
            if (getButton(i) == null) {
                setButton(i, fillerButton);
            }
        }
        
        for (int i = 18; i < maxSlots; i++) {
            this.setAllowedToInsert(i);
        }
    }
    
    public List<ItemStack> getItems(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 18; i < maxSlots; i++) {
            try {
                items.add(NBTWrapper.cloneItemStack(inventory.getItem(i)));
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return items;
    }
    
    public Material updateCraftButton(boolean value) {
        Material craftMaterial;
        if (!value) {
            craftMaterial = Material.LIGHT_GRAY_DYE;
        } else {
            craftMaterial = Material.LIME_DYE;
        }
        return craftMaterial;
    }
}