package com.stardevmc.shop.worth;

import com.firestar311.lib.config.ConfigManager;
import com.firestar311.lib.util.Result;
import com.stardevmc.shop.TitanShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class WorthManager {
    
    private Map<Material, Double> materialWorth = new HashMap<>();
    @SuppressWarnings("unused")
    private Map<ItemStack, Double> itemStackWorth = new HashMap<>();
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private ConfigManager configManager;
    
    public WorthManager(TitanShop plugin) {
        this.configManager = new ConfigManager(plugin, "worth");
        //this.configManager.setup();
    }
    
    public void setWorth(Material material, double value) {
        this.materialWorth.put(material, value);
    }
    
    public Result<StackWorth, String> calculateWorth(ItemStack itemStack) {
        List<Recipe> stackRecipes = Bukkit.getServer().getRecipesFor(itemStack);
        if (stackRecipes.isEmpty()) {
            return new Result<>(null, "No recipes");
        }
        
        Map<Material, Integer> stackMaterials = new HashMap<>();
        if (stackRecipes.size() > 1) {
            return new Result<>(null, "Too many recipes for that ItemStack");
        }
        Recipe recipe = stackRecipes.get(0);
        getRecipeIngredients(stackMaterials, recipe);
        
        if (stackMaterials.isEmpty()) return new Result<>(null, "No materials found in the recipe.");
        
        List<WorthIngredient> worthIngredients = new ArrayList<>();
        double totalWorth = 0;
        for (Entry<Material, Integer> entry : stackMaterials.entrySet()) {
            Material material = entry.getKey();
            Integer amount = entry.getValue();
            
            if (!this.materialWorth.containsKey(material))
                return new Result<>(null, "No monetary value found for material " + material.name());
            totalWorth += amount * materialWorth.get(material);
            worthIngredients.add(new WorthIngredient(material, amount, materialWorth.get(material)));
        }
        
        return new Result<>(new StackWorth(itemStack, totalWorth, worthIngredients), null);
    }
    
    private void getRecipeIngredients(Map<Material, Integer> stackMaterials, Recipe recipe) {
        List<ItemStack> ingredientList = null;
        if (recipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = ((ShapelessRecipe) recipe);
            ingredientList = shapelessRecipe.getIngredientList();
        } else
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shapedRecipe = ((ShapedRecipe) recipe);
                if (!shapedRecipe.getIngredientMap().isEmpty()) {
                    ingredientList = shapedRecipe.getIngredientMap().values().stream().filter(itemStack -> itemStack != null).collect(Collectors.toList());
                } else {
                    if (!shapedRecipe.getChoiceMap().isEmpty()) {
                        System.out.println(shapedRecipe.getChoiceMap().toString());
                        ingredientList = new ArrayList<>();
                        Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
                        for (Entry<Character, RecipeChoice> entry : choiceMap.entrySet()) {
                            RecipeChoice choice = entry.getValue();
                            System.out.println(choice);
                            if (choice instanceof MaterialChoice) {
                                MaterialChoice materialChoice = ((MaterialChoice) choice);
                                for (Material choiceMaterial : materialChoice.getChoices()) {
                                    if (stackMaterials.containsKey(choiceMaterial)) {
                                        ingredientList.add(materialChoice.getItemStack());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        
        for (ItemStack ingredient : ingredientList) {
            Material type = ingredient.getType();
            List<Recipe> stackRecipes = Bukkit.getServer().getRecipesFor(ingredient);
            if (stackRecipes.size() == 1) {
                getRecipeIngredients(stackMaterials, stackRecipes.get(0));
                continue;
            }
            if (stackMaterials.containsKey(type)) {
                stackMaterials.put(type, stackMaterials.get(type) + 1);
            } else {
                stackMaterials.put(type, 1);
            }
        }
    }
    
    public void loadData() {
    
    }
    
    public void saveData() {
    
    }
    
    public double getWorth(Material material) {
        return (materialWorth.containsKey(material) ? materialWorth.get(material) : -1);
    }
}