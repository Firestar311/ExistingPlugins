package com.craftyun83.ironhavensb.island;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.craftyun83.ironhavensb.Main;
import com.craftyun83.ironhavensb.guis.InviteGUI;
import org.bukkit.inventory.ItemStack;

public class InviteIsland {

	public InviteIsland(Player p, Main plugin) {

		try {

			new AnvilGUI.Builder()
					.onComplete((player, text) -> {                             //called when the inventory output slot is clicked
						if(Bukkit.getPlayer(text) == null) {
							player.sendMessage(ChatColor.RED+"This player is not online");
							return AnvilGUI.Response.close();
						} else {

							new InviteGUI(Bukkit.getPlayer(text), p, plugin);
							return AnvilGUI.Response.close();

						}

					})
					.text("<Player>")
					.title("Enter your Player")
					.itemLeft(new ItemStack(Material.PAPER))//set the title of the GUI (only works in 1.14+)
					.plugin(plugin)                                   //set the plugin instance
					.open(p);

		} catch (Exception exc) {

			p.sendMessage(ChatColor.GREEN+"[Iron Haven] >> Sent island invite!");

		}

	}

}
