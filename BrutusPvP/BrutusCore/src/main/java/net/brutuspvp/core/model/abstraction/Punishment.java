package net.brutuspvp.core.model.abstraction;

import java.util.UUID;

import net.brutuspvp.core.enums.Type;

public abstract class Punishment {
	
	protected UUID player;
	protected UUID actor;
	protected String reason;
	protected Type type;
	protected long expire = 0;
	
	public Punishment(UUID player, UUID actor, String reason, Type type) {
		this.player = player;
		this.actor = actor;
		this.reason = reason;
		this.type = type;
	}
	
	public Punishment(UUID player, UUID actor, String reason, Type type, long expire) {
		this.player = player;
		this.actor = actor;
		this.reason = reason;
		this.type = type;
		this.expire = expire;
	}

	public UUID getPlayer() {
		return player;
	}

	public UUID getActor() {
		return actor;
	}

	public String getReason() {
		return reason;
	}

	public Type getType() {
		return type;
	}
	
	public long getExpire() {
		return expire;
	}
	
	public void setExpire(long expire) {
		this.expire = expire;
	}
}