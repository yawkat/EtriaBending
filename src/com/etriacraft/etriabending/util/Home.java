package com.etriacraft.etriabending.util;

import org.bukkit.Location;
import org.bukkit.World;

public class Home {
	
	private Location loc;
	private String owner;
	private String name;
	
	public Home(Location loc, String owner, String name) {
		this.loc = loc;
		this.owner = owner;
		this.name = name;
	}
	
	public Home(World w, int x, int y, int z, float pitch, float yaw, String owner, String name) {
		this(new Location(w, x, y, z, yaw, pitch), owner, name);
	}
	
	public Location getLocation() {
		return this.loc;
	}
	
	public String getOwner() {
		return this.owner;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}

}
