package me.alonedev.ihhub.Utils;

import me.alonedev.ihhub.IHhub;
import me.alonedev.ihhub.Managers.BookManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiUtils {

    public static IHhub main;

    public GuiUtils(IHhub main) {
        this.main = main;
    }

    public ItemStack createGuiItem(Inventory GUI, final Material material, final String name, final int slot, final ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(lore);
        item.setItemMeta(meta);
        GUI.setItem(slot, item);
        return item;
    }

    public void generateItems(Inventory GUI, String gui) {
        List<String> guiItems = main.getConfig().getStringList("GUIS." + gui);
        for (String item : guiItems) {
            String[] items = item.split(";");
            String name = items[0];
            Material material = Material.valueOf(items[1]);
            int slot = Integer.parseInt(items[2]);
            String[] lores = items[3].split(",");
            ArrayList<String> formatLore = new ArrayList<String>();
            for (String lore : lores) {
                formatLore.add(org.bukkit.ChatColor.translateAlternateColorCodes('&', lore));
            }

            GUI.setItem(slot, createGuiItem(GUI, material, org.bukkit.ChatColor.translateAlternateColorCodes('&', name), slot, formatLore));
        }
    }

    public void generateBooks(String gui) {
        main.getConfig().getConfigurationSection("Books." + gui).getKeys(false).forEach(book -> {

            String title = main.getConfig().getString("Books." + gui + "." + book + ".Title");
            String author = main.getConfig().getString("Books." + gui + "." + book + ".Author");
            String link = main.getConfig().getString("Books." + gui + "." + book + ".Link");
            String hoverText = main.getConfig().getString("Books." + gui + "." + book + ".HoverText");
            String clickText = main.getConfig().getString("Books." + gui + "." + book + ".ClickText");
            int slot = main.getConfig().getInt("Books." + gui + "." + book + ".SlotToOpen");

            BookManager bookManager = new BookManager(title, author, link, clickText, hoverText);
            BookManager.addBook(slot, bookManager);

        });
    }

    public ItemStack createBook(int id) {
        BookManager bookManager = BookManager.getBook(id);

        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        BaseComponent[] page = new ComponentBuilder(Util.replaceColors(bookManager.getClickText()))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, bookManager.getLink()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Util.replaceColors(bookManager.getHoverText())).create()))
                .create();


        bookMeta.spigot().addPage(page);

        bookMeta.setTitle(bookManager.getTitle());
        bookMeta.setAuthor(bookManager.getAuthor());

        book.setItemMeta(bookMeta);
        return book;
    }

}
