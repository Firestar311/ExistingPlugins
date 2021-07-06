package net.brutuspvp.core.model;

import net.brutuspvp.core.enums.Type;

public class Evidence {
	private String name;
	private Type type;
	private String link;
	
	public Evidence(String name, Type type, String link) {
		this.name = name;
		this.type = type;
		this.link = link;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getLink() {
		return link;
	}
}