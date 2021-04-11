package me.alonedev.ihhub.GUIS;

import me.alonedev.ihhub.IHhub;
import me.alonedev.ihhub.Managers.BookManager;
import me.alonedev.ihhub.Utils.GuiUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SocialsGUI implements CommandExecutor, Listener {

    private static IHhub main;
    private static GuiUtils gui;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            openInventory(player);

        }
        return true;
    }

    private static Inventory SocialsGUI;

    //Socials GUI
    public SocialsGUI(IHhub main) {
        SocialsGUI = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&bOur Socials!"));
        this.main = main;
        this.gui = new GuiUtils(main);
        gui.generateItems(SocialsGUI, "SocialsGUI");
        gui.generateBooks("SocialsGUI");

    }

    public static void openInventory(final HumanEntity ent) {
        ent.openInventory(SocialsGUI);
    }


    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != SocialsGUI) return;
        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        final Player player = (Player) e.getWhoClicked();

        if (BookManager.getBook(e.getSlot()) !=null) {
            player.openBook(gui.createBook(e.getSlot()));
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == SocialsGUI) {
            e.setCancelled(true);
        }
    }



}
