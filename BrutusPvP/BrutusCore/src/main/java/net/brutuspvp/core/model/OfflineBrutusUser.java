package net.brutuspvp.core.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.enums.Toggle;
import net.brutuspvp.core.model.abstraction.Account;

public class OfflineBrutusUser {

	protected static final BrutusCore plugin = BrutusCore.getInstance();

	private OfflinePlayer offlinePlayer;

	protected UUID uuid;

	protected File file;
	protected FileConfiguration config;

	protected Apartment apartment;
	protected House house;
	protected Mansion mansion;

	protected ArrayList<UUID> friends = new ArrayList<UUID>();

	protected HashMap<Toggle, Boolean> toggles = new HashMap<Toggle, Boolean>();

	protected PersonalAccount defaultAccount = null;

	protected List<String> sharedAccounts = new ArrayList<String>();

	public OfflineBrutusUser(OfflinePlayer player) {
		this.offlinePlayer = player;
		this.uuid = offlinePlayer.getUniqueId();

		file = new File(plugin.getUserFolder() + File.separator + uuid.toString() + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				BrutusCore.createBrutusError(e, "OfflineBrutusUser Constructor: " + player.getName());
			}
		}
		
		config = YamlConfiguration.loadConfiguration(file);

		this.toggles.put(Toggle.ADMINMODE, false);
		this.toggles.put(Toggle.EDITMODE, false);
		this.toggles.put(Toggle.FLY, false);
		this.toggles.put(Toggle.FRIEND_REQUEST, true);
		this.toggles.put(Toggle.GOD, false);
		this.toggles.put(Toggle.MESSAGES, true);
		this.toggles.put(Toggle.RENEWAL_APARTMENT, false);
		this.toggles.put(Toggle.RENEWAL_HOUSE, false);
		this.toggles.put(Toggle.RENEWAL_MANSION, false);
		this.toggles.put(Toggle.TELEPORTING, true);
		this.toggles.put(Toggle.VANISH_JOIN_LEAVE, false);

		if (config.contains("friends")) {
			List<String> friendsSet = config.getStringList("friends");
			if (friendsSet != null) {
				for (String u : friendsSet) {
					UUID uid = UUID.fromString(u);
					if (uid != null) {
						friends.add(uid);
					}
				}
			}
		}

		for (String t : config.getStringList("toggles")) {
			Toggle toggle = Toggle.valueOf(t);
			toggles.put(toggle, config.getBoolean("toggles." + t));
		}

		if (!config.contains("economy.sharedmain") || !config.getBoolean("economy.sharedmain")) {
			if (config.contains("economy.account")) {
				UUID owner = player.getUniqueId();
				double balance = config.getDouble("economy.account.balance");
				defaultAccount = new PersonalAccount(owner, balance);
			} else {
				defaultAccount = new PersonalAccount(player.getUniqueId());
			}
		} else {
			this.sharedAccounts = config.getStringList("economy.sharedname");
		}
		
		if (config.contains("economy.default")) {
			
		} else {
			
		}
	}

	public void setToggle(Toggle toggle, boolean value) {
		this.toggles.put(toggle, value);
	}

	public boolean getToggle(Toggle toggle) {
		return this.toggles.get(toggle);
	}

	public ArrayList<UUID> getFriends() {
		return new ArrayList<UUID>(friends);
	}

	public void addFriend(UUID uuid) {
		friends.add(uuid);
	}

	public void removeFriend(UUID uuid) {
		friends.remove(uuid);
	}

	public boolean isMainAccountShared() {
		return false;
	}

	public void setMainAccountShared(SharedAccount account) {

	}

	public void addSharedAccount(String name) {
		this.sharedAccounts.add(name);
	}

	public void removeSharedAccount(String name) {
		this.sharedAccounts.remove(name);
	}

	public boolean isSharedAccountMember(SharedAccount account) {
		if (this.sharedAccounts == null || this.sharedAccounts.isEmpty()) {
			return false;
		}

		return this.sharedAccounts.contains(account.getName());
	}

	public Account getAccount() {
		return defaultAccount;
	}

	public List<String> getSharedAccounts() {
		return new ArrayList<String>(sharedAccounts);
	}

	public boolean isOp() {
		return offlinePlayer.isOp();
	}

	public void setOp(boolean arg0) {
		offlinePlayer.setOp(arg0);
	}

	public Map<String, Object> serialize() {
		return offlinePlayer.serialize();
	}

	public Location getBedSpawnLocation() {
		return offlinePlayer.getBedSpawnLocation();
	}

	public long getFirstPlayed() {
		return offlinePlayer.getFirstPlayed();
	}

	public long getLastPlayed() {
		return offlinePlayer.getLastPlayed();
	}

	public String getName() {
		return offlinePlayer.getName();
	}

	public Player getPlayer() {
		return offlinePlayer.getPlayer();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public boolean hasPlayedBefore() {
		return offlinePlayer.hasPlayedBefore();
	}

	public boolean isBanned() {
		return offlinePlayer.isBanned();
	}

	public boolean isOnline() {
		return offlinePlayer.isOnline();
	}

	public boolean isWhitelisted() {
		return offlinePlayer.isWhitelisted();
	}

	public void setWhitelisted(boolean arg0) {
		offlinePlayer.setWhitelisted(arg0);
	}

	public OfflinePlayer getOfflinePlayer() {
		return this.offlinePlayer;
	}
}