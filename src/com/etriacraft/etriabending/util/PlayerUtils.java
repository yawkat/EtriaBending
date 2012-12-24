package com.etriacraft.etriabending.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerUtils {
	
	public static boolean teleport(Player p, Location loc) {
		return p.teleport(loc);
	}

}
