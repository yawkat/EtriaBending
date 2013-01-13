package com.etriacraft.etriabending.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class BlockListener implements Listener {
	
	@EventHandler
	public void onSignChance(SignChangeEvent e) {
		e.setLine(0, e.getLine(0).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
		e.setLine(1, e.getLine(1).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
		e.setLine(2, e.getLine(2).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
		e.setLine(3, e.getLine(3).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
	}

}
