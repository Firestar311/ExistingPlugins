package net.brutuspvp.core.model;

import java.util.UUID;

public class FriendRequest {
	
	private UUID requester;
	private UUID toadd;
	private int timeout = 60;
	
	public FriendRequest(UUID requester, UUID toadd) {
		this.requester = requester;
		this.toadd = toadd;
	}
	
	public UUID getRequester() {
		return requester;
	}
	
	public UUID getToAdd() {
		return toadd;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((requester == null) ? 0 : requester.hashCode());
		result = prime * result + ((toadd == null) ? 0 : toadd.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FriendRequest other = (FriendRequest) obj;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		if (toadd == null) {
			if (other.toadd != null)
				return false;
		} else if (!toadd.equals(other.toadd))
			return false;
		return true;
	}
}