package me.alonedev.ironhhub.GUI;

import me.alonedev.ironhhub.IronHhub;
import me.alonedev.ironhhub.Utils.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
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
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class SocialsGUI implements CommandExecutor, Listener {

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
    public SocialsGUI() {
        SocialsGUI = Bukkit.createInventory(null, 27, ChatColor.translateAlternateColorCodes('&', "&bOur Socials!"));

        initializeItems();
    }


    public void initializeItems() {
        SocialsGUI.setItem(13,Util.createGuiItem(SocialsGUI, Material.DIAMOND_SWORD, ChatColor.translateAlternateColorCodes('&', "&8Our Discord"), 13,"§aJoin our discord to get support", "§aand hang out with our community!"));
        SocialsGUI.setItem(0,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(1,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 1));
        SocialsGUI.setItem(2,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 2));
        SocialsGUI.setItem(3,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 3));
        SocialsGUI.setItem(4,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 4));
        SocialsGUI.setItem(5,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 5));
        SocialsGUI.setItem(6,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 6));
        SocialsGUI.setItem(7,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 7));
        SocialsGUI.setItem(8,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 8));
        SocialsGUI.setItem(18,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(19,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(20,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(21,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(22,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(23,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(24,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(25,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
        SocialsGUI.setItem(26,Util.createGuiItem(SocialsGUI, Material.GRAY_STAINED_GLASS_PANE," ", 0));
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

        if (e.getSlot() == 13) {
            Util.sendMsg("You clicked discord", player);
            ItemStack SocialBook = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookMeta = (BookMeta) SocialBook.getItemMeta();

            BaseComponent[] page = new ComponentBuilder(Util.replaceColors("       &b&lClick here"))
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, IronHhub.discordLink))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Go join our discord!").create()))
                    .create();


            bookMeta.spigot().addPage(page);

            bookMeta.setTitle("Our Discord");
            bookMeta.setAuthor("Iron Havens");

            SocialBook.setItemMeta(bookMeta);

            player.openBook(SocialBook);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == SocialsGUI) {
            e.setCancelled(true);
        }
    }



}

