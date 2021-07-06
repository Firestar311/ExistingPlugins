package net.brutuspvp.core.model.abstraction;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.block.Sign;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import net.brutuspvp.core.enums.Type;

public abstract class LivingSpace {
	protected ProtectedCuboidRegion region;
	protected World world;
	protected String name = "";
	protected UUID owner = null;
	protected ArrayList<String> members = new ArrayList<String>();
	protected boolean available = true;
	protected int price = 0;
	protected long expires;
	protected Type type;
	
	protected ArrayList<Sign> signs = new ArrayList<Sign>();
	
	public LivingSpace(ProtectedCuboidRegion region, World world, String name, Type type) {
		this.region = region;
		this.world = world;
		this.name = name;
		this.type = type;
	}
	
	public LivingSpace(ProtectedCuboidRegion region, World world, String name, Type type, int price) {
		this.region = region;
		this.world = world;
		this.name = name;
		this.price = price;
		this.type = type;
	}
	
	public void addMember(String name) {
		members.add(name);
	}
	
	public void removeMember(String name) {
		members.add(name);
	}
	
	public boolean isMember(String name) {
		return members.contains(name);
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	public ArrayList<Sign> getSigns() {
		return new ArrayList<Sign>(signs);
	}
	
	public void addSign(Sign sign) {
		signs.add(sign);
	}
	
	public void removeSign(Sign sign) {
		signs.remove(sign);
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public ProtectedCuboidRegion getRegion() {
		return region;
	}
	
	public void setRegion(ProtectedCuboidRegion region) {
		this.region = region;
	}

	public World getWorld() {
		return world;
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getMembers() {
		return members;
	}
	
	public Type getType() {
		return type;
	}
	
	public abstract void updateSigns();
}