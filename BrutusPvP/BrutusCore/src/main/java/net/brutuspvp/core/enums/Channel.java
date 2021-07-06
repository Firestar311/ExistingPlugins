package net.brutuspvp.core.enums;

import org.bukkit.ChatColor;

public enum Channel {
	
	GLOBAL(ChatColor.WHITE), STAFF(ChatColor.GREEN), TRADE(ChatColor.BLUE), LIVINGSPACE(ChatColor.YELLOW), 
	JAIL(ChatColor.AQUA), TRIAL(ChatColor.GOLD), TOWN(ChatColor.DARK_AQUA);
	
	
	private ChatColor color;
	
	private Channel(ChatColor color) {
		this.color = color;
	}
	
	public String getColorBold() {
		switch (color) {
		case AQUA: return "§b§l";
		case BLACK: return "§0§l";
		case BLUE: return "§9§l";
		case DARK_AQUA: return "§3§l";
		case DARK_BLUE: return "§1§l";
		case DARK_GRAY: return "§8§l";
		case DARK_GREEN: return "§2§l";
		case DARK_PURPLE: return "§5§l";
		case DARK_RED: return "§4§l";
		case GOLD: return "§6§l";
		case GRAY: return "§7§l";
		case GREEN: return "§a§l";
		case LIGHT_PURPLE: return "§d§l";
		case RED: return "§c§l";
		case WHITE: return "§f§l";
		case YELLOW: return "§e§l";
		default: return "";
		}
	}
	
	public ChatColor getColor() {
		return color;
	}
}