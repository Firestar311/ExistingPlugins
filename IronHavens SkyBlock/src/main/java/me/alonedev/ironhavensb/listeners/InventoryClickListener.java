package me.alonedev.ironhavensb.listeners;

import me.alonedev.ironhavensb.Main;
import me.alonedev.ironhavensb.guis.BiomeGUI;
import me.alonedev.ironhavensb.guis.CreatedGUI;
import me.alonedev.ironhavensb.guis.ReloadGUI;
import me.alonedev.ironhavensb.guis.UpgradeGUI;
import me.alonedev.ironhavensb.island.CreateIsland;
import me.alonedev.ironhavensb.island.DeleteIsland;
import me.alonedev.ironhavensb.island.InviteIsland;
import me.alonedev.ironhavensb.upgrades.BorderUpgrade;
import com.sk89q.worldedit.WorldEditException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.io.IOException;

public class InventoryClickListener implements Listener {

    private Main plugin;

    public InventoryClickListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws WorldEditException, IOException {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("Island viewer")) {
            e.setCancelled(true);
            if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            if (e.getSlot() == 4 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lTeleport to your island"))) {
                p.teleport(new Location(Bukkit.getWorld(plugin.islandsConfig.getString(p.getName())), 0, 81, 0));
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Teleported you to your island!"));
            } else if (e.getSlot() == 2 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lUpgrade your island"))) {
                new UpgradeGUI(p, plugin);
            } else if (e.getSlot() == 0 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lReload island"))) {
                new ReloadGUI(p, plugin);
            } else if (e.getSlot() == 8 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lDelete island"))) {
                World world = Bukkit.getWorld(p.getUniqueId().toString());
                new DeleteIsland(world.getWorldFolder(), world, plugin, p);
                p.closeInventory();
            } else if (e.getSlot() == 6 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§lInvite Friends"))) {
                new InviteIsland(p, plugin);
            }

        } else if (e.getView().getTitle().equalsIgnoreCase("Create your island")) {
            e.setCancelled(true);
            if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            if (e.getSlot() == 4 && p.getOpenInventory() == BiomeGUI.BiomeGUI) {
                new BiomeGUI(p, plugin);
            }

        } else if (e.getView().getTitle().equalsIgnoreCase("Island Upgrades")) {
            e.setCancelled(true);
            if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            if (e.getSlot() == 2 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lIncrease world border"))) {
                new BorderUpgrade(p, plugin);
            } else if (e.getSlot() == 2 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lIncrease Coop-member limit"))) {
                //TODO new CoopUpgrade(p, plugin);
            }

        } else if (e.getView().getTitle().equalsIgnoreCase("Reload Island")) {
            e.setCancelled(true);
            if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            if (e.getSlot() == 3 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§lConfirm"))) {
                new BiomeGUI(p, plugin);
            } else if (e.getSlot() == 5 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lCancel"))) {
                new CreatedGUI(p, plugin);
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("Accept Island Invite")) {
            try {
                e.setCancelled(true);
                if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                    return;
                }
                if (e.getSlot() == 2 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§a§lAccept"))) {
                    //TODO new JoinIsland(p, InviteGUI.invitor, plugin);
                } else if (e.getSlot() == 6 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lDecline"))) {
                    p.sendMessage(ChatColor.RED + "[Iron Haven] >> You declined the coop invitation!");
                }
            } catch (Exception exc) {
                p.sendMessage(ChatColor.GREEN + "[Iron Haven] >> Sent island invite!");
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("Biome Chooser")) {
            e.setCancelled(true);
            if ((e.getCurrentItem() == null) || (e.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            if (e.getSlot() == 1 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lJungle Wood Biome"))) {
                new CreateIsland(p.getLocation(), new File(plugin.getDataFolder() + File.separator + "islands" + File.separator + "Jungle.schem"), p, plugin);
                p.closeInventory();
            } else if (e.getSlot() == 3 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lSpruce Wood Biome"))) {
                new CreateIsland(p.getLocation(), new File(plugin.getDataFolder() + File.separator + "islands" + File.separator + "Spruce.schem"), p, plugin);
                p.closeInventory();
            } else if (e.getSlot() == 5 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lOak Wood Biome"))) {
                new CreateIsland(p.getLocation(), new File(plugin.getDataFolder() + File.separator + "islands" + File.separator + "Oak.schem"), p, plugin);
                p.closeInventory();
            } else if (e.getSlot() == 7 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lAcacia Wood Biome"))) {
                new CreateIsland(p.getLocation(), new File(plugin.getDataFolder() + File.separator + "islands" + File.separator + "Acacia.schem"), p, plugin);
                p.closeInventory();
            } else if (e.getSlot() == 11 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lClassic Island"))) {
                new CreateIsland(p.getLocation(), new File(plugin.getDataFolder() + File.separator + "islands" + File.separator + "Classic.schem"), p, plugin);
                p.closeInventory();
            } else if (e.getSlot() == 15 && (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6§lHardcore Island"))) {
                new CreateIsland(p.getLocation(), new File(plugin.getDataFolder() + File.separator + "islands" + File.separator + "Hardcore.schem"), p, plugin);
                p.closeInventory();
            }
        }
    }
}