package net.brutuspvp.core.model;

import net.brutuspvp.core.enums.Type;

public class Job {
	
	private String name;
	private Type type;
	private String description;
	private double earnings;
	
	public Job(String name, String description, double earnings, Type type) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.earnings = earnings;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public double getEarnings() {
		return earnings;
	}
}