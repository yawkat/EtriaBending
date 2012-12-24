package com.etriacraft.etriabending.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.etriacraft.etriabending.suites.BackpackSuite;

public class BackpackListeners implements Listener {
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (BackpackSuite.backpackusers.contains(e.getPlayer().getName()) && !BackpackSuite.snoopers.contains(e.getPlayer().getName())) {
			final String invTitle = e.getInventory().getTitle();
			if (!invTitle.equalsIgnoreCase("backpack") && invTitle.equalsIgnoreCase("satchel")) return; //Double Sure
			BackpackSuite.closePack(e.getPlayer().getName(), e.getInventory());
		}

		// Closed Inventory, snoopers can click again!
		if (BackpackSuite.snoopers.contains(e.getPlayer().getName())) {
			BackpackSuite.snoopers.remove(e.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (BackpackSuite.backpackusers.contains(e.getPlayer().getName())) {
			final String invTitle = e.getPlayer().getOpenInventory().getTitle();
			if (!invTitle.equalsIgnoreCase("backpack") && !invTitle.equalsIgnoreCase("satchel")) return;
			BackpackSuite.closePack(e.getPlayer().getName(), e.getPlayer().getOpenInventory().getTopInventory());
		}

		if (BackpackSuite.snoopers.contains(e.getPlayer().getName())) {
			BackpackSuite.snoopers.remove(e.getPlayer().getName());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		final Player p = e.getEntity();

		if (BackpackSuite.backpackusers.contains(p.getName())) {
			final String invTitle = p.getOpenInventory().getTitle();
			if (!invTitle.equalsIgnoreCase("backpack") && !invTitle.equalsIgnoreCase("satchel")) return;
			BackpackSuite.closePack(e.getEntity().getName(), p.getOpenInventory().getTopInventory());
		}

		if (BackpackSuite.snoopers.contains(p.getName())) {
			BackpackSuite.snoopers.remove(p.getName());
		}

		if (!BackpackSuite.getPackIsProtected(p)) {
			final ItemStack[] iss = BackpackSuite.backpacks.get(p.getName());
			for (int i = 0; i < iss.length; i++) {
				if (iss[i] == null) continue;
				p.getWorld().dropItemNaturally(p.getLocation(), iss[i]);
			}

			BackpackSuite.clearPack(p.getName());
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

		if (BackpackSuite.snoopers.contains(e.getWhoClicked().getName())) {
			e.setCancelled(true);
		}
	}
	

}
