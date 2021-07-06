package com.kingrealms.realms.crafting.recipe;

import com.kingrealms.realms.crafting.CraftResult;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

public class TableRecipe {
    
    private ID id;
    private Map<ID, Integer> ingredients = new HashMap<>();
    private ID result;
    
    public TableRecipe(String id) {
        this.id = new ID(id);
    }
    
    public TableRecipe addIngredient(CustomItem customItem, int amount) {
        this.ingredients.put(customItem.getId(), amount);
        return this;
    }
    
    public TableRecipe setResult(CustomItem result) {
        this.result = result.getId();
        return this;
    }
    
    public CustomItem getResult() {
        return CustomItemRegistry.REGISTRY.get(this.result);
    }
    
    public Map<ID, Integer> getIngredients() {
        return this.ingredients;
    }
    
    public boolean matches(List<ItemStack> items) {
        Map<ID, Boolean> ingredientsMatch = new HashMap<>();
        Map<ItemStack, Integer> totalItemCount = getTotalItemCount(items);
        this.ingredients.forEach((id, amount) -> {
            for (Entry<ItemStack, Integer> entry : totalItemCount.entrySet()) {
                CustomItem customItem = CustomItemRegistry.REGISTRY.get(id);
                if (customItem.matches(entry.getKey())) {
                    if (entry.getValue() >= amount) {
                        ingredientsMatch.put(id, true);
                    }
                }
            }
        });
        return ingredientsMatch.size() == this.ingredients.size();
    }
    
    public CraftResult craft(List<ItemStack> items) {
        Map<ItemStack, Integer> totalItemCount = getTotalItemCount(items);
    
        List<ItemStack> leftOvers = new ArrayList<>();
        Map<ItemStack, Integer> amounts = new HashMap<>();
        for (Entry<ItemStack, Integer> entry : totalItemCount.entrySet()) {
            ItemStack itemStack = entry.getKey();
            int totalAmount = entry.getValue();
            boolean noIngredient = true;
            for (Entry<ID, Integer> ingredientEntry : this.ingredients.entrySet()) {
                CustomItem item = CustomItemRegistry.REGISTRY.get(ingredientEntry.getKey());
                if (!item.matches(itemStack)) continue;
                int amount = totalAmount / ingredientEntry.getValue();
                amounts.put(itemStack, amount);
                noIngredient = false;
                break;
            }
    
            if (noIngredient) {
                leftOvers.add(itemStack);
            }
        }
    
        int minimumAmount = -1;
        for (int amount : amounts.values()) {
            if (minimumAmount == -1) {
                minimumAmount = amount;
            } else {
                minimumAmount = Math.min(amount, minimumAmount);
            }
        }
    
        amounts:
        for (Entry<ItemStack, Integer> amountsEntry : amounts.entrySet()) {
            for (Entry<ID, Integer> entry : this.ingredients.entrySet()) {
                CustomItem customItem = CustomItemRegistry.REGISTRY.get(entry.getKey());
                if (!customItem.matches(amountsEntry.getKey())) continue;
                int totalAmountNeeded = entry.getValue() * minimumAmount;
                int totalAmount = totalItemCount.get(amountsEntry.getKey());
                if (totalAmount < totalAmountNeeded) {
                    return new CraftResult(false).setLeftOvers(items);
                }
    
                int leftOver = totalAmount - totalAmountNeeded;
                ItemStack itemStack = null;
                try {
                    itemStack = NBTWrapper.cloneItemStack(amountsEntry.getKey());
                } catch (InvocationTargetException | IllegalAccessException e) {}
                itemStack.setAmount(leftOver);
                leftOvers.add(itemStack);
                continue amounts;
            }
        }
    
        ItemStack result = CustomItemRegistry.REGISTRY.get(this.result).getItemStack();
        result.setAmount(minimumAmount);
        return new CraftResult(result, leftOvers);
    }
    
    private Map<ItemStack, Integer> getTotalItemCount(List<ItemStack> items) {
        Map<ItemStack, Integer> totalItemCount = new HashMap<>();
        for (ItemStack itemStack : items) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) continue;
            if (totalItemCount.containsKey(itemStack)) {
                totalItemCount.put(itemStack, totalItemCount.get(itemStack) + itemStack.getAmount());
            } else {
                totalItemCount.put(itemStack, itemStack.getAmount());
            }
        }
        return totalItemCount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TableRecipe recipe = (TableRecipe) o;
        return Objects.equals(id, recipe.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}