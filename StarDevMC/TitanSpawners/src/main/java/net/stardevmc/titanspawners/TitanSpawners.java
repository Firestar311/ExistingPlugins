package net.stardevmc.titanspawners;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class TitanSpawners extends JavaPlugin implements Listener {
    
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("titanspawners")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cOnly players may set a spawner type."));
                return true;
            }
            
            Player player = ((Player) sender);
            
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
            
            if (!player.hasPermission("titanspawners.command")) {
                player.sendMessage(Utils.color("&cYou lack the permission &7(titanspawners.command)&c."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 0, "give", "g")) {
                if (!player.hasPermission("titanspawners.command.give")) {
                    player.sendMessage(Utils.color("&cYou lack the permission &7(titanspawners.command.give)&c."));
                    return true;
                }
                
                if (!(args.length > 1)) {
                    sender.sendMessage("&cYou must provide a type of mob to be given.");
                    return true;
                }
                EntityType type;
                try {
                    type = EntityType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Utils.color("&cYou provided an invalid entity type: " + args[1]));
                    return true;
                }
                
                ItemStack itemStack = ItemBuilder.start(Material.SPAWNER).withName("&d&lMob Spawner: &e&l" + type.name()).withLore(Utils.wrapLore(35, "&7Be careful when placing, &cthis can only be mined with Silk Touch!")).buildItem();
                
                try {
                    itemStack = NBTWrapper.addNBTString(itemStack, "spawnertype", type.name());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                player.getInventory().addItem(itemStack);
                player.sendMessage(Utils.color("&aYou were given a spawner with the type: &e" + type.name()));
            } else if (Utils.checkCmdAliases(args, 0, "set", "s")) {
                if (!player.hasPermission("titanspawners.command.set")) {
                    player.sendMessage(Utils.color("&cYou lack the permission &7(titanspawners.command.set)&c."));
                    return true;
                }
                
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide a type of mob to spawn."));
                    return true;
                }
                
                if (!player.hasPermission("titanspawners.give")) {
                    player.sendMessage("&cYou do not have permisson to use that command.");
                    return true;
                }
                
                Block block = player.getTargetBlock(null, 50);
                if (block == null) {
                    player.sendMessage(Utils.color("&cThe block you are not looking at is null."));
                    return true;
                }
                
                if (!(block.getState() instanceof CreatureSpawner)) {
                    player.sendMessage(Utils.color("&cYou must be looking at a Spawner."));
                    return true;
                }
                
                CreatureSpawner spawner = ((CreatureSpawner) block.getState());
                
                EntityType type;
                try {
                    type = EntityType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Utils.color("&cYou provided an invalid entity type: " + args[1]));
                    return true;
                }
                
                if (!player.hasPermission("titanspawners.type." + type.name().toLowerCase()) || !player.hasPermission("titanspawners.type.all")) {
                    player.sendMessage(Utils.color("&cYou do not have permission to set that type of mob."));
                    return true;
                }
                
                spawner.setSpawnedType(type);
                spawner.update();
                player.sendMessage(Utils.color("&aYou set the spawner type to &e" + type.name()));
            } else {
                sender.sendMessage(Utils.color("&cUnknown subcommand."));
            }
        }
        
        return true;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ItemStack handItem = e.getPlayer().getInventory().getItemInMainHand();
        Block block = e.getBlock();
        
        if (!handItem.hasItemMeta()) return;
        ItemMeta handMeta = handItem.getItemMeta();
        if (!handMeta.hasEnchant(Enchantment.SILK_TOUCH)) return;
        if (!(block.getState() instanceof CreatureSpawner)) return;
        
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType type = spawner.getSpawnedType();
        
        ItemStack itemStack = ItemBuilder.start(Material.SPAWNER).withName("&d&lMob Spawner: &e&l" + type.name()).withLore(Utils.wrapLore(35, "&7Be careful when placing, &cthis can only be mined with Silk Touch!")).buildItem();
        
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "spawnertype", type.name());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        block.getDrops().clear();
        block.getWorld().dropItem(block.getLocation().add(.5, .5, .5), itemStack);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack handItem = e.getItemInHand();
        Block block = e.getBlockPlaced();
        
        if (!handItem.getType().equals(Material.SPAWNER)) return;
        String rawType;
        try {
            rawType = NBTWrapper.getNBTString(handItem, "spawnertype");
        } catch (Exception ex) {
            return;
        }
        EntityType type = EntityType.valueOf(rawType);
        if (type == null) return;
        
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(type);
        spawner.update();
    }
}