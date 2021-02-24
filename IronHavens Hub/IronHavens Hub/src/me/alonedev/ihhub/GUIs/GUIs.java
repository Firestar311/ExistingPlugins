package me.alonedev.ihhub.GUIs;

import me.alonedev.ihhub.Util;
import me.mattstudios.mfgui.gui.guis.Gui;
import me.mattstudios.mfgui.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

public class GUIs {


        //Guis


        public static void socials(Player player){


            Gui socialsGUI = new Gui(3, "&aGenerator+");

            socialsGUI.setDefaultClickAction(event -> {
                event.setCancelled(true);
            });


            //GUI Items
            GuiItem discordGuiItem = new GuiItem(new ItemStack(Material.PLAYER_HEAD), event -> {

                //Discord player skull
                ItemStack is = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta sm = (SkullMeta) is.getItemMeta();
                sm.setOwner("Fossil360");
                sm.setLore(Arrays.asList("Click here to join our Discord!", " ", "Report bugs, engage with the community","and much more on our discord!"));
                sm.setDisplayName("Discord");
                is.setItemMeta(sm);

                //Click event
                if ((event.getWhoClicked() instanceof Player)) {
                    Player playerClicked = (Player) event.getWhoClicked();
                    Util.sendMsg("You Clicked stone!", playerClicked);
                }
            });
            GuiItem fuelGuiItem = new GuiItem(new ItemStack(Material.STONE), event -> {
                if ((event.getWhoClicked() instanceof Player)) {
                    Player playerClicked = (Player) event.getWhoClicked();
                    Util.sendMsg("You Clicked stone!", playerClicked);
                }
            });

            GuiItem typeGuiItem = new GuiItem(new ItemStack(Material.STONE), event -> {
                if ((event.getWhoClicked() instanceof Player)) {
                    Player playerClicked = (Player) event.getWhoClicked();
                    Util.sendMsg("You Clicked stone!", playerClicked);
                }
            });
            GuiItem fillerGuiItem = new GuiItem(new ItemStack(Material.STONE));


            socialsGUI.setItem(16, discordGuiItem);
            socialsGUI.setItem(13, fuelGuiItem);
            socialsGUI.setItem(10, typeGuiItem);
            socialsGUI.setItem(0, 0, fillerGuiItem);
            socialsGUI.open(player);
        }


}


