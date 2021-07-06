package net.brutuspvp.core.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.LivingSpace;

public class Apartment extends LivingSpace {
	//TODO private Chest chest;
	//TODO private Location workbench;
	
	public Apartment(String name, World world, ProtectedCuboidRegion area) {
		super(area, world, name, Type.APARTMENT);
	}
	
	public Apartment(String name, World world, ProtectedCuboidRegion area, int price) {
		super(area, world, name, Type.APARTMENT, price);
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
}