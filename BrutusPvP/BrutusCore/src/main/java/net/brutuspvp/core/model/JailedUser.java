package net.brutuspvp.core.model;

import java.util.UUID;

public class JailedUser {
	
	private UUID player;
	private String reason;
	private Jail jail;
	private UUID actor;
	
	
	public JailedUser(UUID player, UUID actor, String reason, Jail jail) {
		this.player = player;
		this.actor = actor;
		this.reason = reason;
		this.jail = jail;
	}
	public UUID getPlayer() {
		return player;
	}
	public String getReason() {
		return reason;
	}
	public Jail getJail() {
		return jail;
	}
	public UUID getActor() {
		return actor;
	}
}