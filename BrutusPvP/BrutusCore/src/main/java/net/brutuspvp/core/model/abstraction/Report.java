package net.brutuspvp.core.model.abstraction;

import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Bukkit;

import net.brutuspvp.core.enums.Status;
import net.brutuspvp.core.enums.Type;

public abstract class Report {
	protected int id = 0;
	protected UUID submitter;
	protected UUID assignee;
	protected String description;
	protected Status status;
	protected Type type;
	//TODO add a comments list
	
	private static TreeSet<Integer> usedIds = new TreeSet<Integer>();
	
	protected Report(UUID submitter, String description, Type type) {
		this.submitter = submitter;
		this.description = description;
		this.type = type;
		this.id = usedIds.size() + 1;
		usedIds.add(id);
	}

	public UUID getAssignee() {
		return assignee;
	}

	public void setAssignee(UUID assignee) {
		this.assignee = assignee;
	}

	public Status getStatus() {
		return status;
	}
	
	public Type getType() {
		return type;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public UUID getSubmitter() {
		return submitter;
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		String format = "§e[id] - submitter | assignee | description | status";
		format = format.replace("id", id + "");
		format = format.replace("submitter", Bukkit.getOfflinePlayer(submitter).getName());
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