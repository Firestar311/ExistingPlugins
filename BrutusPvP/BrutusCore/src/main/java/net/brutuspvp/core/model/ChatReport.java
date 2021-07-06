package net.brutuspvp.core.model;

import java.util.UUID;

import org.bukkit.Bukkit;

import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.Report;

public class ChatReport extends Report {

	private UUID accused;
	//TODO Have a list of the 10 most recent chat messages of the accused

	public ChatReport(UUID submitter, String description, UUID accused) {
		super(submitter, description, Type.CHAT);
		this.accused = accused;
	}

	public UUID getAccused() {
		return accused;
	}
	
	@Override
	public String toString() {
		String format = "§e[id] - submitter | accused | assignee | description | status";
		format = format.replace("id", id + "");
		format = format.replace("submitter", Bukkit.getOfflinePlayer(submitter).getName());
		format = format.replace("accused", Bukkit.getOfflinePlayer(accused).getName());
		if(assignee != null) {
			format = format.replace("assignee", Bukkit.getOfflinePlayer(assignee).getName());
		} else {
			format = format.replace("assignee", "NONE");
		}
		format = format.replace("description", description);
		format = format.replaceAll("status", status.toString());
		
		return format;
	}
}