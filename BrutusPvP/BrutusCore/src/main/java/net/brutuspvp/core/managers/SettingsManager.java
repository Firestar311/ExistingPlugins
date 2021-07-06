package net.brutuspvp.core.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.firestar311.fireutils.classes.Utils;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Variables;

public class SettingsManager {

	private File file;
	private FileConfiguration config;
	private BrutusCore plugin;
	private UUID serverUUID = null;
	
	public SettingsManager(BrutusCore passedPlugin) {
		plugin = passedPlugin;
		file = Utils.createYamlFile(plugin, "config");
		config = Utils.createYamlConfig(plugin, file, "messages", "core", "housing", "motd");
		if (!config.contains("serveruuid")) {
			this.serverUUID = UUID.randomUUID();
			this.config.set("serveruuid", serverUUID.toString());
			plugin.saveConfig();
		} else {
			this.serverUUID = UUID.fromString(config.getString("serveruuid"));
		}
	}
	
	public UUID getServerUUID() {
		return this.serverUUID;
	}
	
	public int getErrors() {
		return config.getInt("core.errors");
	}
	
	public void setErrors(int errors) {
		config.set("core.errors", errors);
		Utils.saveFile(plugin, file, config);
	}
	
	public List<String> getNormalMOTD() {
		List<String> lines = new ArrayList<String>();
		for(String l : config.getStringList("motd.normal")) {
			lines.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		return lines;
	}
	
	public List<String> getStaffMOTD() {
		List<String> lines = new ArrayList<String>();
		for(String l : config.getStringList("motd.staff")) {
			lines.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		return lines;
	}
	
	public List<String> getImperialMOTD() {
		List<String> lines = new ArrayList<String>();
		for(String l : config.getStringList("motd.imperial")) {
			lines.add(ChatColor.translateAlternateColorCodes('&', l));
		}
		return lines;
	}
	
	public int getRentDays() {
		return config.getInt("housing.rentdays");
	}
	
	public long getSaveInterval() {
		return config.getLong("housing.saveinterval")*60*60;
	}
	
	public String getChatFormat(Player player, String message) {
		String chatFormat = "";
		if(config.contains("chat.formatting.groupformatting")) {
			String playerGroup = plugin.getPermission().getPrimaryGroup(player);
			
			Set<String> chatGroups = config.getConfigurationSection("chat.formatting.groupformatting").getKeys(false);
			if(chatGroups.contains(playerGroup)) {
				chatFormat = config.getString("chat.formatting.groupformatting." + playerGroup);
			}
		}
		if (chatFormat == "") {
			chatFormat = plugin.getConfig().getString("chat.formatting.global");
		}
		chatFormat = chatFormat.replace(Variables.PLAYER_DISPLAYNAME, player.getDisplayName());
		chatFormat = chatFormat.replace(Variables.MESSAGE, message);
		return ChatColor.translateAlternateColorCodes('&', chatFormat);
	}
	
// Configurable Messages
	public String getNoPermissionMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.nopermission"));
	}
	
	public String getPMSenderMessage(String sender, String target, String message) {
		String format = config.getString("messages.pmsender").replace(Variables.SENDER, sender)
				.replace(Variables.TARGET, target)
				.replace(Variables.MESSAGE, message);
		return ChatColor.translateAlternateColorCodes('&', format);
	}
	
	public String getPMTargetMessage(String sender, String target, String message) {
		String format = config.getString("messages.pmtarget").replace(Variables.SENDER, sender)
				.replace(Variables.TARGET, target)
				.replace(Variables.MESSAGE, message);
		return ChatColor.translateAlternateColorCodes('&', format);
	}
	
	public String getPMOfflineMessage(String target) {
		String format = config.getString("messages.pmoffline").replace(Variables.TARGET, target);
		return ChatColor.translateAlternateColorCodes('&', format);
	}
	
	public String getChatDisabledMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.chatdisabled"));
	}
	
	public String getActorEnableChatMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.enablechatactor"));
	}
	
	public String getActorDisableChatMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.disablechatactor"));
	}
	
	public String getAllEnableChatMessage(String actor) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.enablechatall")
				.replace(Variables.ACTOR, actor));
	}
	
	public String getAllDisableChatMessage(String actor) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.disablechatall")
				.replace(Variables.ACTOR, actor));
	}
	
	public String getPlayerOnlyCommandMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.playeronlycommand"));
	}
	
	public String getVanishNoPlaceBreakBlocksMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.vanishnoplaceorbreak"));
	}
	
	public String getVanishNoChatMessage() {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.vanishchatdisabled"));
	}
	
	public String getPunishMessage(String target, String action, String actor) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.punishmessage")
				.replace(Variables.TARGET, target)
				.replace(Variables.ACTION, action)
				.replace(Variables.ACTOR, actor));
	}
	
	public String getUnpunishMessage(String target, String action, String actor) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.unpunishmessage")
				.replace(Variables.TARGET, target)
				.replace(Variables.ACTION, action)
				.replace(Variables.ACTOR, actor));
	}
	
	public String getPunishMessage(String target, String action, String actor, String reason) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.punishmessagereason")
				.replace(Variables.TARGET, target)
				.replace(Variables.ACTION, action)
				.replace(Variables.ACTOR, actor)
				.replace(Variables.REASON, reason));
	}
	
	public String getUnpunishMessage(String target, String action, String actor, String reason) {
		return ChatColor.translateAlternateColorCodes('&', config.getString("messages.punishmessagereason")
				.replace(Variables.TARGET, target)
				.replace(Variables.ACTION, action)
				.replace(Variables.ACTOR, actor)
				.replace(Variables.REASON, reason));
	}
}