package com.etriacraft.etriabending.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.etriacraft.etriabending.EtriaBending;

public class BlockListener implements Listener {
	
	EtriaBending plugin;
	public BlockListener(EtriaBending instance) {
		this.plugin = instance;
	}
	
	@EventHandler
	public void onSignChance(SignChangeEvent e) {
		e.setLine(0, e.getLine(0).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
		e.setLine(1, e.getLine(1).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
		e.setLine(2, e.getLine(2).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
		e.setLine(3, e.getLine(3).replaceAll("&([0-9A-Fa-f])", "\u00A7$1"));
	}
	
	@EventHandler
	public void onPlayerBuild(BlockPlaceEvent e) {
		if (plugin.getConfig().getStringList("NoBuild").contains(e.getPlayer().getWorld().getName())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerBreak(BlockBreakEvent e) {
		if (plugin.getConfig().getStringList("NoBuild").contains(e.getPlayer().getWorld().getName())) {
			e.setCancelled(true);
		}
	}

}
