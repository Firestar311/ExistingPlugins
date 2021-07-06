package net.brutuspvp.core.model;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.firestar311.fireutils.classes.Utils;

import net.brutuspvp.core.enums.Channel;
import net.brutuspvp.core.model.abstraction.LivingSpace;
import net.md_5.bungee.api.chat.TextComponent;

public class BrutusUser extends OfflineBrutusUser {

	private Player player;
	private Channel channel = Channel.GLOBAL;
	
	private ArrayList<Channel> hiddenchatchannels = new ArrayList<Channel>();
	private LivingSpace confirmBuy;

	public BrutusUser(Player player) {
		super(player);
		this.player = player;
	}
	
	public BrutusUser(OfflineBrutusUser offlineUser) {
		super(offlineUser.getOfflinePlayer());
		this.player = offlineUser.getOfflinePlayer().getPlayer();
	}
	
	public void sendMessage(String msg) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}

	public Location getLocation() {
		return player.getLocation();
	}
	
	public String getDisplayName() {
		return player.getDisplayName();
	}

	public boolean isBuying() {
		return (confirmBuy != null);
	}

	public void setBuying(LivingSpace space) {
		this.confirmBuy = space;
	}

	public void removeBuying() {
		this.confirmBuy = null;
	}

	public LivingSpace getBuying() {
		return this.confirmBuy;
	}

	public void removeHiddenChannel(Channel channel) {
		hiddenchatchannels.remove(channel);
	}

	public boolean isHiddenChannel(Channel channel) {
		return hiddenchatchannels.contains(channel);
	}

	public void addHiddenChannel(Channel channel) {
		hiddenchatchannels.add(channel);
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void saveUserData() {
		ArrayList<String> fds = new ArrayList<String>();
		friends.forEach(uuid -> fds.add(uuid.toString()));
		config.set("friends", fds);

		toggles.forEach((toggle, value) -> config.set("toggles." + toggle.toString(), value.toString()));
		
		if (sharedAccounts != null || !sharedAccounts.isEmpty()) {
			config.set("economy.sharedname", sharedAccounts);
		}
		
		if (defaultAccount != null) {
			config.set("economy.account.balance", defaultAccount.getBalance());
		}
		
		Utils.saveFile(file, config);
	}

	public void sendMessage(TextComponent component) {
		player.spigot().sendMessage(component);
	}
}