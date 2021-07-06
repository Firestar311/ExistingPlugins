package net.brutuspvp.core.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.LivingSpace;

public class Mansion extends LivingSpace {
	
	private EnderChest enderchest;
	protected Location craftingTable;
	protected Furnace furnace;
	
	protected ArrayList<Chest> chests = new ArrayList<Chest>();
	
	public Mansion(ProtectedCuboidRegion area, World world, String name) {
		super(area, world, name, Type.MANSION);
	}
	
	public Mansion(ProtectedCuboidRegion area, World world, String name, int price) {
		super(area, world, name, Type.MANSION, price);
	}
	
	public Location getCraftingTable() {
		return craftingTable;
	}

	public void setCraftingTable(Location craftingTable) {
		this.craftingTable = craftingTable;
	}

	public ArrayList<Chest> getChests() {
		return new ArrayList<Chest>(chests);
	}
	
	public void addChest(Chest chest) {
		this.chests.add(chest);
	}
	
	public void removeChest(Chest chest) {
		this.chests.remove(chest);
	}

	public Furnace getFurnace() {
		return furnace;
	}

	public void setFurnace(Furnace furnace) {
		this.furnace = furnace;
	}

	public void updateSigns() {
		SimpleDateFormat sdf = null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(expires);
		String DATE_FORMAT = "MM/dd/yy h:mm a";
		sdf = new SimpleDateFormat(DATE_FORMAT);
		for(Sign sign : signs) {
			sign.setLine(0, name);
			if (available) {
				sign.setLine(1, "§aAvailable");
			} else {
				sign.setLine(1, "§c" + owner);
			}
			if (available) {
				sign.setLine(2, "§e$" + price);
			} else {
				sign.setLine(2, "§7Until:");
			}
			if (available) {
				sign.setLine(3, "Rent for: " + BrutusCore.getInstance().settings().getRentDays() + "days");
			} else {
				sign.setLine(3, sdf.format(cal.getTime()));
			}
			BrutusCore.addSignChange(sign);
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendSignChange(sign.getLocation(), sign.getLines());
			}
		}
	}
	
	public void setEnderChest(EnderChest enderchest) {
		this.enderchest = enderchest;
	}
	
	public EnderChest getEnderChest() {
		return this.enderchest;
	}
}