package net.brutuspvp.core.model;

import java.util.UUID;

import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.Punishment;

public class IPBan extends Punishment {
	
	private String address = "";
	
	public IPBan(UUID player, UUID actor, String reason, Type type, String address) {
		super(player, actor, reason, type);
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
}