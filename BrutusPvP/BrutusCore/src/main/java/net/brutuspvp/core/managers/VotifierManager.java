package net.brutuspvp.core.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.firestar311.fireutils.classes.Utils;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import net.brutuspvp.core.BrutusCore;

@SuppressWarnings("unused")
public class VotifierManager implements CommandExecutor, Listener {

	private BrutusCore plugin;

	private File file;
	private FileConfiguration config;

	private HashMap<String, Vote> voteQueue = new HashMap<String, Vote>();

	private HashMap<UUID, ArrayList<Vote>> votes = new HashMap<UUID, ArrayList<Vote>>();

	public VotifierManager(BrutusCore plugin) {
		this.plugin = plugin;
		this.plugin.registerListener(this);

		file = Utils.createYamlFile(plugin, "votifier");
		config = Utils.createYamlConfig(plugin, file, "sites", "commands", "settings", "queue");
		
		this.loadData();

		// TODO File stuff
	}

	public void saveData() {
		for (Entry<UUID, ArrayList<Vote>> entry : votes.entrySet()) {
			UUID uuid = entry.getKey();
			ArrayList<Vote> playerVotes = entry.getValue();
			
			int counter = 0;
			
			for (Vote v : playerVotes) {
				config.set(uuid.toString() + "." + counter + ".address", v.getAddress());
				config.set(uuid.toString() + "." + counter + ".servicename", v.getServiceName());
				config.set(uuid.toString() + "." + counter + ".timestamp", v.getTimeStamp());
				counter++;
			}
			Utils.saveFile(file, config);
		}
	}

	private void loadData() {
		for (String u : config.getConfigurationSection("").getKeys(false)) {
			ArrayList<Vote> playerVotes = new ArrayList<Vote>();
			UUID uuid = null;
			try {
				uuid = UUID.fromString(u);
			} catch (IllegalArgumentException e) {
				
			}
			if (uuid == null) {
				continue;
			}
			for (String i : config.getConfigurationSection(u).getKeys(false)) {
				int voteNumber = Integer.parseInt(i);
				String address = config.getString(u + "." + voteNumber + ".address");
				String serviceName = config.getString(u + "." + voteNumber + ".servicename");
				String timeStamp = config.getString(u + "." + voteNumber + ".timestamp");
				
				Vote v = new Vote();
				v.setAddress(address);
				v.setServiceName(serviceName);
				v.setTimeStamp(timeStamp);
				v.setUsername(Bukkit.getOfflinePlayer(uuid).getName());
				playerVotes.add(v);
				this.votes.put(uuid, playerVotes);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerVote(VotifierEvent e) {
		Vote vote = e.getVote();
		Player player = Bukkit.getPlayer(vote.getUsername());
		if (player == null) {
			OfflinePlayer of = Bukkit.getOfflinePlayer(vote.getUsername());
			ArrayList<Vote> vs = votes.get(of.getUniqueId());
			if (vs == null) {
				vs = new ArrayList<Vote>();
			}
			vs.add(vote);
			votes.put(of.getUniqueId(), vs);
		}

		// plugin.getVaultEconomy().depositPlayer(player, 5);

		String message = "§a{name} has voted on {site} and will recieve 5 denari.";
		message = message.replace("{name}", player.getName());
		message = message.replace("{site}", vote.getServiceName());
		ArrayList<Vote> vs = votes.get(player.getUniqueId());
		if (vs == null) {
			vs = new ArrayList<Vote>();
		}
		vs.add(vote);
		votes.put(player.getUniqueId(), vs);

		Bukkit.broadcastMessage(message);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("vote")) {
			sender.sendMessage("§6-----------------------------------------------------");
			sender.sendMessage("§ahttp://minecraftservers.org/server/432883");
			sender.sendMessage("§ahttp://minecraft-server-list.com/server/396004/");
			sender.sendMessage("§ahttp://www.planetminecraft.com/server/brutuspvp-1-10/vote/");
			sender.sendMessage("§6-----------------------------------------------------");
		}

		return true;
	}
}