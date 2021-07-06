package com.kingrealms.realms.crafting;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.crafting.recipe.TableRecipe;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.ToolType;
import com.kingrealms.realms.items.type.*;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.items.NBTWrapper;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@SuppressWarnings("SameParameterValue")
public class CraftingManager implements Listener {
    
    private Realms plugin = Realms.getInstance();
    private ConfigManager configManager = StorageManager.craftingConfig;
    private Set<Location> tableLocations = new HashSet<>();
    private Set<TableRecipe> tableRecipes = new HashSet<>();
    
    public CraftingManager() {
        configManager.setup();
        plugin.getSeason().addListener(this);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getSeason().isActive()) { return; }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getOpenInventory() != null) {
                        Inventory inv = p.getOpenInventory().getTopInventory();
                        if (inv.getHolder() instanceof CraftingGui) {
                            CraftingGui gui = (CraftingGui) inv.getHolder();
                            boolean validRecipe = hasValidRecipe(gui.getItems(inv));
                            Material material = gui.updateCraftButton(validRecipe);
                            ItemStack itemStack = inv.getItem(13);
                            if (itemStack.getType() != material) {
                                itemStack.setType(material);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
    
    public boolean hasValidRecipe(List<ItemStack> itemStacks) {
        if (itemStacks == null || itemStacks.isEmpty()) { return false; }
        for (TableRecipe recipe : this.tableRecipes) {
            if (recipe.matches(itemStacks)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void saveData() {
        int i = 0;
        for (Location location : tableLocations) {
            configManager.getConfig().set("tables." + i, location);
            i++;
        }
        
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("tables")) { return; }
        ConfigurationSection section = config.getConfigurationSection("tables");
        for (String t : section.getKeys(false)) {
            Location location = section.getLocation(t);
            this.tableLocations.add(location);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) { return; }
        Block block = e.getClickedBlock();
        if (this.tableLocations.contains(block.getLocation())) {
            e.setCancelled(true);
            new CraftingGui().openGUI(e.getPlayer());
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CraftingGui) {
            CraftingGui gui = (CraftingGui) e.getInventory().getHolder();
            List<ItemStack> items = gui.getItems(e.getInventory());
            if (!items.isEmpty()) {
                for (ItemStack itemStack : items) {
                    if (itemStack != null && itemStack.getType() != Material.AIR) {
                        HashMap<Integer, ItemStack> nf = e.getPlayer().getInventory().addItem(itemStack);
                        if (!nf.isEmpty()) {
                            for (ItemStack item : nf.values()) {
                                Item dropped = e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item);
                                dropped.setVelocity(new Vector(0, 0, 0));
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack hand = e.getItemInHand();
        try {
            String id = NBTWrapper.getNBTString(hand, "itemid");
            if (id.equalsIgnoreCase(CustomItemRegistry.CRAFTING_SKILL_TABLE.getId().toString())) {
                tableLocations.add(e.getBlockPlaced().getLocation());
                RealmsAPI.getProfile(e.getPlayer()).sendMessage("&gPlaced &hCrafting Skill Table");
            }
        } catch (Exception ex) {}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (tableLocations.contains(e.getBlock().getLocation())) {
            this.tableLocations.remove(e.getBlock().getLocation());
            RealmsAPI.getProfile(e.getPlayer()).sendMessage("&gRemoved &hCrafting Skill Table");
            if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                RealmsAPI.getProfile(e.getPlayer()).getInventory().addItem(CustomItemRegistry.CRAFTING_SKILL_TABLE.getItemStack());
            }
            e.setDropItems(false);
        }
    }
    
    public void registerVanillaRecipes() {
        BlastingRecipe cobbleToStone = new BlastingRecipe(createKey("cobbletostone"), new ItemStack(Material.STONE), Material.COBBLESTONE, 2F, 100);
        plugin.getServer().addRecipe(cobbleToStone);
    
        ShapedRecipe redMushroom = new ShapedRecipe(createKey("redmushroomblock"), new ItemStack(Material.RED_MUSHROOM_BLOCK));
        redMushroom.shape("RRR", "RRR", "RRR");
        redMushroom.setIngredient('R', Material.RED_MUSHROOM);
        plugin.getServer().addRecipe(redMushroom);
    
        ShapedRecipe brownMushroom = new ShapedRecipe(createKey("brownmushroomblock"), new ItemStack(Material.BROWN_MUSHROOM_BLOCK));
        brownMushroom.shape("BBB", "BBB", "BBB");
        brownMushroom.setIngredient('B', Material.BROWN_MUSHROOM);
        plugin.getServer().addRecipe(brownMushroom);
    
        ShapelessRecipe portalIgnigter = new ShapelessRecipe(createKey("portalIgniter"), CustomItemRegistry.PORTAL_IGNIGTER.getItemStack());
        portalIgnigter.addIngredient(Material.FLINT);
        portalIgnigter.addIngredient(Material.IRON_BLOCK);
        plugin.getServer().addRecipe(portalIgnigter);
    
        ShapedRecipe netherPickaxe = new ShapedRecipe(createKey("netherPickaxe"), CustomItemRegistry.NETHER_PICKAXE.getItemStack());
        netherPickaxe.shape("DDD", " G ", " G ");
        netherPickaxe.setIngredient('D', Material.DIAMOND_BLOCK);
        netherPickaxe.setIngredient('G', Material.GOLD_BLOCK);
        plugin.getServer().addRecipe(netherPickaxe);
    
        ShapedRecipe craftingSkillTable = new ShapedRecipe(createKey("craftingSkillTable"), CustomItemRegistry.CRAFTING_SKILL_TABLE.getItemStack());
        craftingSkillTable.shape("CC", "CC");
        craftingSkillTable.setIngredient('C', Material.CRAFTING_TABLE);
        plugin.getServer().addRecipe(craftingSkillTable);
    
        for (ToolType toolType : ToolType.values()) {
            String name = toolType.name().toLowerCase();
            plugin.getServer().addRecipe(createHammerRecipe(name + "Hammer", CustomItemRegistry.HAMMERS.getItem(toolType), toolType.getMain(), toolType.getStick()));
            plugin.getServer().addRecipe(createExcavatorRecipe(name + "Excavator", CustomItemRegistry.EXCAVATORS.getItem(toolType), toolType.getMain(), toolType.getStick()));
            plugin.getServer().addRecipe(createLumberaxeRecipe(name + "Lumberaxe", CustomItemRegistry.LUMBERAXES.getItem(toolType), toolType.getMain(), toolType.getStick()));
        }
    }
    
    private NamespacedKey createKey(String name) {
        return new NamespacedKey(plugin, name);
    }
    
    private Recipe createHammerRecipe(String key, Hammer hammer, Material material, Material stick) {
        ShapedRecipe recipe = new ShapedRecipe(createKey(key), hammer.getItemStack());
        recipe.shape("MMM", " S ", " S ");
        recipe.setIngredient('M', material);
        recipe.setIngredient('S', stick);
        return recipe;
    }
    
    private Recipe createExcavatorRecipe(String key, Excavator excavator, Material material, Material stick) {
        ShapedRecipe recipe = new ShapedRecipe(createKey(key), excavator.getItemStack());
        recipe.shape(" M ", " S ", " S ");
        recipe.setIngredient('M', material);
        recipe.setIngredient('S', stick);
        return recipe;
    }
    
    private Recipe createLumberaxeRecipe(String key, LumberAxe lumberAxe, Material material, Material stick) {
        ShapedRecipe recipe = new ShapedRecipe(createKey(key), lumberAxe.getItemStack());
        recipe.shape("MM ", "MS ", " S ");
        recipe.setIngredient('M', material);
        recipe.setIngredient('S', stick);
        return recipe;
    }
    
    public void registerTableRecipes() {
        CustomItemRegistry.SPAWNER_SHARDS.getRegistry().forEach((type, item) -> tableRecipes.add(new TableRecipe(type.name().toLowerCase() + "_spawner_recipe").addIngredient(item, 8).setResult(CustomItemRegistry.SPAWNERS.getItem(type))));
        CustomItemRegistry.MYSTICAL_SLIVERS.getRegistry().forEach((type, item) -> tableRecipes.add(new TableRecipe(type.name().toLowerCase() + "_mystical_recipe").addIngredient(item, 8).setResult(CustomItemRegistry.MYSTICAL_RESOURCES.getItem(type))));
        CustomItemRegistry.CROP_SCRAPS.getRegistry().forEach((type, item) -> tableRecipes.add(new TableRecipe(type.name().toLowerCase() + "_crop_recipe").addIngredient(item, 8).setResult(CustomItemRegistry.CROP_ITEMS.getItem(type))));
        CustomItemRegistry.WOOD_CHIPS.getRegistry().forEach((type, item) -> tableRecipes.add(new TableRecipe(type.name().toLowerCase() + "_chip_recipe").addIngredient(item, 8).setResult(CustomItemRegistry.ARCANE_SAPLINGS.getItem(type))));
    }
    
    public void addTableRecipe(TableRecipe recipe) {
        this.tableRecipes.add(recipe);
    }
    
    public Set<TableRecipe> getTableRecipes() {
        return this.tableRecipes;
    }
}