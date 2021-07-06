package net.brutuspvp.core.model;

import java.util.UUID;

import org.bukkit.Location;

import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.Report;

public class TicketReport extends Report {
	
	private Location location;
	
	public TicketReport(UUID submitter, String description, Location location) {
		super(submitter, description, Type.TICKET);
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
}